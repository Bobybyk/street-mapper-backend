package app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import app.server.data.Route;

/**
 * Classe réprésentant les réponses du server.
 * Actuellement le server réagit à un mot clef lu dans la chaine de caractèrere envoyé par le client
 * et agit en conséquence.
 */
class RequestHandler implements Runnable {

    /**
     * Ensemble des couples mot-clef actions a exécuter
     * @see ServerActionCallback
     */
    private HashMap<String, ServerActionCallback> requestActions = new HashMap<>();

    /**
     * Nom de la commande correspondant la requête pour un chemin
     */
    private static final String ROUTE_KEY = "ROUTE";

    /**
     * Caractère utilisé pour sépérarer les arguments de la requête 
     */
    private static final String charSplitter = ";";

    /**
     * Socket du client permettant de lui envoyé la réponse
     */
    private Socket clientSocket;

    /**
     * 
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     */
    RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.setupRequestAction();
    }

    /**
     * Ajoute les mots-clefs à leur action respective 
     */
    private void setupRequestAction() {
        this.requestActions.put(ROUTE_KEY, this::handleRouteRequest);
    }

    /**
     * Lit le contenu du socket
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     * 
     */
    private void handleClient(Socket clientSocket) throws IOException {
        String inputLine = null;
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        do {
            inputLine = in.readLine();
            handleLine(inputLine, clientSocket);
        } while (inputLine != null);

    }

    /**
     * Execute l'action en fonction requête lu dans la chaine de caractère
     * @param clientLine   Ligne (chaine de caractere) lue dans le sockets
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    private void handleLine(String clientLine, Socket clientSocket) throws IOException {
        String[] splittedLine = clientLine.split(charSplitter);
        // Pour l'instant assumer que tout va bien niveau formattage
        String clientRequest = splittedLine[0];
        ServerActionCallback callback = requestActions.get(clientRequest);
        if (callback == null) {
            // TODO : Error handling here
            System.err.println(String.format("Unknown action for %s", clientRequest));
        } else {
            callback.execute(clientRequest, clientSocket);
        }
    }

    /**
     * Gere la reponse du renvoie d'un trajet au client
     * @param clientLine   Ligne (chaine de caractere) lue dans le sockets
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    private synchronized void handleRouteRequest(String inputLine, Socket clientSocket) throws IOException {
        /// Todo: Waiting the disjkra merge
        System.out.println( String.format("read Line = %s", inputLine) );

        // Dummy route
        Route trajet = new Route();
        
        ObjectOutputStream outStream = new ObjectOutputStream(clientSocket.getOutputStream());
        outStream.writeObject(trajet);

    }

    // Implement Runnable
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

/**
 * l'Interface {@code ServerActionCallback} doit etre implémenté par l'importe quel objet dont la volonté 
 * est de communiqué avec le client
 */
@FunctionalInterface
interface ServerActionCallback {

    /**
     * 
     * @param s            Ligne (chaine de caractere) lue dans le sockets 
     * @param socket       Socket sur lequel la réponse sera envoyée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    void execute(String s, Socket socket) throws IOException;
}

