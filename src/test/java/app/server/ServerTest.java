package app.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

import app.server.data.Route;
import app.server.data.UnknownRequestException;

public class ServerTest {

    private static final String HOST = "localhost";
    private static final String ROUTE_REQUEST = "ROUTE;GARE1;GARE2";
    private static final String WRONG_REQUEST = "RANDOM;Some thing;...";
    private static final int PORT = 12345;
    private static final int incommingConnection = 3;
    private static final int SLEEP_TIME = 1_000;

    @Test
    public void test_CreateandStopServer() throws UnknownHostException, IOException, InterruptedException {
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
    public void test_QueryRoute() throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException {
        Server server = new Server(HOST, PORT, incommingConnection);
        Thread threadServer = new Thread(() -> {
            server.start();
        });
        threadServer.start();

        Thread.sleep(SLEEP_TIME);
        Socket clientSocket = new Socket(HOST, PORT);

 
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeUTF(ROUTE_REQUEST);
        out.flush();

        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        Object serializableRoute = in.readObject();
        assertTrue(serializableRoute instanceof Route);

        clientSocket.close();
        server.stop();
 
    }

    @Test
    public void test_WrongQuery() throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException {
        Server server = new Server(HOST, PORT, incommingConnection);
        Thread threadServer = new Thread(() -> {
            server.start();
        });
        threadServer.start();

        Thread.sleep(SLEEP_TIME);
        Socket clientSocket = new Socket(HOST, PORT);

        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeUTF(WRONG_REQUEST);
        out.flush();

        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        Object exception = in.readObject();
        assertTrue(exception instanceof UnknownRequestException);

        clientSocket.close();
        server.stop();
    }
}
