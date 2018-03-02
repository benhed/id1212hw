package kth.id1212.filecatalog.client.view;

import java.sql.SQLException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.List;
import java.rmi.server.UnicastRemoteObject;

import java.util.concurrent.ThreadLocalRandom;
import kth.id1212.filecatalog.server.model.InputException;

import kth.id1212.filecatalog.common.Account;
import kth.id1212.filecatalog.common.Client;
import kth.id1212.filecatalog.server.model.FileDTO;
import kth.id1212.filecatalog.common.DatabaseCommands;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class ViewManager implements Runnable{
    private boolean isRunning;
    private DatabaseCommands dbCmds;
    private int serverId;
    private Client clientOutput;
    private final Scanner scan = new Scanner(System.in);
    private final SyncedOutput out = new SyncedOutput();

    private final String REGISTER = "register:\tregister a new account.\n";
    private final String LOGIN = "login:\t\tlog in to an existing account.\n";
    private final String LOGOUT = "logout:\t\tlog out from the account you are currently logged in to.\n";
    private final String UPLOAD = "upload:\t\tupload a file.\n";
    private final String DOWNLOAD = "download:\tdownload a file from the public catalog.\n";
    private final String LIST = "list:\t\tlist all public files on the catalog and all private files that you own.\n";
    private final String DELETE = "delete:\t\tdelete a file that you own from the catalog.\n";
    private final String UNREGISTER = "unregister:\tunregister an account.\n";
    private final String COMMANDS_LIST = REGISTER + LOGIN + LOGOUT + UPLOAD + DOWNLOAD + LIST + DELETE + UNREGISTER;

    
    public void start(DatabaseCommands db) throws RemoteException{
        this.dbCmds = db;
        this.clientOutput = new Output();
        this.isRunning = true;
        this.serverId = 0;
        new Thread(this).start();
    }
    
    @Override
    public void run(){
        String username;
        String password;
        String filename;
        
        out.println("Welcome! Type '?' to see a list of available commands.");

        while(isRunning){
            try{
                String command = scan.next();
                switch(command.toUpperCase()){
                    case "?":
                        out.println("Commands list:");
                        out.println(COMMANDS_LIST);
                        break;
                        
                    case "LOGIN":
                        if(serverId == 0){
                            out.print("Username: ");
                            username = scan.next();
                            out.print("Password: ");
                            password = scan.next();
                            serverId = dbCmds.login(clientOutput, new Account(username, password));
                            if(serverId != 0)
                                out.println("Successfully logged in to account " + username + ".");
                        } else {
                            out.println("Hello " + dbCmds.getUsername(serverId)+ ". You are already logged in, please log out to log in.");
                        }
                        break;
                        
                    case "LOGOUT":
                        if(serverId != 0){
                            isRunning = false;
                            dbCmds.logout(serverId);
                            UnicastRemoteObject.unexportObject(clientOutput, false);
                            out.println("Bye bye.");
                        } else {
                            out.println("You have to be logged in to log out.");
                        }
                        break;
                        
                    case "REGISTER":
                        if(serverId == 0){
                            out.print("Desired username: ");
                            username = scan.next();
                            out.print("Desired password: ");
                            password = scan.next();
                            try{
                                serverId = dbCmds.register(clientOutput, new Account(username, password));
                                out.println("Successfully registered account " + username + ".");
                            } catch (InputException e){
                                out.println("Registration failed. Try again.");
                            } 
                        } else {
                            out.println("You are logged in. log out to create a new account.");
                          }
                        break;
                        
                    case "UNREGISTER":
                        out.println("Enter the credentials for the account you wish to remove: ");
                        out.print("Username");
                        username = scan.next();
                        out.print("Password");
                        password = scan.next();
                        try{
                            dbCmds.unregister(username, password);
                            out.println("Successfully unregistered account " + username + ".");
                        } catch (InputException e){
                            out.println("Unregistration failed. Try again.");
                        }
                        break;
                        
                    case "LIST":
                        out.println("Available files: ");
                        List<FileDTO> files = dbCmds.listAccessibleFiles(serverId);
                        for(FileDTO file : files){
                            out.println("File name: "+ file.getFileName() + " | File size: " + Integer.toString(file.getFileSize()) + " | File creator: " + file.getFileOwner() + " | Access: " + file.getFileAccess() + " | Permissions: " + file.getFilePermission());
                        }
                        break;
                    
                    case "UPLOAD":
                        if(serverId != 0){
                            out.print("Please enter name of the file you wish to upload: ");
                            filename = scan.next();
                            try{
                                FileDTO file = createFile(filename);
                                if(file != null){
                                    dbCmds.upload(file);
                                    out.println("Successfully uploaded file " + filename + ".");
                                }
                            } catch (InputException e){
                                out.println("Upload failed. Try again.");
                            }
                        } else {
                            out.println("You can't upload files without being logged in.");
                        }
                        break;
                        
                    case "DOWNLOAD":
                        out.print("Please enter the name of the file you wish to download: ");
                        filename = scan.next();
                        try{
                            dbCmds.download(filename, serverId);
                            out.println("Successfully downloadd file " + filename);
                        } catch (InputException e){
                            out.println("Successfully deleted file " + filename + ".");
                        }
                        break;
                        
                    case "DELETE":
                        out.print("Enter name of file to delete: ");
                        filename = scan.next();
                        try{
                            dbCmds.delete(filename, serverId);
                            out.println("Successfully deleted file " + filename + ".");
                        } catch (InputException e){
                            out.println("Deletion failed. Try again.");
                        }
                        break;
                    default:
                        out.println( command + " is not a valid command.");
                        out.println("Type '?' to see a list of available commands.");
                        break;
                }
            } catch(RemoteException e){
                out.println(e.getMessage());
            } catch (SQLException e){
                out.println(e.getMessage());
            }
        }
            
    }
    
    private FileDTO createFile(String filename){
            try{
                int size = ThreadLocalRandom.current().nextInt(1, 100 + 1);
                String username = dbCmds.getUsername(serverId);
                
                out.print("Upload with 'public' or 'private' access? The default value is private. (public/private) ");
                String fileAccess = scan.next();
                if(!fileAccess.equalsIgnoreCase("public") && !fileAccess.equalsIgnoreCase("private")){
                    fileAccess = "private";
                }
                out.print("Upload with 'read' only, or 'read' and 'write' permissions? (r/w) ");
                String filePermission = scan.next();
                if(!filePermission.equalsIgnoreCase("r") && !filePermission.equalsIgnoreCase("w")){
                    filePermission = "r";
                }
                out.print("Would you like to be notified when this file is downloaded by another user? (yes/no) ");
                String notify = scan.next();
                int notifyAnswer = 0;
                if(!notify.equalsIgnoreCase("yes") && !notify.equalsIgnoreCase("no")){
                    notifyAnswer = 0;
                }
                if(notify.equalsIgnoreCase("yes")){
                    notifyAnswer = 1;
                }

                FileDTO file = new FileDTO(filename, size, username, fileAccess, filePermission, serverId, notifyAnswer );
                return file;
            } catch(RemoteException re){
                out.println("remote exception occured");
                out.println(re.getMessage());
            }
        return null;
    }
    
    private class Output extends UnicastRemoteObject implements Client {
        public Output() throws RemoteException{}
        
        @Override
        public void output(String msg){
            out.println((String) msg);
        }
    }
    
  public class SyncedOutput {
    public synchronized void println(String string){
        System.out.println(string);
    }
    
    public synchronized void print(String string){
        System.out.print(string);
    }
}

}
