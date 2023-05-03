package server;

import java.util.Map;

import server.data.SuggestionStations.SuggestionKind;
import server.map.Plan;
import server.map.Time;
import util.Logger;
import util.Parser;

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
    public static class ParsingException extends Exception {
        public ParsingException(String msg) {
            super(msg);
        }
    }

    private RequestParser() {}

    @FunctionalInterface
    private static interface Handler {
        ServerActionCallback handle(Plan plan, String[] args) throws ParsingException;
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
     * @param plan instance du plan sur laquelle effectuer les calculs
     * @param args la requête
     * @return le traitement de la requête
     * @throws ParsingException si la requête n'a pas le bon format
     */
    public static ServerActionCallback getServerActionCallback(Plan plan, String args) throws ParsingException {
        ParsingException parsingException = new ParsingException("Requete non reconnu");

        if (args == null)
            throw parsingException;

        String[] splittedLine = args.split(CHAR_SPLITTER);

        if (splittedLine.length == 0)
            throw parsingException;

        String requestKey = splittedLine[0];
        Handler handle = handler.get(requestKey);
        if (handle == null)
            throw parsingException;
            
        return handle.handle(plan, splittedLine);
    }

    /**
     * Parse une requête ROUTE
     *
     * @param inputArgs liste des arguments de la requête
     * @return le traitement de la requête
     * @throws ParsingException si la requête n'a pas le bon format
     */
    private static ServerActionCallback handleRouteRequest(Plan plan, String[] inputArgs)
            throws ParsingException {
        if ((inputArgs.length != 5 && inputArgs.length != 6) || inputArgs[1].isBlank()
                || inputArgs[2].isBlank() || inputArgs[3].isBlank()) {
            String message = "Départ ou arrivée ou temps manquant.";
            Logger.error(message);
            throw new ParsingException(message);
        } else {
            Logger.info("TRAJET");
            String start = inputArgs[1].trim();
            String arrival = inputArgs[2].trim();
            try {
                int[] time = Parser.parse2IntSep(inputArgs[3], ":");
                boolean distOpt = !inputArgs[4].trim().equals(TIME_KEY);
                boolean foot = inputArgs.length == 6 && inputArgs[5].trim().equals(FOOT_KEY);
                return new SearchPath(new Plan(plan), start, arrival,
                new Time(time[0], time[1]), distOpt, foot);
            } catch (Exception e) {
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
    private static ServerActionCallback handleSearchRequest(Plan p, String[] inputArgs)
            throws ParsingException {
        if (inputArgs.length != 3 || inputArgs[1].isBlank()) {
            String message = "Station manquante ou vide";
            Logger.error(message);
            throw new ParsingException(message);
        }

        String stationToSearch = inputArgs[1].trim();
        SuggestionKind kind = SuggestionKind.ofString(inputArgs[2].trim());
        if (kind == null) {
            throw new ParsingException(
                    "Impossible d'analyser le type de search <Arrival| Depart>");
        }
        return new SearchStation(p.getStationsInfo(), stationToSearch, kind);
    }

    /**
     * Parse une requête TIME
     *
     * @param inputArgs liste des arguments de la requête
     * @return le traitement de la requête
     * @throws ParsingException si la requête n'a pas le bon format
     */
    private static ServerActionCallback handleTimeRequest(Plan plan, String[] inputArgs)
            throws ParsingException {
        if (inputArgs.length != 3 || inputArgs[1].isBlank() || inputArgs[2].isBlank()) {
            String message = "Station ou horaire manquant";
            Logger.error(message);
            throw new ParsingException(message);
        }
        String station = inputArgs[1].trim();
        try {
            int[] time = Parser.parse2IntSep(inputArgs[2], ":");
            Time t = new Time(time[0], time[1], 0);
            return new SearchTime(plan, station, t);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new ParsingException("Time mal formé");
        }
    }
}
