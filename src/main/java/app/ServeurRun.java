package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurRun {

    public static int port = 34347;

    public static void main(String[] args) throws IOException {
        ServerSocket ssock = new ServerSocket(port);
        System.out.println("Lancement du serveur");
        System.out.println("En attente de connexion...");
        while(true){
            Socket csock = ssock.accept();
            Thread client = new Thread(new ThreadServeur(csock));
            client.start();
        }
    }
}
