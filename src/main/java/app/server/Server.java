package app.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import app.map.Plan;
import app.map.PlanParser;
import app.map.PlanParser.IncorrectFileFormatException;


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

    private SynchronizedPlan synchronizedPlan;


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
    public Server(Plan p, int port, boolean withConsole, int maxIncommingConnection, int poolSize) throws UnknownHostException, IOException {
        this.isRunning = false;
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.serverSocket = new ServerSocket(port, Math.abs(maxIncommingConnection));
        this.serverConsole = withConsole ? new ServerConsole(this): null;
        this.consoleThread = withConsole ? new Thread(serverConsole): null;
        synchronizedPlan = new SynchronizedPlan(p);

    }

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
    public Server(String csvMapPath, int port, boolean withConsole, int maxIncommingConnection, int poolSize) throws UnknownHostException, IOException, 
        FileNotFoundException, IncorrectFileFormatException, IllegalArgumentException {
            this( PlanParser.planFromSectionCSV(csvMapPath), port, withConsole, maxIncommingConnection, poolSize);
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
    public Server(String csvMapPath, int port, boolean withConsole, int maxIncommingConnection) throws UnknownHostException, IOException, 
    FileNotFoundException, IncorrectFileFormatException, IllegalArgumentException {
        this(csvMapPath, port, withConsole, maxIncommingConnection, DEFAULT_POOL_SIZE);
    }

    /**
     * 
     * @param host                   Nom de l'adresse sur laquelle le server doit etre lié
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @param withConsole            Determine si l'entrée standart doit etre ecoutée
     * @throws UnknownHostException  si aucune adresse pour le {@code host} ne pouvait etre trouvée
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String csvMapPath, int port, boolean withConsole) throws UnknownHostException, IOException, 
    FileNotFoundException, IncorrectFileFormatException, IllegalArgumentException {
        this(csvMapPath, port, withConsole, MAX_INCOMMING_CONNECTION);
    }

    /**
     * 
     * @param host                   Nom de l'adresse sur laquelle le server doit etre lié
     * @param port                   Numero du port sur lequel le server doit etre lié
     * @throws UnknownHostException  si aucune adresse pour le {@code host} ne pouvait etre trouvée
     * @throws IOException           si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String csvMapPath, int port) throws UnknownHostException, IOException, 
    FileNotFoundException, IncorrectFileFormatException, IllegalArgumentException {
        this(csvMapPath, port, false, MAX_INCOMMING_CONNECTION);
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
                RequestHandler requestHandler = new RequestHandler(this, clientSocket);
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

    public <T> T mapPlan(MapUser<T> user) throws InterruptedException, Exception {
        return synchronizedPlan.map(user);
    }

    public void updateMap(Plan newPlan) throws InterruptedException, Exception {
        synchronizedPlan.updateMap(newPlan);
    }

    public void updateTime(String timeFilePath) throws InterruptedException, Exception {
        synchronizedPlan.updateTime(timeFilePath);
    }

}

@FunctionalInterface
interface MapUser<T> {
    T apply(Plan p) throws Exception;
}

class SynchronizedPlan {

    /**
     * Le plan
     */
    private Plan plan;

    /**
     * Sempaohore protegeant l'access à la variable {@code readcount}
     */
    private Semaphore readerSemaphore;

    /**
     * Sempaohore protegeant l'access à la variable {@code writecount}
     */
    private Semaphore writerSemaphore;


    /**
     * Semaphore pretant d'indiquer d'un ecrivain veut ecrire
     */
    private Semaphore readTry;

    /**
     * Semaphore protegeant l'access au plan
     */
    private Semaphore ressourceSemaphore;

    /**
     * Nombre de lecteurs en cours
     */
    private int readcount;

    /**
     * Nombre d'ecrivains en cours
     */
    private int writecount;

    SynchronizedPlan(Plan plan) {
        this.plan = plan;
        readerSemaphore = new Semaphore(1);
        writerSemaphore = new Semaphore(1);
        readTry = new Semaphore(1);
        ressourceSemaphore = new Semaphore(1);

        readcount = 0;
        writecount = 0;
    }

    public <T> T map(MapUser<T> user) throws InterruptedException, Exception  {
        readTry.acquire();
        readerSemaphore.acquire();
        readcount++;
        if (readcount == 1) {
            ressourceSemaphore.acquire();;
        }
        readerSemaphore.release();
        readTry.release();     
        T res = user.apply(plan);

        readerSemaphore.acquire();
        readcount--;
        if (readcount == 0) {
            ressourceSemaphore.release();
        }
        readerSemaphore.release();
        return res;
    }

    private void acquireWriterRessource() throws InterruptedException, Exception {
        writerSemaphore.acquire();
        writecount += 1;
        if (writecount == 1) {
            readTry.acquire();
        }
        writerSemaphore.release();
        ressourceSemaphore.acquire();
    }

    private void releaseWriterRessource() throws InterruptedException, Exception {
        ressourceSemaphore.release();
        writerSemaphore.acquire();
        writecount--;
        if (writecount == 0) {
            readTry.release();
        }

        writerSemaphore.release();
    }

    public void updateTime(String path) throws InterruptedException, Exception {
        acquireWriterRessource();

       // Update Time when 28-diskratime merged

        releaseWriterRessource();
    }

    public void updateMap(Plan newPlan) throws InterruptedException, Exception {
        acquireWriterRessource();

        this.plan = newPlan;

        releaseWriterRessource();
    }
}