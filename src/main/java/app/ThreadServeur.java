package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadServeur extends Thread{

    private BufferedReader br;
    private PrintWriter pw;

    public ThreadServeur(Socket csock) throws IOException {
        br = new BufferedReader(new InputStreamReader(csock.getInputStream()));
        pw = new PrintWriter(csock.getOutputStream(), true);
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
                //do something
            }else{
                pw.println("[Erreur serveur] Command invalide");
            }
        }
    }
}

