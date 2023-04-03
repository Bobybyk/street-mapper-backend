package app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import app.App;
import app.map.Map;
import app.server.data.Route;
import app.server.data.UnknownRequestException;

/**
 * Classe réprésentant les réponses du server.
 * Actuellement le server réagit à un mot clef lu dans la chaine de caractèrere envoyé par le client
 * et agit en conséquence.
 */
class RequestHandler implements Runnable {



    /**
     * Nom de la commande correspondant la requête pour un chemin
     */
    private static final String ROUTE_KEY = "ROUTE";

    /**
     * Ensemble des couples mot-clef actions a exécuter
     * @see ServerActionCallback
    */
    private static final java.util.Map<String, ServerActionCallback> requestActions = java.util.Map.of(
        ROUTE_KEY, RequestHandler::handleRouteRequest
    );

    /**
     * Caractère utilisé pour sépérarer les arguments de la requête 
     */
    private static final String charSplitter = ";";

    /**
     * Socket du client permettant de lui envoyé la réponse
     */
    private final Socket clientSocket;

    /**
     * 
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     */
    RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Lit le contenu du socket
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     * 
     */
    private void handleClient(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String message = in.readLine();

        if (message != null)
            handleLine(message, clientSocket);
        
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
            new UnknownRequestException(clientRequest).execute(clientLine, clientSocket);
        } else {
            callback.execute(clientLine, clientSocket);
        }
    }

    /**
     * Gere la reponse du renvoie d'un trajet au client
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    private static void handleRouteRequest(String inputLine, Socket clientSocket) throws IOException {
        String[] tabLine = inputLine.split(charSplitter);
        ObjectOutputStream outStream = new ObjectOutputStream(clientSocket.getOutputStream());
        try {
            Route trajet = new Route(App.getInstanceOfMap().findPathDistOpt(tabLine[1], tabLine[2]));
            outStream.writeObject(trajet);
            outStream.flush();
        } catch (Map.PathNotFoundException e) {
            System.out.println("Erreur: Trajet inexistant");
            outStream.writeObject(new Error("[Erreur-serveur] Trajet incorrect"));
            outStream.flush();
        }
    }

    // Implement Runnable
    @Override
    public void run() {
        try {
            while (true) {
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException _exception) {
                System.out.println("Erreur @run REQQUESTHANDLER");
            }
        }
    }
    
}

