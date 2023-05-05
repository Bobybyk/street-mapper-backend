package server;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import server.RequestParser.ParsingException;
import server.map.Plan;

public class RequestParserTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private ServerActionCallback getServerActionCallbackHelper(String args) throws Exception {
        Plan plan = new Plan();
        return RequestParser.getServerActionCallback(plan, args);
    }

    private void parsingExceptionHelper(String args) {
        assertThrows(ParsingException.class, () -> getServerActionCallbackHelper(args),
                String.format("Parsing exception : %s", args));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void handleNullRequest() {
        parsingExceptionHelper(null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"TEST", "TEST;3", "ROUTE;Gare de Lyon; Madeleine",
            "ROUTE;Gare de Lyon; Madeleine;13:30;DISTANCE;FOOT;42",
            "ROUTE;Gare de Lyon; Madeleine;130;DISTANCE;FOOT",
            "ROUTE; ; Madeleine;13:30;DISTANCE;FOOT", "ROUTE;Gare de Lyon; ;13:30;DISTANCE;FOOT",
            "ROUTE;Gare de Lyon; Madeleine; ;DISTANCE;FOOT", "SEARCH", "SEARCH;", "SEARCH;test;",
            "SEARCH;test;34", "SEARCH; ;DEPART", "TIME;", "TIME;;", "TIME;test;34", "TIME; ;13:23",
            "TIME;test; "})
    @Timeout(DEFAULT_TIMEOUT)
    public void parsingException(String args) {
        parsingExceptionHelper(args);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROUTE;Gare de Lyon; Madeleine;13:30;DISTANCE;",
            "ROUTE; Gare de Lyon; Madeleine;13:30;DISTANCE; FOOT",
            "ROUTE;Gare de Lyon; Madeleine ;13:30; TIME;",
            "ROUTE; Gare de Lyon; Madeleine; 13:30;TIME;FOOT",
            "ROUTE;Gare de Lyon; Madeleine;13:30;TIME;TEST"})
    @Timeout(DEFAULT_TIMEOUT)
    public void handleRoute(String request) throws Exception {
        ServerActionCallback callback = getServerActionCallbackHelper(request);
        assertTrue(callback instanceof SearchPath, request);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SEARCH; test; DEPART", "SEARCH; test; ARRIVAL"})
    @Timeout(DEFAULT_TIMEOUT)
    public void handleStation(String request) throws Exception {
        ServerActionCallback callback = getServerActionCallbackHelper(request);
        assertTrue(callback instanceof SearchStation, request);
    }

    @ParameterizedTest
    @ValueSource(strings = {"TIME; test; 12:20"})
    @Timeout(DEFAULT_TIMEOUT)
    public void handleTime(String request) throws Exception {
        ServerActionCallback callback = getServerActionCallbackHelper(request);
        assertTrue(callback instanceof SearchTime, request);
    }
}
