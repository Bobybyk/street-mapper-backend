package app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

class RequestHandler implements Runnable {

    private HashMap<String, ServerActionCallback> requestActions = new HashMap<>();

    private static final String ROUTE_KEY = "ROUTE";
    private static final String KILL_KEY = "kill";
    private String charSplitter = ";";

    private Socket clientSocket;
    private Server server;


    RequestHandler(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.setupRequestAction();
    }

    private void setupRequestAction() {
        this.requestActions.put(ROUTE_KEY, this::handleRouteRequest);
        this.requestActions.put(KILL_KEY, this::handleKillRequest);
    }

    private void handleClient(Socket clientSocket) throws IOException {
        String inputLine = null;
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        do {
            inputLine = in.readLine();
            handleLine(inputLine, clientSocket);
        } while (inputLine != null);

    }

    private void handleLine(String clientLine, Socket clientSocket) throws IOException {
        String[] splittedLine = clientLine.split(charSplitter);
        // Pour l'instant assumer que tout va bien niveau formattage
        String clientRequest = splittedLine[0];
        ServerActionCallback callback = requestActions.get(clientRequest);
        if (callback == null) {
            System.err.println(String.format("Unknown action for %s", clientRequest));
        } else {
            callback.execute(clientRequest, clientSocket);
        }
    }

    private synchronized void handleRouteRequest(String inputLine, Socket clientSocket) throws IOException {
        /// Todo: Waiting the disjkra merge
        System.out.println( String.format("read Line = %s", inputLine) );

    }

    private synchronized void handleKillRequest(String inputLine, Socket clientSocket) throws IOException {
        server.stop();
    }

    @Override
    public void run() {
        try {
            handleClient(clientSocket);
        } catch (IOException e) {
            // TODO: Handle IOerror
            e.printStackTrace();
        }
    }
    
}

@FunctionalInterface
interface ServerActionCallback {
    void execute(String s, Socket socket) throws IOException ;
}

