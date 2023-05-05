package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import server.map.Plan;
import server.map.PlanParser;
import server.map.PlanParser.InconsistentDataException;
import server.map.PlanParser.IncorrectFileFormatException;
import util.Logger;


/**
 * Classe représetant le server avec lequel le client communique pour recupérer les Informations
 * dont il a besoin
 */
public class Server {

    /**
     * Nombres de threads par default utilsés par le server
     */
    private static final int DEFAULT_POOL_SIZE = 10; // Totalement abitraire pour l'instant

    /**
     * Nombres de connexions simultanées que le server gère.
     */
    public static final int DEFAULT_BACKLOG = 50;

    /**
     * Nombres de secondes laissées aux threads lancés pour se terminer avant la fermeture de tous
     * les threads
     */
    private static final long AWAIT_TIME_BEFORE_DYING = 5; // Totalement abitraire pour l'instant

    /**
     * Le socket du server
     */
    private ServerSocket serverSocket;

    /**
     * Determine si le server est toujours en train de tourner, et donc à accepter de connexions
     */
    private boolean isRunning;

    /**
     * Ensemble des threads que le server dispose pour gérer les réponses à envoyer
     */
    private final ExecutorService threadPool;

    /**
     * La console du server
     */
    private ServerConsole serverConsole;

    /**
     * Le thread de {@code serverConsole}
     */
    private Thread consoleThread;

    /**
     * Instance du plan utilisée par le server
     */
    private Plan plan;

    /**
     * liste des clients
     */
    private List<Socket> clients;


    /**
     * 
     * @param plan                   Plan du reseau
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @param withConsole            Determine si l'entrée standart doit etre ecoutée
     * @param maxIncommingConnection Nombre de connexions simultanées que le server peut gérer 
     * @param poolSize               Nombre de threads que le server peut utiliser
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    private Server(Plan plan, int port, boolean withConsole, int maxIncommingConnection, int poolSize) throws IOException {
        this.isRunning = false;
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.serverSocket = new ServerSocket(port, maxIncommingConnection);
        this.serverConsole = withConsole ? new ServerConsole(this): null;
        this.consoleThread = withConsole ? new Thread(serverConsole): null;
        this.plan = plan;
        this.clients = new ArrayList<>();
    }

    /**
     * 
     * @param csvMapPath             chemin vers le ficher csv contenant les stations
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @param withConsole            Determine si l'entrée standart doit etre ecoutée
     * @param maxIncommingConnection Nombre de connexions simultanées que le server peut gérer 
     * @param poolSize               Nombre de threads que le server peut utiliser
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String csvMapPath, int port, boolean withConsole, int maxIncommingConnection, int poolSize) throws IOException, 
        IncorrectFileFormatException, IllegalArgumentException {
            this( PlanParser.planFromSectionCSV(csvMapPath), port, withConsole, maxIncommingConnection, poolSize);
    }

    /**
     * 
     * @param csvMapPath             chemin vers le ficher csv contenant les stations
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @param withConsole            Determine si l'entrée standart doit etre ecoutée
     * @param maxIncommingConnection Nombre de connexions simultanées que le server peut gérer 
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String csvMapPath, int port, boolean withConsole, int maxIncommingConnection) throws IOException, 
        IncorrectFileFormatException, IllegalArgumentException {
        this(csvMapPath, port, withConsole, maxIncommingConnection, DEFAULT_POOL_SIZE);
    }

    /**
     * 
     * @param csvMapPath             chemin vers le ficher csv contenant les stations
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @param withConsole            Determine si l'entrée standart doit etre ecoutée
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String csvMapPath, int port, boolean withConsole) throws IOException, 
        IncorrectFileFormatException, IllegalArgumentException {
        this(csvMapPath, port, withConsole, DEFAULT_BACKLOG);
    }

    /**
     *
     * @param csvMapPath chemin vers le ficher csv contenant les stations
     * @param port       Numero du port sur lequel le server doit etre lié
     * @throws UnknownHostException si aucune adresse pour le {@code host} ne pouvait etre trouvée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du
     *         socket
     */
    public Server(String csvMapPath, int port) throws IOException, 
        IncorrectFileFormatException, IllegalArgumentException {
        this(csvMapPath, port, false, DEFAULT_BACKLOG);
    }

    /**
     * Demarre le server
     */
    public void start() {
        isRunning = true;
        startConsole();
        while ( isRunning ) {
            try {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                ClientHandler requestHandler = new ClientHandler(this, clientSocket);
                threadPool.execute(requestHandler);
            } catch (SocketTimeoutException e) {
                Logger.info("timeout");
            } catch (IOException e) {
                Logger.info("ioexception");
            }
            removeCloseClientSocket();
        }

        try {
            stop();
        } catch (IOException e) {
            Logger.error("Impossible de fermer le socket");
        }
    }

    private void startConsole() {
        if (consoleThread != null) {
            consoleThread.start();
        }
    }

    private void stopConsole() throws InterruptedException {
        if (consoleThread != null) {
            serverConsole.stop();
            consoleThread.join(AWAIT_TIME_BEFORE_DYING);
        }
    }

    /**
     * Arrete le server
     */
    public void stop() throws IOException {

        tearDown();
        isRunning = false;
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    /**
     *
     * @return si the server est en train de tourner
     */
    public boolean isRunning() {
        return isRunning;
    }

    public ServerConsole getServerConsole() {
        return serverConsole;
    }

    /**
     * Termine tous les threads en cours
     */
    private void tearDown() {
        try {
            closeSockets();
            stopConsole();
            if (!threadPool.awaitTermination(AWAIT_TIME_BEFORE_DYING, TimeUnit.SECONDS) ) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            consoleThread.interrupt();
        }
    }

    private void removeCloseClientSocket() {
        clients.removeIf(Socket::isClosed);
    }

    /**
     * Ferme tous les sockets des clients
     */
    private void closeSockets() {
        for (Socket socket : clients) {
            try {
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException e) {
                Logger.error("Arrive lors de la fermeture d'un socket");
            }
        }
    }

    public synchronized Plan getPlan() {
        return plan;
    }

    /**
     * Met à jour le plan du server
     * @param newPlan le nouveau plan
     */
    private synchronized void setPlan(Plan newPlan) {
        plan = newPlan;
    }

    public void updateMap(String pathMapFile) throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan p = PlanParser.planFromSectionCSV(pathMapFile);
        setPlan(p);
    }

    /**
     * Met à jour le time du plan du server
     * @param pathTimeFile chemin vers le ficher de temps
     * @throws InconsistentDataException
     * @throws IncorrectFileFormatException
     * @throws FileNotFoundException
     */
    public void updateTime(String pathTimeFile) throws FileNotFoundException, IncorrectFileFormatException, InconsistentDataException {
       Plan p = getPlan().resetLinesSections();
       PlanParser.addTimeFromCSV(p, pathTimeFile);
       setPlan(p);
    }
}
