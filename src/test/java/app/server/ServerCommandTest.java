package app.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import app.map.StationInfo;
import app.map.PlanParser.IncorrectFileFormatException;
import app.server.data.SuggestionStations;

public class ServerCommandTest {
    private static Server server = null;
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private static final String MAP_DATA_ALL = "map_data_all";
    private static final String MAP_DATA_DUMMY = "map_data_dummy";
    private static final String SUGGESTION_REQUEST_1 = "SEARCH;Chatelet;ARRIVAL";
    private static final String SUGGESTION_REQUEST_2 = "SERACH;stationA;ARRIVAL";

    private static String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
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
        Server server = new Server(getPath(MAP_DATA_ALL), PORT, false);
        Thread threadServer = new Thread(server::start);
        threadServer.start();
        return server;
    }

    public static StationInfo createStationInfo(String stationName, String... lines) {
        return new StationInfo(stationName, Arrays.asList(lines));
    }

    private static void changeMap(String path) throws IllegalArgumentException, Exception {
        ServerCommandUpdateMapFile scuf = new ServerCommandUpdateMapFile();
        scuf.execute(server, "", path );
    }

    /**
     * Envoye une requete au server et retourne l'objet qui correspond à la reponse du server
     *
     * @param request la requete a envoyé
     * @return objet renvoyé par le server
     * @throws IOException erreur du serveur
     * @throws ClassNotFoundException class introuvable
     */
    private static Object sendRequest(InputStream inputStream, PrintWriter out, String request) throws IOException, ClassNotFoundException {
        out.println(request);
        out.flush();
        ObjectInputStream in = new ObjectInputStream(inputStream);
        return in.readObject();
    }

    private static boolean suggesionTest(Socket clientSocket, String request, int expectedSize, StationInfo... stationInfos) throws IOException, IllegalArgumentException, ClassNotFoundException {
        InputStream stream = clientSocket.getInputStream();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        Object o = sendRequest(stream, out, request);
        boolean res = false;
        if (o instanceof SuggestionStations s) {
            Set<StationInfo> infos = s.getStations();
            
            res =  infos.size() == expectedSize && infos.containsAll(Arrays.asList(stationInfos));
        }
        return res;
    }

    @BeforeAll
    static void init() {
        try {
            server = initServer();
        } catch (IOException | IllegalArgumentException | IncorrectFileFormatException e) {
            System.out.println("Erreur Test init socket / serveur ");
        }
    }

    @AfterAll
    static void close() {
        try {
            server.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInitialSuggestionValue() throws IOException, IllegalArgumentException, IncorrectFileFormatException, ClassNotFoundException {
        Socket clientSocket = new Socket(HOST, PORT);
        StationInfo chatelet = createStationInfo("Châtelet", "1", "4", "7", "11", "14");
        boolean res = suggesionTest(clientSocket, SUGGESTION_REQUEST_1,1, chatelet);
        clientSocket.close();
        assertTrue(res);
    }

    @Test
    public void testSuggestionValueBeforeChange() throws IOException, IllegalArgumentException, ClassNotFoundException {
        Socket clientSocket = new Socket(HOST, PORT);
        StationInfo stationA = createStationInfo("stationA");
        boolean res = suggesionTest(clientSocket, SUGGESTION_REQUEST_2, 1, stationA);
        clientSocket.close();
        assertFalse(res);
    }

    // @Test
    // public void testSuggestionValueAftereChange() throws Exception {

    //     changeMap(getPath(MAP_DATA_DUMMY));
    //     Thread.sleep(1000);

    //     Socket clientSocket = new Socket(HOST, PORT);
    //     StationInfo stationA = createStationInfo("stationA");
    //     boolean res = suggesionTest(clientSocket, SUGGESTION_REQUEST_2, 1, stationA);
    //     clientSocket.close();
    //     assertTrue(res);
    // }

    
    
}
