
package kth.id1212.filecatalog.server.model;

import java.util.Random;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import kth.id1212.filecatalog.common.Client;
import kth.id1212.filecatalog.common.Account;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class ClientHandler {
    private final Random random = new Random();
    private final Map<Integer, AccountHolder> accountHolders = Collections.synchronizedMap(new HashMap<>());
    
    public int createClient(Client clientOutput, Account account){
        int clientId = random.nextInt();
        AccountHolder accountHolder = new AccountHolder(clientId, account.getUsername(), clientOutput, this);
        accountHolders.put(clientId, accountHolder);
        return clientId;
    }
    
    public AccountHolder getHolder(int id){
        return accountHolders.get(id);
    }
    
    public void removeHolder(int id){
        accountHolders.remove(id);
    }
    
    public void notifyFileDownload(int id, String filename, String username){
        AccountHolder accountHolder = getHolder(id);
        if(accountHolder != null){
            accountHolder.sendMsg(username + " just downloaded your file " + filename + ".");
        }
    }
}
