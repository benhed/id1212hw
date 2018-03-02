
package kth.id1212.filecatalog.server.startup;

import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import kth.id1212.filecatalog.server.controller.Controller;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class Server {
    private final String src = "hw3";
    
    public static void main(String[] args){
        try{
            Server server = new Server();
            server.startServant();
        } catch(RemoteException | MalformedURLException e){
            System.out.println("Couldn't start the server.");
        }
    }
    
    private void startServant() throws RemoteException, MalformedURLException {
        try{
            LocateRegistry.getRegistry().list();
        } catch (RemoteException e){
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller controller = new Controller(src);
        Naming.rebind("hw3DB", controller);
    }
}
