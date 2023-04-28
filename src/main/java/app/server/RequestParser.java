package app.server;

import java.util.Map;
import app.App;
import app.map.Time;
import app.server.data.SuggestionStations.SuggestionKind;
import app.util.Parser;

public class RequestParser {

    /**
     * Caractère utilisé pour séparer les arguments de la requête
     */
    private static final String CHAR_SPLITTER = ";";
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
     * Nom de la commande pour demander les horaires de passages à une station
     */
    private static final String TIME_KEY = "TIME";


    static class ParsingException extends Exception {
        public ParsingException(String msg) {
            super(msg);
        }
    }

    private RequestParser() {}

    @FunctionalInterface
    private static interface Handler {
        ServerActionCallback handle(String[] args) throws ParsingException;
    }

    private static final Map<String, Handler> handler =
            Map.of(ROUTE_KEY, RequestParser::handleRouteRequest, SEARCH_KEY,
                    RequestParser::handleSearchRequest, TIME_KEY, RequestParser::handleTimeRequest);


    static ServerActionCallback getServerActionCallback(String args) throws ParsingException {
        String[] splittedLine = args.split(CHAR_SPLITTER);
        if (splittedLine.length != 0) {
            String requestKey = splittedLine[0];
            Handler handle = handler.get(requestKey);
            if (handle != null)
                return handle.handle(splittedLine);
        }
        throw new ParsingException("Requete non reconnu");
    }

    /**
     * Gere la reponse du renvoie d'un trajet au client
     *
     * @param inputArgs entrée de l'utilisateur séparé par {@link RequestParser#charSplitter}
     * @throws ParsingException
     */
    private static ServerActionCallback handleRouteRequest(String[] inputArgs)
            throws ParsingException {
        if (inputArgs.length != 3) {
            System.out.println("TRAJET PAS BON");
            throw new ParsingException("Trajet départ ou arrivé manquant.");
        } else {
            System.out.println("TRAJET");
            return new SearchPath(App.getInstanceOfMap(), inputArgs[1], inputArgs[2], null, true);
        }
    }

    /**
     * Gere la reponse du pour la sugestion de station en fonction du nom
     *
     * @param inputLine entrée de l'utilisateur séparé par {@link ClientHandler#charSplitter}
     * @throws ParsingException
     */
    private static ServerActionCallback handleSearchRequest(String[] inputArgs)
            throws ParsingException {
        if (inputArgs.length != 3 || inputArgs[1].isBlank()) {
            System.out.println("RECHERCHE DE STATION PAS BONNE");
            throw new ParsingException("Station manquante ou vide");
        }

        String stationToSearch = inputArgs[1].trim();
        SuggestionKind kind = SuggestionKind.ofString(inputArgs[2].trim());
        if (kind == null) {
            throw new ParsingException(
                    "Impossible de analyser le type de search <Arrival| Depart>");
        }
        return new SearchStation(App.getInstanceOfMap().getStationsInfo(), stationToSearch, kind);
    }

    private static ServerActionCallback handleTimeRequest(String[] inputArgs)
            throws ParsingException {
        if (inputArgs.length != 3 || inputArgs[1].isBlank()) {
            System.out.println("RECHERCHE HORAIRE PAS BONNE");
            throw new ParsingException("Station ou horaire manquant");
        }
        String station = inputArgs[1].trim();
        try {
            int[] time = Parser.parse2IntSep(inputArgs[2], ":");
            Time t = new Time(time[0], time[1], 0);
            return new SearchTime(App.getInstanceOfMap(), station, t);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new ParsingException("Time mal formé");
        }
    }
}
