package app.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

public class ServerTest {

    private static final String host = "localhost";
    private static final int port = 12345;
    private static final int incommingConnection = 3;
    private static final int sleepTime = 1_000;

    @Test
    public void test_CreateandStopServer() throws UnknownHostException, IOException, InterruptedException {
        Server s = new Server(host, port, incommingConnection);
        assertFalse(s.isRunning());

        Thread killServer = new Thread(() -> {
            s.start();
        });
        killServer.start();

        Thread.sleep(sleepTime);
        assertTrue(s.isRunning());
        
        s.stop();
        Thread.sleep(sleepTime);
        assertFalse(s.isRunning());
    }
    
}
