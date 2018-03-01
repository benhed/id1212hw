package client.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Benjamin and Anton on 2017-11-17.
 */
public class ServerConnection {
    private BufferedReader readIn;
    private PrintWriter printOut;
    private Socket socket;
    private volatile boolean connected;

    //Connect to the server
    public void connect(int port, String serverName, ServerMsgHandler smh) throws IOException{
        socket = new Socket();
        socket.connect(new InetSocketAddress(serverName, port), 100000);
        socket.setSoTimeout(10000000);
        connected = true;
        readIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printOut = new PrintWriter(socket.getOutputStream(), true);

        new Thread(new ServerRequestHandler(smh)).start();
    }

    //disconnect from the server and close socket
    public void disconnect() throws IOException{
        send("disconnect socket");
        socket.close();
        socket = null;
        connected = false;
    }

    //send a message to the server
    public void send(String msg){
        printOut.println(msg);
    }

    //class that listens for server connection requests
    private class ServerRequestHandler implements Runnable{
        private final ServerMsgHandler smh;

        private ServerRequestHandler(ServerMsgHandler smh){
            this.smh = smh;
        }

        @Override
        public void run(){
            try {
                while(true) {
                    smh.handleReceivedMsg(readIn.readLine());
                }
            }
            catch(IOException e){
                if(connected)
                    throw new RuntimeException("No active connection", e);
            }
        }
    }
}
