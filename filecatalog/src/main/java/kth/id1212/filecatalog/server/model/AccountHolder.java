
package kth.id1212.filecatalog.server.model;

import kth.id1212.filecatalog.common.Client;
import java.rmi.RemoteException;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class AccountHolder {
    
    private final int clientId;
    private final String username;
    private final Client clientOutput;
    private final ClientHandler clientHandler;
    
    public AccountHolder(int clientId, String username, Client clientOutput, ClientHandler clientHandler){
        this.clientId = clientId;
        this.username = username;
        this.clientOutput = clientOutput;
        this.clientHandler = clientHandler;
    }
    
    public void sendMsg(String msg){
        try{
            clientOutput.output(msg);
        } catch(RemoteException e){
            System.out.println("Couldn't send message");
        }
    }
    
    public String getUsername(){
        return username;
    }
}
