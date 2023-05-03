package app.server;

import java.util.Map;
import app.App;
import app.map.Plan;
import app.map.Time;
import app.server.data.SuggestionStations.SuggestionKind;
import app.util.Parser;

/**
 * Parser de requêtes du client
 */
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

    private static final String FOOT_KEY = "FOOT";

    /**
     * La requête n'a pas le bon format
     */
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

    /**
     * L'ensemble des requêtes reconnues associées à leur traitement
     */
    private static final Map<String, Handler> handler =
            Map.of(ROUTE_KEY, RequestParser::handleRouteRequest, SEARCH_KEY,
                    RequestParser::handleSearchRequest, TIME_KEY, RequestParser::handleTimeRequest);


    /**
     * Parse le nom de la requête
     *
     * @param args la requête
     * @return le traitement de la requête
     * @throws ParsingException si la requête n'a pas le bon format
     */
    static ServerActionCallback getServerActionCallback(String args) throws ParsingException {
        if (args != null) {
            String[] splittedLine = args.split(CHAR_SPLITTER);
            String requestKey = splittedLine[0];
            Handler handle = handler.get(requestKey);
            if (handle != null)
                return handle.handle(splittedLine);
        }
        throw new ParsingException("Requete non reconnu");
    }

    /**
     * Parse une requête ROUTE
     *
     * @param inputArgs liste des arguments de la requête
     * @return le traitement de la requête
     * @throws ParsingException si la requête n'a pas le bon format
     */
    private static ServerActionCallback handleRouteRequest(String[] inputArgs)
            throws ParsingException {
        if ((inputArgs.length != 5 && inputArgs.length != 6) || inputArgs[1].isBlank()
                || inputArgs[2].isBlank() || inputArgs[3].isBlank()) {
            System.out.println("TRAJET PAS BON");
            throw new ParsingException("Départ ou arrivée ou temps manquant.");
        } else {
            System.out.println("TRAJET");
            String start = inputArgs[1].trim();
            String arrival = inputArgs[2].trim();
            try {
                int[] time = Parser.parse2IntSep(inputArgs[3], ":");
                boolean distOpt = !inputArgs[4].equals(TIME_KEY);
                boolean foot = inputArgs.length == 6 && inputArgs[5].trim().equals(FOOT_KEY);
                return new SearchPath(new Plan(App.getInstanceOfMap()), start, arrival,
                        new Time(time[0], time[1]), distOpt, foot);
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                throw new ParsingException("Time mal formé");
            }
        }
    }

    /**
     * Parse une requête SEARCH
     *
     * @param inputArgs liste des arguments de la requête
     * @return le traitement de la requête
     * @throws ParsingException si la requête n'a pas le bon format
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

    /**
     * Parse une requête TIME
     *
     * @param inputArgs liste des arguments de la requête
     * @return le traitement de la requête
     * @throws ParsingException si la requête n'a pas le bon format
     */
    private static ServerActionCallback handleTimeRequest(String[] inputArgs)
            throws ParsingException {
        if (inputArgs.length != 3 || inputArgs[1].isBlank() || inputArgs[2].isBlank()) {
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
