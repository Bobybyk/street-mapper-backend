package app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import app.map.Plan;
import app.server.data.ErrorServer;
import app.server.data.Route;
import app.server.data.SuggestionStations;
import app.server.data.SuggestionStations.SuggestionKind;

/**
 * Classe réprésentant les réponses du server. Actuellement le server réagit à un mot clef lu dans
 * la chaine de caractèrere envoyé par le client et agit en conséquence.
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
    private static final java.util.Map<String, ServerActionCallback> requestActions =
            java.util.Map.of(ROUTE_KEY, RequestHandler::handleRouteRequest, SEARCH_KEY,
                    RequestHandler::handleSearchRequest);

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
     */
    private Server server;

    /**
     * Un liseur de stream associé au {@code InputStream} du {@code clientSocket}
     */
    private InputStreamReader clientInputStreamReader;

    /**
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     */
    RequestHandler(Server server, Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        this.clientInputStreamReader = new InputStreamReader(clientSocket.getInputStream());
    }

    /**
     * Forme un message d'erreur
     *
     * @param reason Message decrivant le message l'erreur
     * @return
     */
    private static String errorMessageFormat(String reason) {
        return String.format("[Erreur-serveur] %s", reason);
    }

    /**
     * Cree un {@code ErrorServer} en formattant {@code reason}
     *
     * @param reason Message decrivant le message l'erreur
     *
     * @see #errorMessageFormat()
     */
    private static ErrorServer serverErrorFormatted(String reason) {
        return new ErrorServer(errorMessageFormat(reason));
    }

    /**
     * Lit le contenu du socket
     *
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du
     *         socket
     */
    private Serializable handleClient() throws IOException {
        BufferedReader in = new BufferedReader(clientInputStreamReader);
        String message = in.readLine();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (message == null)
            return serverErrorFormatted("Message est null");

        return handleLine(message);
    }

    /**
     * Execute l'action en fonction requête lu dans la chaine de caractère
     *
     * @param clientLine Ligne (chaine de caractere) lue dans le sockets
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du
     *         socket
     */
    private Serializable handleLine(String clientLine) throws IOException {
        String[] splittedLine = clientLine.split(charSplitter);
        String clientRequest = splittedLine[0];
        ServerActionCallback callback = requestActions.get(clientRequest);
        if (callback == null) {
            return serverErrorFormatted("Serveur erreur");
        } else {
            return callback.execute(server, splittedLine);
        }
    }

    /**
     * Gere la reponse du renvoie d'un trajet au client
     *
     * @param inputArgs entrée de l'utilisateur séparé par {@link RequestHandler#charSplitter}
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du
     *         socket
     */
    private static Serializable handleRouteRequest(Server server, String[] inputArgs) throws IOException {
        if (inputArgs.length != 3) {
            System.out.println("TRAJET PAS BON");
            return serverErrorFormatted("Trajet départ ou arrivé manquant.");
        } else {
            try {
                Route trajet = server.mapPlan(plan -> {
                    return new Route(plan.findPathDistOpt(inputArgs[1], inputArgs[2]));
                });
                System.out.println("TRAJET");
                return trajet;

            } catch (Exception e) {
                if (e instanceof Plan.PathNotFoundException) {
                    return serverErrorFormatted("Trajet inexistant");
                } else if (e instanceof InterruptedException) {
                    return serverErrorFormatted("le thread de recuperation de map a ete interrompu");
                } else {
                    return serverErrorFormatted(e.getMessage());
                }
            }
        }
    }

    /**
     * Gere la reponse du pour la sugestion de station en fonction du nom
     *
     * @param inputLine entrée de l'utilisateur séparé par {@link RequestHandler#charSplitter}
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du
     *         socket
     */
    private static Serializable handleSearchRequest(Server server, String[] inputArgs) throws IOException {
        if ( inputArgs.length < 3 || inputArgs[1].isBlank() ) {
            System.out.println("RECHERCHE DE STATION PAS BONNE");
            return serverErrorFormatted("Station manquante ou vide");
        }

        String stationToSearch = inputArgs[1].trim();
        SuggestionStations.SuggestionKind kind = SuggestionKind.ofString(inputArgs[2].trim());
        if (kind == null) {
            return serverErrorFormatted("Impossible de analyser le type de search <Arrival| Depart>");
        }
        try {
            SuggestionStations suggestions = server.mapPlan(plan -> new SuggestionStations(stationToSearch, kind, plan.getStationsInfo()));
            return suggestions;
        } catch (InterruptedException e) {
            return serverErrorFormatted("le thread de recuperation de plan a ete interrompu");
        } catch (Exception e) {
            // Ne devrait pas etre atteint puisque [Plan.getStationsInfo()] ne leve pas d'exception
            return serverErrorFormatted(e.getMessage());
        }

    }

    // Implement Runnable
    @Override
    public void run() {
        try {
            while (true) {
                Serializable s = handleClient();
                ObjectOutputStream outStream =
                        new ObjectOutputStream(clientSocket.getOutputStream());
                outStream.writeObject(s);
                outStream.flush();
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
