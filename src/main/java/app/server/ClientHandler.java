package app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import app.server.data.ErrorServer;
import app.util.Logger;
import app.util.Logger.Type;

/**
 * Classe réprésentant les réponses du server. Actuellement le server réagit à un mot clef lu dans
 * la chaine de caractèrere envoyé par le client et agit en conséquence.
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
     * Instance du server qui a créé le {@code RequestHandler}
    */
    private Server server;

    /**
     * @param clientSocket Socket sur lequel la réponse sera envoyée
     */
    ClientHandler(Server server, Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientInputStreamReader = new InputStreamReader(clientSocket.getInputStream());
        this.server = server;
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
                Logger.logln(Type.INFO, ignore.getMessage());
            }
        }
    }
}
