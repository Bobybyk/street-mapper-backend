package app.server;

import java.util.Map;
import app.map.Plan;
import app.map.Time;
import app.server.data.SuggestionStations.SuggestionKind;
import app.util.Logger;
import app.util.Parser;
import app.util.Logger.Type;

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


    static class ParsingException extends Exception {
        public ParsingException(String msg) {
            super(msg);
        }
    }

    private RequestParser() {}

    @FunctionalInterface
    private static interface Handler {
        ServerActionCallback handle(Plan plan, String[] args) throws ParsingException;
    }

    private static final Map<String, Handler> handler =
            Map.of(ROUTE_KEY, RequestParser::handleRouteRequest, SEARCH_KEY,
                    RequestParser::handleSearchRequest, TIME_KEY, RequestParser::handleTimeRequest);


    static ServerActionCallback getServerActionCallback(Plan plan, String args) throws ParsingException {
        String[] splittedLine = args.split(CHAR_SPLITTER);
        ParsingException parsingException = new ParsingException("Requete non reconnu");

        if (splittedLine.length == 0)
            throw parsingException;

        String requestKey = splittedLine[0];
        Handler handle = handler.get(requestKey);
        if (handle == null)
            throw parsingException;
            
        return handle.handle(plan, splittedLine);
    }

    /**
     * Gere la reponse du renvoie d'un trajet au client
     *
     * @param inputArgs entrée de l'utilisateur séparé par {@link RequestParser#charSplitter}
     * @throws ParsingException
     */
    private static ServerActionCallback handleRouteRequest(Plan plan, String[] inputArgs)
            throws ParsingException {
        if (inputArgs.length != 5 && inputArgs.length != 6) {
            String message = "Trajet départ ou arrivé manquant.";
            Logger.logln(Type.ERROR, message);
            throw new ParsingException(message);
        } else {
            Logger.logln(Type.INFO, "TRAJET");
            String start = inputArgs[1];
            String arrival = inputArgs[2];
            int[] time = Parser.parse2IntSep(inputArgs[3], ":");
            boolean distOpt = !inputArgs[4].equals(TIME_KEY);
            boolean foot = inputArgs.length == 6 && inputArgs[5].equals(FOOT_KEY);
            return new SearchPath(plan.resetLinesSections(), start, arrival,
                    new Time(time[0], time[1]), distOpt, foot);
        }
    }

    /**
     * Gere la reponse du pour la sugestion de station en fonction du nom
     *
     * @param inputLine entrée de l'utilisateur séparé par {@link ClientHandler#charSplitter}
     * @throws ParsingException
     */
    private static ServerActionCallback handleSearchRequest(Plan p, String[] inputArgs)
            throws ParsingException {
        if (inputArgs.length != 3 || inputArgs[1].isBlank()) {
            String message = "Station manquante ou vide";
            Logger.logln(Type.ERROR, message);
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

    private static ServerActionCallback handleTimeRequest(Plan plan, String[] inputArgs)
            throws ParsingException {
        if (inputArgs.length != 3 || inputArgs[1].isBlank()) {
            String message = "Station ou horaire manquant";
            Logger.log(Type.ERROR, message);
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
