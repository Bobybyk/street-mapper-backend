package app.server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.server.data.ErrorServer;
import org.junit.jupiter.api.*;

import app.server.data.Route;

public class ServerTest {

    private static final String HOST = "localhost";
    private static final String ROUTE_REQUEST_WRONG = "ROUTE;GARE1;GARE2";
    private static final String ROUTE_REQUEST_RIGHT = "ROUTE;Pyramides;Bercy";

    private static final int PORT = 12334;
    private static final int incommingConnection = 3;
    private static final long TIMEOUT = 3;

    private static Server server = null;
    private static Socket clientSocket = null;
    private static PrintWriter out = null;
    private static ObjectInputStream in = null;

    @BeforeAll
    static void init() {
        try {
            server = initServer();
            System.out.println("################### 1 --debug");
            clientSocket = new Socket(HOST, PORT);
            System.out.println("################### 1");
            out = new PrintWriter(clientSocket.getOutputStream());
            System.out.println("################### 1");
            in = new ObjectInputStream(clientSocket.getInputStream());
            in.reset();
            System.out.println("################### 1");
        } catch (IOException e) {
            System.out.println("Erreur Test init socket / serveur ");
        }
    }

    @AfterAll
    static void close() {
        try {
            server.stop();
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoye une requete au server et retourne l'objet qui correspond à la reponse du server
     *
     * @param request      la requete a envoyé
     * @return objet renvoyé par le server
     * @throws IOException            erreur du serveur
     * @throws ClassNotFoundException class introuvable
     */
    private static Object sendRequest(String request) throws IOException, ClassNotFoundException {
        out.println(request);
        out.flush();
        return in.readObject();
    }

    /**
     * Cree un Server, le lance dans un nouveau thread et retourne cette instance
     *
     * @return le server creer
     * @throws IOException erreur du serveur
     */
    private static Server initServer() throws IOException {
        Server server = new Server(HOST, PORT, incommingConnection);
        Thread threadServer = new Thread(server::start);
        threadServer.start();
        return server;
    }

   @Test
    @Timeout(value = TIMEOUT)
    public void testServerIsRunning() {
        assertTrue(server.isRunning());
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testWrongQueryRoute() throws IOException, ClassNotFoundException {
        System.out.println("testWrongQueryRoute() avant ");
        Object serializableRoute = sendRequest(ROUTE_REQUEST_WRONG);
        assertTrue(serializableRoute instanceof ErrorServer);
        System.out.println("testWrongQueryRoute() apres");

    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testQueryRoute() throws IOException, ClassNotFoundException {
        System.out.println("testQueryRoute() avant ");

        Object serializableRoute = sendRequest(ROUTE_REQUEST_RIGHT);
        assertTrue(serializableRoute instanceof Route);
        System.out.println("testQueryRoute() apres ");

    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testQueryRouteTwice() throws IOException, ClassNotFoundException {

        System.out.println("testQueryRouteTwice() avant ");

        // Once
        Object serializableRoute = sendRequest(ROUTE_REQUEST_RIGHT);
        assertTrue(serializableRoute instanceof Route);

        // Twice
        Object serializableRoute2 = sendRequest(ROUTE_REQUEST_RIGHT);
        assertTrue(serializableRoute2 instanceof Route);
        System.out.println("testQueryRouteTwice() apres ");

    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testQueryOneGoodOneWrong() throws Exception {
        System.out.println("testQueryOneGoodOneWrong() avant ");

        Object serializableRoute = sendRequest(ROUTE_REQUEST_RIGHT);
        assertTrue(serializableRoute instanceof Route);
        Object exception = sendRequest(ROUTE_REQUEST_WRONG);
        assertTrue(exception instanceof ErrorServer);
        System.out.println("testQueryOneGoodOneWrong() ap ");

    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testWrongQuery() throws Exception {
        System.out.println("testWrongQuery() avant ");

        Object exception = sendRequest(ROUTE_REQUEST_WRONG);
        assertTrue(exception instanceof ErrorServer);
        System.out.println("testWrongQuery() ap ");

    }

}

