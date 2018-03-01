
package clientserver.client.view;

import clientserver.client.net.*;
import java.util.*;

public class UserInputManager implements Runnable{
    private final Scanner scan = new Scanner(System.in);
    SynchronizedOutput output = new SynchronizedOutput();
    ServerConnection serverConnection;
    private final int port;
    private final String serverName;
    boolean isActive = true;

    public UserInputManager(ServerConnection connection, String serverName, int port){
        this.serverConnection = connection;
        this.serverName = serverName;
        this.port = port;
    }

    @Override
    public synchronized void run()
    {
        serverConnection.connect(serverName, port, new Out());
        System.out.println("Press '.' to quit.");
        while(isActive) {
            String in = scan.next();
            if(in.equals(".")) {
                System.out.println("Bye!");
                isActive = false;
            }
            else {
                serverConnection.sendQueue(in);
            }
        }
    }

    private class Out implements ServerMsgHandler {
        @Override
        public void handleReceivedMsg(String msg){
            output.println(msg);
        }
    }
    
}
