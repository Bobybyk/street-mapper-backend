package app.server;

import java.util.NoSuchElementException;
import java.util.Scanner;

class ServerConsole implements Runnable {

    public static final String argsSplitter = " ";
    public static final String PROMPT = "server >>> ";

    private boolean isRunning;
    private Scanner scanner;

    public ServerConsole() {
        isRunning = false;
        scanner = new Scanner(System.in);
    }


    public void dispatchFromInput(String line) {
        String[] args = line.split(argsSplitter);
        String commandString = args[0];
        ServerCommand command = ServerCommand.ofString(commandString);
    }

    private void write(Object o) {
        System.out.print(o);
        System.out.flush();
    }

    public void displayPrompt() {
        write(PROMPT);
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                displayPrompt();
                String line = scanner.nextLine();
                dispatchFromInput(line);
            } catch (IllegalStateException | NoSuchElementException e) {
               isRunning = false;
            }
        }
        scanner.close();
    }
}

enum ServerCommand {
    UPDATE_TIME,
    UPDATE_MAP;

    public static ServerCommand ofString(String s) {
        return switch (s.toLowerCase()) {
            case "update-time" -> UPDATE_TIME;
            case "update-map" -> UPDATE_MAP;
            default -> null;
        };
    }
};
