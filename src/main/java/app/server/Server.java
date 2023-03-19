package app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Server {


    private HashMap<String, ServerActionCallback> serverActions = new HashMap<>();
    private String charSplitter = ";";

    private static final String ROUTE_KEY = "ROUTE";

    private ServerSocket serverSocket;
    private boolean isRunning;
    // private String host;
    // private int port;

    public Server(String host, int port, int maxIncommingConnection) throws UnknownHostException, IOException {
        // this.host = host;
        // this.port = port;
        this.isRunning = false;
        this.serverSocket = new ServerSocket(port, Math.abs(maxIncommingConnection), InetAddress.getByName(host));
        this.setupServerAction();
    }

    public void start() {
        isRunning = true;

        try {
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }

            serverSocket.close();

        } catch (IOException e) {
            System.err.println( String.format("Erreur : %s\nServer STOP", e.getMessage()) );
        }    
    }

    private void setupServerAction() {
        this.serverActions.put(ROUTE_KEY, this::handleRouteRequest);
    }

    private void handleClient(Socket clientSocket) throws IOException {
        String inputLine = null;
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        while ( (inputLine = in.readLine() ) != null) {
            handleLine(inputLine, clientSocket);
        }
    }

    private void handleLine(String clientLine, Socket clientSocket) throws IOException {
        String[] splittedLine = clientLine.split(charSplitter);
        // Pour l'instant assumer que tout va bien niveau formattage
        String clientRequest = splittedLine[0];
        ServerActionCallback callback = serverActions.get(clientRequest);
        if (callback == null) {
            System.err.println(String.format("Unknown action for %s", clientRequest));
        } else {
            callback.execute(clientRequest, clientSocket);
        }
    }

    private void handleRouteRequest(String inputLine, Socket clientSocket) throws IOException {

    }
}


@FunctionalInterface
interface ServerActionCallback {
    void execute(String s, Socket socket) throws IOException ;
}
