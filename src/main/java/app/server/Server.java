package app.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Classe représetant le server avec lequel le client communique pour recupérer les Informations dont il a besoin
 */
public class Server {

    /**
     * Nombres de threads par default utilsés par le server
     */
    private static final int DEFAULT_POOL_SIZE = 10; // Totalement abitraire pour l'instant

    /**
     * Nombres de connexions simultanées que le server gère 
     */
    private static final int MAX_INCOMMING_CONNECTION = 3; // Totalement abitraire pour l'instant

    /**
     * Nombres de secondes laissées aux threads lancés pour se terminer avant la fermeture de tous les threads
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

    private ServerConsole serverConsole;

    private Thread consoleThread;

    /**
     * 
     * @param host                   Nom de l'adresse sur laquelle le server doit etre lié
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @param withConsole            Determine si l'entrée standart doit etre ecoutée
     * @param maxIncommingConnection Nombre de connexions simultanées que le server peut gérer 
     * @param poolSize               Nombre de threads que le server peut utiliser
     * @throws UnknownHostException  si aucune adresse pour le {@code host} ne pouvait etre trouvée
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String host, int port, boolean withConsole, int maxIncommingConnection, int poolSize) throws UnknownHostException, IOException {
        this.isRunning = false;
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.serverSocket = new ServerSocket(port, Math.abs(maxIncommingConnection), InetAddress.getByName(host));
        this.serverConsole = withConsole ? new ServerConsole(): null;
        this.consoleThread = withConsole ? new Thread(serverConsole): null;
    }

    /**
     * 
     * @param host                   Nom de l'adresse sur laquelle le server doit etre lié
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @param withConsole            Determine si l'entrée standart doit etre ecoutée
     * @param maxIncommingConnection Nombre de connexions simultanées que le server peut gérer 
     * @throws UnknownHostException  si aucune adresse pour le {@code host} ne pouvait etre trouvée
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String host, int port, boolean withConsole, int maxIncommingConnection) throws UnknownHostException, IOException {
        this(host, port, withConsole, maxIncommingConnection, DEFAULT_POOL_SIZE);
    }

    /**
     * 
     * @param host                   Nom de l'adresse sur laquelle le server doit etre lié
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @param withConsole            Determine si l'entrée standart doit etre ecoutée
     * @throws UnknownHostException  si aucune adresse pour le {@code host} ne pouvait etre trouvée
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String host, int port, boolean withConsole) throws UnknownHostException, IOException {
        this(host, port, withConsole, MAX_INCOMMING_CONNECTION);
    }

    /**
     * 
     * @param host                   Nom de l'adresse sur laquelle le server doit etre lié
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @throws UnknownHostException  si aucune adresse pour le {@code host} ne pouvait etre trouvée
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String host, int port) throws UnknownHostException, IOException {
        this(host, port, false, MAX_INCOMMING_CONNECTION);
    }

    /**
     * Demarre le server
     */
    public void start() {
        isRunning = true;
        startConsole();
        while ( isRunning() ) {
            try {
                Socket clientSocket = serverSocket.accept();
                RequestHandler requestHandler = new RequestHandler(clientSocket);
                threadPool.execute(requestHandler);
            } catch (SocketTimeoutException e) {
                System.out.println("Erreur timeout");
            } catch (IOException e) {
                System.out.println("Erreur ioexception");
            }
        }

        tearDown();
    }

    private void startConsole() {
        if (consoleThread != null) {
            consoleThread.start();
        }
    }

    private void stopConsole() throws InterruptedException {
        if (consoleThread != null)
         consoleThread.join(AWAIT_TIME_BEFORE_DYING);

    }

    /**
     * Arrete le server
     */
    void stop() throws IOException {
        serverSocket.close();
        isRunning = false;
    }

    /**
     * 
     * @return si the server est en train de tourner
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Termine tous les threads en cours
     */
    private void tearDown() {
        try {
            stopConsole();
            if (!threadPool.awaitTermination(AWAIT_TIME_BEFORE_DYING, TimeUnit.SECONDS) ) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            consoleThread.interrupt();
        }
    }
}


