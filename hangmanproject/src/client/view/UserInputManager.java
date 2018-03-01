package client.view;

import client.connection.ServerMsgHandler;
import client.controller.Controller;
import com.sun.corba.se.spi.activation.Server;

import java.util.Scanner;

/**
 * Created by Benjamin and Anton on 2017-11-17.
 */
public class UserInputManager implements Runnable{
    private final Scanner scan = new Scanner(System.in);
    SynchronizedOutput output = new SynchronizedOutput();
    Controller controller;
    private final int port;
    private final String serverName;
    boolean isActive = true;

    public UserInputManager(Controller controller, String serverName, int port){
        this.controller = controller;
        this.serverName = serverName;
        this.port = port;
    }

    @Override
    public synchronized void run(){
        controller.connectToServer(port, serverName, new ServerOutput());
        System.out.println("Press '.' to quit.");

        while(isActive){
            String userInput = scan.next();
            if(userInput.equals(".")){
                System.out.println("Bye!");
                controller.disconnect();
                isActive = false;
            }
            else{
                controller.send(userInput);
            }
        }
    }


}

