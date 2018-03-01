package clientserver.startup;

import clientserver.client.net.ServerConnection;
import clientserver.client.view.UserInputManager;

public class Main {
    private static final int PORT = 7080;
    private static final String SERVER_NAME = "localhost";

    public static void main(String[] args) {
        ServerConnection serverConnection = new ServerConnection();
        UserInputManager userInput = new UserInputManager(serverConnection, SERVER_NAME, PORT);
        System.out.println("Guess a letter or word.");
        
        new Thread(userInput).start();
    }
}
