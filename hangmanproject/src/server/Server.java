package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class Server {

    private static final int LINGERTIME = 10000;
    private static final int PORT = 7080;

    private int port;
    private boolean running = true;
    private ServerSocket socket;
    private Socket clientSocket;
    private Client client;
    private List<Client> clients;
    FileHandler fileHandler;

    public static void main(String[] args){
        Server server = new Server(PORT);
        server.start();
    }

    public Server(int port){
        this.port = port;
        this.fileHandler = new FileHandler();
        this.clients = new ArrayList<>();
    }

    private Client addClient(Socket clientSocket){
        Client newClient = new Client();
        newClient.socket = clientSocket;
        clients.add(client);
        System.out.println("New client added");
        return newClient;
    }

    public void start(){
        handle();
    }

    public void handle(){
        try{
            this.socket = new ServerSocket(this.port);
        }
        catch(IOException e){
            throw new RuntimeException("Could not access the port " + port);
        }
         while(running()){
            try{
                clientSocket = this.socket.accept();
                clientSocket.setSoLinger(true,LINGERTIME);
                client = addClient(clientSocket);
            }
            catch(IOException e){
                throw new RuntimeException("Could not accept client");
            }

            Thread thisHangman = new Thread(new Hangman(client,this));
            thisHangman.setPriority(Thread.MAX_PRIORITY);
            thisHangman.start();
         }
         System.out.println("Server terminated");
    }

    private synchronized boolean running(){
        return this.running;
    }
}
