package startup;

import client.controller.Controller;
import client.view.UserInputManager;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class Main {
    private static final int PORT = 7080;
    private static final String SERVER_NAME = "localhost";

    public static void main(String[] args){
        Controller controller = new Controller();
        UserInputManager userInput = new UserInputManager(controller, SERVER_NAME, PORT);

        new Thread(userInput).start();
    }
}
