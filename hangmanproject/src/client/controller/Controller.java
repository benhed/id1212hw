package client.controller;

import client.connection.ServerConnection;
import client.connection.ServerMsgHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Benjamin and Anton on 2017-11-17.
 */
public class Controller {
    private final ServerConnection serverCon = new ServerConnection();

    public void connectToServer( int port, String serverName, ServerMsgHandler smh){
        CompletableFuture.runAsync(() ->{
            try{
                serverCon.connect(port, serverName, smh);
            }
            catch(IOException e){
                throw new RuntimeException("Failed to connect to server.", e);
            }
        });

        System.out.println("Guess a letter or word.");
    }

    public void disconnect(){
        try{
            serverCon.disconnect();
        }
        catch(IOException e){
            throw new RuntimeException("Failed to disconnect from server.", e);
        }
    }

    //Send a message to the server
    public void send(String msg) {
        CompletableFuture.runAsync(() -> serverCon.send(msg));
    }
}
