package app;

import map.Trajet;

import java.io.*;
import java.net.Socket;

public class ThreadServeur extends Thread{

    private BufferedReader br;
    private PrintWriter pw;
    private Socket csock;

    public ThreadServeur(Socket csock) throws IOException {
        this.br = new BufferedReader(new InputStreamReader(csock.getInputStream()));
        this.pw = new PrintWriter(csock.getOutputStream(), true);
        this.csock = csock;
    }
    @Override
    public void run() {
        String value;
        while(true)
        {
            try {
                value = br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            var rep = value.split(" ");
            if(rep[0].equalsIgnoreCase("TEST_TRAJET")){
                pw.println("OBJET TRAJET");
            }else{
                pw.println("[Erreur serveur] Command invalide");
            }
        }
    }
}

