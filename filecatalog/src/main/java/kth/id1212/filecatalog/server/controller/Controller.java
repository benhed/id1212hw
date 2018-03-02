
package kth.id1212.filecatalog.server.controller;

import kth.id1212.filecatalog.server.integration.AccountCatalogDAO;
import kth.id1212.filecatalog.server.model.AccountHolder;
import kth.id1212.filecatalog.common.Account;
import kth.id1212.filecatalog.common.Client;
import kth.id1212.filecatalog.server.model.FileDTO;
import kth.id1212.filecatalog.server.model.ClientHandler;
import kth.id1212.filecatalog.server.model.InputException;
import kth.id1212.filecatalog.common.DatabaseCommands;

import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.rmi.RemoteException;
import java.util.List;


/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class Controller extends UnicastRemoteObject implements DatabaseCommands {
    private final AccountCatalogDAO accountCatalog;
    private final ClientHandler clientHandler = new ClientHandler();
    
    public Controller(String src) throws RemoteException {
        super();
        accountCatalog = new AccountCatalogDAO(src);
    }
    
    @Override
    public synchronized int login(Client clientOutput, Account account){
        if(accountCatalog.userExists(account.getUsername())){
            if(accountCatalog.loginCheck(account.getUsername(), account.getPassword())){
                int clientId = clientHandler.createClient(clientOutput, account);
                return clientId;
            } else {
                try{
                    clientOutput.output("Username or password wrong, try again.");
                } catch (RemoteException e){
                    System.out.println("Oops! Something went wrong.");
                }    
            }
        } else {
            try{
                clientOutput.output("No account with that username exists.");
            } catch (RemoteException e){
                System.out.println("Oops! Something went wrong.");
            }
        }
        return 0;
    }
    
    @Override
    public synchronized void logout(int id){
        clientHandler.removeHolder(id);
    }
    
    @Override
    public synchronized int register(Client clientOutput, Account account) throws InputException{
        try{
            if(!accountCatalog.userExists(account.getUsername())){
                accountCatalog.register(account);
                return login(clientOutput, account);
            } else {
                throw new Exception("That username is taken.");
            }
        } catch(Exception e){
            throw new InputException("That username is taken.");
        }
    }
    
    @Override
    public synchronized void unregister(String username, String password) throws InputException{
        try{
            if(accountCatalog.userExists(username)){
                if(accountCatalog.loginCheck(username, password)){
                    accountCatalog.unregister(username);
                } else {
                    throw new Exception("Incorrect username or password, try again.");
                }
            } else {
                throw new Exception("No account with that username exists.");
            }
        } catch(Exception e){
            throw new InputException("Something went wrong");
        }
    }
    
    @Override
    public synchronized List<FileDTO> listAccessibleFiles(int id) throws SQLException {
        try{
            String username = null;
            
            if(id != 0){
                AccountHolder user = clientHandler.getHolder(id);
                username = user.getUsername();
            }
            
            List<FileDTO> filesList = accountCatalog.listFiles();
            List<FileDTO> accessibleFilesList = new ArrayList<>();
            for(FileDTO file : filesList){
                if(file.getFileAccess().equals("public") || file.getFileOwner().equals(username)){
                    accessibleFilesList.add(file);
                }
            }
            return accessibleFilesList;
        } catch (SQLException e){
            throw new RuntimeException("Couldn't list all accessible files.", e);
        }
    }
    
    @Override
    public synchronized void upload(FileDTO file) throws InputException {
        if(!accountCatalog.fileExists(file.getFileName())){
            accountCatalog.addFile(file);
        } else {
            throw new InputException("That name is already in use, try again.");
        }
    }
    
    @Override
    public synchronized void delete(String filename, int id) throws InputException {
        if(accountCatalog.fileExists(filename)){
            FileDTO file = accountCatalog.getFile(filename);

            AccountHolder user = clientHandler.getHolder(id);
            String username = user.getUsername();
            if(file.getFileOwner().equals(username)){
                accountCatalog.removeFile(file);
            } else if(file.getFileAccess().equals("public")) {
                if(file.getFilePermission().equals("w")){
                    accountCatalog.removeFile(file);
                } else {
                    throw new InputException("You do not have the permission to do that");
                }
            }
        } else if(!(accountCatalog.fileExists(filename))) {
                throw new InputException("There is no such file, try again.");
            }
    }
    
    @Override
    public synchronized String download(String filename, int id) throws InputException{
        if(accountCatalog.fileExists(filename) && accountCatalog.getFile(filename).getFileAccess().equals("public")){
            if(id != 0){
                AccountHolder user = clientHandler.getHolder(id); 
                String username = user.getUsername();
                if(accountCatalog.getFile(filename).getNotifyValue() == 1)
                    notifyFileDownload(accountCatalog.getFile(filename).getNotifyId(), filename, username);
            } 
            else {
                return "Anonymous is not allowed here! You must be logged in to do that.";
            }
            return filename+" was found";
        } else {
            throw new InputException("There is no such file, try again.");
        }
    }
    
    @Override
    public synchronized void notifyFileDownload(int id, String filename, String username){
        clientHandler.notifyFileDownload(id, filename, username);
    }
    
    @Override
    public synchronized String getUsername(int id){
        return clientHandler.getHolder(id).getUsername();
    }
}
