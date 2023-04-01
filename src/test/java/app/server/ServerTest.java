package app.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import app.server.data.Route;
import app.server.data.UnknownRequestException;

public class ServerTest {

    private static final String HOST = "localhost";
    private static final String ROUTE_REQUEST = "ROUTE;GARE1;GARE2";
    private static final String WRONG_REQUEST = "RANDOM;Some thing;...";
    private static final int PORT = 12345;
    private static final int incommingConnection = 3;
    private static final int SLEEP_TIME = 1_000;
    private static final long TIMEOUT = 10;

    @Test
    @Timeout(value = TIMEOUT, unit = TimeUnit.SECONDS)
    public void testCreateandStopServer() throws UnknownHostException, IOException, InterruptedException {
        Server s = new Server(HOST, PORT, incommingConnection);
        assertFalse(s.isRunning());

        Thread threadServer = new Thread(() -> {
            s.start();
        });
        threadServer.start();

        Thread.sleep(SLEEP_TIME);
        assertTrue(s.isRunning());
        
        s.stop();
        Thread.sleep(SLEEP_TIME);
        assertFalse(s.isRunning());
    }
    
    @Test
    @Timeout(value = TIMEOUT, unit = TimeUnit.SECONDS)
    public void testQueryRoute() throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException {
        Server server = new Server(HOST, PORT, incommingConnection);
        Thread threadServer = new Thread(() -> {
            server.start();
        });
        threadServer.start();

        Thread.sleep(SLEEP_TIME);
        Socket clientSocket = new Socket(HOST, PORT);

 
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        out.println(ROUTE_REQUEST);
        out.flush();

        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        Object serializableRoute = in.readObject();
        assertTrue(serializableRoute instanceof Route);

        clientSocket.close();
        server.stop();
 
    }

    @Test
    @Timeout(value = TIMEOUT, unit = TimeUnit.SECONDS)
    public void testWrongQuery() throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException {
        Server server = new Server(HOST, PORT, incommingConnection);
        Thread threadServer = new Thread(() -> {
            server.start();
        });
        threadServer.start();

        Thread.sleep(SLEEP_TIME);
        Socket clientSocket = new Socket(HOST, PORT);

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        out.println(WRONG_REQUEST);
        out.flush();

        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        Object exception = in.readObject();
        assertTrue(exception instanceof UnknownRequestException);

        clientSocket.close();
        server.stop();
    }
}
