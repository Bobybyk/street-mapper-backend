package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import server.data.ErrorServer;
import util.Logger;

/**
 * Classe représentant la gestion des requêtes avec le client. Actuellement le server réagit à un
 * mot clef lu dans la chaîne de caractère envoyée par le client et agit en conséquence.
 */
class ClientHandler implements Runnable {

    private static final String MESSAGE_NULL_ARGS = "Message est null";
    /**
     * Socket du client permettant de lui envoyé la réponse
     */
    private final Socket clientSocket;

    /**
     * Un liseur de stream associé au {@code InputStream} du {@code clientSocket}
     */
    private InputStreamReader clientInputStreamReader;

    /**
     * Indique si le client est connecté
     */
    private boolean isConnected;

    /**
     * Instance du server qui a créé le {@code ClientHandler}
    */
    private Server server;

    /**
     * 
     * @param server       Instance du server qui a créé le {@code ClientHandler}
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     */
    ClientHandler(Server server, Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientInputStreamReader = new InputStreamReader(clientSocket.getInputStream());
        this.isConnected = true;
        this.server = server;
    }

    /**
     * Forme un message d'erreur
     *
     * @param reason Message décrivant le message l'erreur
     * @return
     */
    private static String errorMessageFormat(String reason) {
        return String.format("[Erreur-serveur] %s", reason);
    }

    /**
     * Crée un {@code ErrorServer} en formatant {@code reason}
     *
     * @param reason Message décrivant le message d'erreur
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
        if (message == null)
            return serverErrorFormatted(MESSAGE_NULL_ARGS);
        return handleLine(message);
    }

    /**
     * Execute l'action en fonction de la requête lue dans la chaîne de caractère
     *
     * @param clientLine Ligne (chaîne de caractère) lue dans le socket
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du
     *         socket
     */
    private Serializable handleLine(String clientLine) {
        try {
            ServerActionCallback callback = RequestParser.getServerActionCallback(server.getPlan(), clientLine);
            return callback.execute();
        } catch (RequestParser.ParsingException e) {
            return serverErrorFormatted(e.getMessage());
        }
    }

    // Implement Runnable
    @Override
    public void run() {
        while (isConnected) {
            try {
                Serializable s = handleClient();
                ObjectOutputStream outStream =
                        new ObjectOutputStream(clientSocket.getOutputStream());
                outStream.writeObject(s);
                outStream.flush();
            } catch (IOException e) {
                isConnected = false;
                try {
                    clientSocket.close();
                } catch (IOException ignore) {
                    Logger.info(ignore.getMessage());
                }
            }
        }
    }
}
