package app.server.data;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import app.server.ServerActionCallback;

/**
 * Class representant une erreur en cas de command non reconnue 
 */
public class UnknownRequestException extends Exception implements ServerActionCallback {

    /**
     * 
     * @param wrongRequest Nom de la commande non reconnue
     */
    public UnknownRequestException(String wrongRequest) {
        super(String.format("Unknown Command: %s", wrongRequest) );
    }

    @Override
    public void execute(String s, Socket socket) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(this);
    }
    
}
