package app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import app.server.data.ErrorServer;

/**
 * Classe représentant la gestion des requêtes avec le client. Actuellement le server réagit à un
 * mot clef lu dans la chaîne de caractère envoyée par le client et agit en conséquence.
 */
class ClientHandler implements Runnable {
    /**
     * Socket du client permettant de lui envoyé la réponse
     */
    private final Socket clientSocket;

    /**
     * Un liseur de stream associé au {@code InputStream} du {@code clientSocket}
     */
    private InputStreamReader clientInputStreamReader;

    /**
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     */
    ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientInputStreamReader = new InputStreamReader(clientSocket.getInputStream());
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
            return serverErrorFormatted("Message est null");
        return handleLine(message);
    }

    /**
     * Execute l'action en fonction de la requête lue dans la chaîne de caractère
     *
     * @param clientLine Ligne (chaîne de caractère) lue dans le socket
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du
     *         socket
     */
    private Serializable handleLine(String clientLine) throws IOException {
        try {
            ServerActionCallback callback = RequestParser.getServerActionCallback(clientLine);
            return callback.execute();
        } catch (RequestParser.ParsingException e) {
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
