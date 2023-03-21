package app.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    /**
     * Nombres de threads par default utilsés par le server
     */
    private static final int DEFAULT_POOL_SIZE = 10; // Totalement abitraire pour l'instant

    /**
     * Nombres de secondes laissées aux threads lancés pour se terminer avant la fermeture de tous les threads
     */
    private static final long AWAIT_TIME_BEFORE_DYING = 5;

    private ServerSocket serverSocket;
    private boolean isRunning;
    private final ExecutorService threadPool;

    public Server(String host, int port, int maxIncommingConnection, int poolSize) throws UnknownHostException, IOException {
        this.isRunning = false;
        this.serverSocket = new ServerSocket(port, Math.abs(maxIncommingConnection), InetAddress.getByName(host));
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public Server(String host, int port, int maxIncommingConnection) throws UnknownHostException, IOException {
        this(host, port, maxIncommingConnection, DEFAULT_POOL_SIZE);
    }

    public void start() {
        isRunning = true;
        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                RequestHandler requestHandler = new RequestHandler(this, clientSocket);
                threadPool.execute(requestHandler);
            } catch (IOException e) {
                System.err.println( String.format("Erreur : %s\n", e.getMessage()) );
            }
        }

        tearDown();

    }

    synchronized void stop() {
        this.isRunning = false;
    }

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


