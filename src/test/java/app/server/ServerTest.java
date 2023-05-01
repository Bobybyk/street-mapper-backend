package app.server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.map.PlanParser.IncorrectFileFormatException;
import app.server.data.ErrorServer;
import org.junit.jupiter.api.*;

import app.server.data.Route;
import app.server.data.SuggestionStations;

public class ServerTest {

    private static final String HOST = "localhost";
    private static final String ROUTE_REQUEST_WRONG = "ROUTE;GARE1;GARE2";
    private static final String ROUTE_REQUEST_RIGHT = "ROUTE;Pyramides;Bercy";

    private static final String NULL_REQUEST = null;
    private static final String EMPTY_REQUEST = "";
    private static final String SUGGESTION_VALID_DEPART = "SEARCH;GARE1;DEPART";
    private static final String SUGGESTION_VALID_ARRIVAL = "SEARCH;GARE1;ARRIVAL";
    private static final String SUGGESTION_INVALID_2ARG = "SEARCH;GARE1;afhja";
    private static final String SUGGESTION_EMPTY = "SEARCH; ";

    private static final int PORT = 12334;
    private static final int incommingConnection = 3;
    private static final long TIMEOUT = 3;

    private static Server server = null;
    private static Socket clientSocket = null;
    private static PrintWriter out = null;
    private static ObjectInputStream in = null;

    private static final String MAP_DATA_ALL = "map_data_all";

    private static String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
    }

    @BeforeAll
    static void init() {
        try {
            server = initServer();
            clientSocket = new Socket(HOST, PORT);
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException | IllegalArgumentException | IncorrectFileFormatException e) {
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
        in = new ObjectInputStream(clientSocket.getInputStream());
        return in.readObject();
    }

    /**
     * Cree un Server, le lance dans un nouveau thread et retourne cette instance
     *
     * @return le server creer
     * @throws IOException erreur du serveur
     * @throws IncorrectFileFormatException
     * @throws IllegalArgumentException
     */
    private static Server initServer() throws IOException, IllegalArgumentException, IncorrectFileFormatException {
        Server server = new Server(getPath(MAP_DATA_ALL), PORT, false, incommingConnection);
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
    public void testNullRequest() throws Exception {
        Object errorServer = sendRequest(NULL_REQUEST);
        assertTrue(errorServer instanceof ErrorServer);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testEmptyRequest() throws Exception {
        Object errorServer = sendRequest(EMPTY_REQUEST);
        assertTrue(errorServer instanceof ErrorServer);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testWrongQueryRoute() throws Exception {
        Object serializableRoute = sendRequest(ROUTE_REQUEST_WRONG);
        assertTrue(serializableRoute instanceof ErrorServer);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testQueryRoute() throws Exception {
        Object serializableRoute = sendRequest(ROUTE_REQUEST_RIGHT);
        assertTrue(serializableRoute instanceof Route);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testQueryRouteTwice() throws Exception {
        Object serializableRoute = sendRequest(ROUTE_REQUEST_RIGHT);
        assertTrue(serializableRoute instanceof Route);
        Object serializableRoute2 = sendRequest(ROUTE_REQUEST_RIGHT);
        assertTrue(serializableRoute2 instanceof Route);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testQueryOneGoodOneWrong() throws Exception {
        Object serializableRoute = sendRequest(ROUTE_REQUEST_RIGHT);
        assertTrue(serializableRoute instanceof Route);
        Object exception = sendRequest(ROUTE_REQUEST_WRONG);
        assertTrue(exception instanceof ErrorServer);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testWrongQuery() throws Exception {
        Object exception = sendRequest(ROUTE_REQUEST_WRONG);
        assertTrue(exception instanceof ErrorServer);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testValidSuggestionDepart() throws Exception {
        Object suggestions = sendRequest(SUGGESTION_VALID_DEPART);
        assertTrue(suggestions instanceof SuggestionStations);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testValidSuggestionArrival() throws Exception {
        Object suggestions = sendRequest(SUGGESTION_VALID_ARRIVAL);
        assertTrue(suggestions instanceof SuggestionStations);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testInvalidSuggestion2Arg() throws Exception {
        Object suggestions = sendRequest(SUGGESTION_INVALID_2ARG);
        assertTrue(suggestions instanceof ErrorServer);
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testEmptySuggestion() throws Exception {
        Object suggestions = sendRequest(SUGGESTION_EMPTY);
        assertTrue(suggestions instanceof ErrorServer);
    }

}

