package app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import app.App;
import app.map.Map;
import app.server.data.ErrorServer;
import app.server.data.Route;

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
     * Nom de la commande correspondant la recherche de stations par leur nom.
     * 
     * <p>
     * Command structure: SEARCH;nom de station
     */
    private static final String SEARCH_KEY = "SEARCH";

    /**
     * Ensemble des couples mot-clef actions a exécuter
     *
     * @see ServerActionCallback
     */
    private static final java.util.Map<String, ServerActionCallback> requestActions = java.util.Map.of(
            ROUTE_KEY, RequestHandler::handleRouteRequest,
            SEARCH_KEY, RequestHandler::handleSearchRequest
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
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     */
    RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Forme un message d'erreur
     * @param reason Message decrivant le message l'erreur
     * @return
     */
    private static String errorMessageFormat(String reason) {
        return String.format("[Erreur-serveur] %s", reason);
    }

    /**
     * Lit le contenu du socket
     *
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    private Serializable handleClient() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String message = in.readLine();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (message != null)
            return handleLine(message);
        return null;

    }

    /**
     * Execute l'action en fonction requête lu dans la chaine de caractère
     *
     * @param clientLine   Ligne (chaine de caractere) lue dans le sockets
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    private Serializable handleLine(String clientLine) throws IOException {
        String[] splittedLine = clientLine.split(charSplitter);
        String clientRequest = splittedLine[0];
        ServerActionCallback callback = requestActions.get(clientRequest);
        if (callback == null) {
            return new ErrorServer("Serveur erreur");
        } else {
            return callback.execute(clientLine);
        }
    }
    /**
     * Gere la reponse du renvoie d'un trajet au client
     *
     * @param inputLine entrée de l'utilisateur
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    private static Serializable handleRouteRequest(String inputLine) throws IOException {
        String[] tabLine = inputLine.split(charSplitter);

        if (tabLine.length != 3) {
            System.out.println("TRAJET PAS BON");
            final String errorTrajetManquant = errorMessageFormat("Trajet départ ou arrivé manquant.");
            return new ErrorServer(errorTrajetManquant);
        } else {
            try {
                Route trajet = new Route(App.getInstanceOfMap().findPathDistOpt(tabLine[1], tabLine[2]));
                System.out.println("TRAJET");
                return trajet;

            } catch (Map.PathNotFoundException e) {
                final String errorTrajetInexistant = errorMessageFormat("Trajet inexistant.");
                System.out.println(errorTrajetInexistant);
                System.out.println("ERREUR");
                return new ErrorServer(errorTrajetInexistant);
            }
        }
    }


    private static Serializable handleSearchRequest(String inputLine) throws IOException {
        String[] inputSplitted = inputLine.split(charSplitter);
        if ( inputSplitted.length < 2 || inputSplitted[1].isBlank() ) {
            final String errorMissingSearchStation = errorMessageFormat("Station manquante ou vide");
            System.out.println("TRAJET PAS BON");
            return new ErrorServer(errorMissingSearchStation);
        }

        String stationToSearch = inputSplitted[1].trim();
        // Chercher dans la map
        return null;
    }

    // Implement Runnable
    @Override
    public void run() {
        try {
            while (true) {
                Serializable s = handleClient();
                if (s != null) {
                    ObjectOutputStream outStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    outStream.writeObject(s);
                    outStream.flush();
                }
            }
        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException ignore) {
                System.out.println("Erreur @run REQQUESTHANDLER");
            }
        }
    }

}

