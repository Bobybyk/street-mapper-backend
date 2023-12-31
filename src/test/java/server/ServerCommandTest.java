package server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import server.commands.ServerCommandDebug;
import server.commands.ServerCommandUpdateMapFile;
import server.commands.ServerCommandUpdateTimeFile;
import server.data.DepartureTimes;
import server.data.StationTime;
import server.data.SuggestionStations;
import server.map.StationInfo;
import server.map.Time;
import server.map.PlanParser.IncorrectFileFormatException;
import util.Logger;

class ServerCommandTest {
    private static Server server = null;
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private static final String MAP_DATA_ALL = "map_data_all";
    private static final String MAP_DATA_DUMMY = "map_data_dummy";
    private static final String TIME_DATA = "time_data_all";
    private static final String SUGGESTION_REQUEST_1 = "SEARCH;Chatelet;ARRIVAL";
    private static final String SUGGESTION_REQUEST_2 = "SEARCH;stationA;ARRIVAL";
    private static final String TIME_REQUEST = "TIME;Avron;6:00";
    private static final int DEFAULT_TIMEOUT = 2000;
    private static boolean isLoggerEnable;


    static StationInfo createStationInfo(String stationName, String... lines) {
        return new StationInfo(stationName, Arrays.asList(lines));
    }

    @BeforeAll
    static void init() throws Exception {
        server = initServer();
        changeTimeFile(getPath(TIME_DATA));
        changeMap(getPath(MAP_DATA_DUMMY));
        isLoggerEnable = Logger.isEnable();
    }

    @AfterAll
    static void close() {
        if (isLoggerEnable) {
            Logger.enable();
        } else {
            Logger.disable();
        }
        try {
            server.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private static void changeMap(String path) throws IllegalArgumentException, Exception {
        ServerCommandUpdateMapFile scmf = new ServerCommandUpdateMapFile();
        scmf.execute(server, "", path);
    }

    private static void changeTimeFile(String path) throws IllegalArgumentException, Exception {
        ServerCommandUpdateTimeFile sctf = new ServerCommandUpdateTimeFile();
        sctf.execute(server, "", path);
    }

    private static void changeLoggerState(boolean enable) throws IllegalArgumentException, Exception {
        ServerCommandDebug debug = new ServerCommandDebug();
        debug.execute(server, "", enable ? "1" : "0" );
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
        boolean res = false;
        InputStream stream = clientSocket.getInputStream();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        Object o = sendRequest(stream, out, request);
        if (o instanceof SuggestionStations s) {
            Set<StationInfo> infos = s.getStations();            
            res =  infos.size() == expectedSize && infos.containsAll(Arrays.asList(stationInfos));
        }
        return res;
    }

    private static boolean timeTest(Socket clientSocket, String request, int index, StationTime time) throws ClassNotFoundException, IOException {
        boolean res = false;
        InputStream stream = clientSocket.getInputStream();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        Object o = sendRequest(stream, out, request);
        if (o instanceof DepartureTimes departureTimes) {
            List<StationTime> times = departureTimes.getTimes();
            int size = times.size();
            if (index < 0 || index >= size )
                return false;
            StationTime st = times.get(index);
            res = st.equals(time);
        }

        return res;
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void testInitialSuggestionValue() throws IOException, IllegalArgumentException, IncorrectFileFormatException, ClassNotFoundException {
        Socket clientSocket = new Socket(HOST, PORT);
        StationInfo chatelet = createStationInfo("Châtelet", "1", "4", "7", "11", "14");
        boolean res = suggesionTest(clientSocket, SUGGESTION_REQUEST_1,1, chatelet);
        clientSocket.close();
        assertFalse(res);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void testTimeBeforeChange() throws IOException, IllegalArgumentException, ClassNotFoundException {
        Socket clientSocket = new Socket(HOST, PORT);
        StationTime nationTime = new StationTime("2", "Avron", new Time(6, 5, 0));
        boolean res = timeTest(clientSocket, TIME_REQUEST, 0, nationTime);
        clientSocket.close();
        assertFalse(res);
    }

    @Test 
    @Timeout(DEFAULT_TIMEOUT)
    void testWrongMapCommandFormat() throws Exception {
        ServerCommandUpdateMapFile scmf = new ServerCommandUpdateMapFile();
        assertThrows(IllegalArgumentException.class, () -> scmf.execute(server)
        , "ServerCommandUpdateMapFile wrong argument");
    }

    @Test 
    @Timeout(DEFAULT_TIMEOUT)
    void testWrongTimeCommandFormat() throws Exception {
        ServerCommandUpdateTimeFile scmf = new ServerCommandUpdateTimeFile();
        assertThrows(IllegalArgumentException.class, () -> scmf.execute(server)
        , "ServerCommandUpdateTimeFile wrong argument");
    }

    @Test 
    @Timeout(DEFAULT_TIMEOUT)
    void testEnableLogger() throws Exception {
        changeLoggerState(true);
        assertTrue(Logger.isEnable());
    }

    @Test 
    @Timeout(DEFAULT_TIMEOUT)
    void testDisableLogger() throws Exception {
        changeLoggerState(false);
        assertFalse(Logger.isEnable());
    }

    @Test 
    @Timeout(DEFAULT_TIMEOUT)
    void testWrongDebugCommandFormat() throws Exception {
        ServerCommandDebug scmf = new ServerCommandDebug();
        assertThrows(IllegalArgumentException.class, () -> scmf.execute(server)
        , "ServerCommandUpdateTimeFile wrong argument");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void testSuggestionValueAftereChange() throws Exception {

        Socket clientSocket = new Socket(HOST, PORT);
        StationInfo stationA = createStationInfo("stationA", "random");
        boolean res = suggesionTest(clientSocket, SUGGESTION_REQUEST_2, 1, stationA);
        clientSocket.close();
        assertTrue(res);
    }    
    
}
