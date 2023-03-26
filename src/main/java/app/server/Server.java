package app.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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


    /**
     * 
     * @param host Nom de l'adresse sur laquelle le server doit etre lié
     * @param port Numero du port sur lequel le server doit etre lié
     * @param maxIncommingConnection Nombre de connexions simultanées que le server peut gérer 
     * @param poolSize Nombre de threads que le server peut utiliser
     * @throws UnknownHostException si aucune adresse le port (port) ne pouvait etre trouvée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String host, int port, int maxIncommingConnection, int poolSize) throws UnknownHostException, IOException {
        this.isRunning = false;
        this.serverSocket = new ServerSocket(port, Math.abs(maxIncommingConnection), InetAddress.getByName(host));
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * 
     * @param host Nom de l'adresse sur laquelle le server doit etre lié
     * @param port Numero du port sur lequel le server doit etre lié
     * @param maxIncommingConnection Nombre de connexions simultanées que le server peut gérer 
     * @throws UnknownHostException si aucune adresse le port (port) ne pouvait etre trouvée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Server(String host, int port, int maxIncommingConnection) throws UnknownHostException, IOException {
        this(host, port, maxIncommingConnection, DEFAULT_POOL_SIZE);
    }

    /**
     * Demarre le server
     */
    public void start() {
        isRunning = true;
        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                RequestHandler requestHandler = new RequestHandler(clientSocket);
                threadPool.execute(requestHandler);
            } catch (IOException e) {
                System.err.println( String.format("Erreur : %s\n", e.getMessage()) );
            }
        }

        tearDown();
    }

    /**
     * Arrete le server
     */
    synchronized void stop() {
        this.isRunning = false;
    }

    /**
     * Termine tous les threads en cours
     */
    private synchronized void tearDown() {
        try {
            if (!threadPool.awaitTermination(AWAIT_TIME_BEFORE_DYING, TimeUnit.SECONDS) ) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }
}

