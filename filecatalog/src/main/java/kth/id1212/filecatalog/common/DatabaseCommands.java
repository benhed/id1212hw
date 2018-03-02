/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kth.id1212.filecatalog.common;

import java.util.List;

import java.sql.SQLException;
import java.rmi.RemoteException;
import java.rmi.Remote;

import kth.id1212.filecatalog.server.model.FileDTO;
import kth.id1212.filecatalog.server.model.InputException;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public interface DatabaseCommands extends Remote{
    int login(Client clientOutput, Account account) throws RemoteException;
    
    void logout(int serverId) throws RemoteException;
   
    int register(Client clientOutput, Account account) throws RemoteException, InputException;
    
    void unregister(String username, String password) throws RemoteException, InputException;
    
    List<FileDTO> listAccessibleFiles(int serverId) throws RemoteException, SQLException;
    
    void upload(FileDTO file) throws RemoteException, InputException;
    
    String download(String filename, int serverId) throws RemoteException, InputException;
    
    void delete(String filename, int serverId) throws RemoteException, InputException;
    
    void notifyFileDownload(int notifyId, String filename, String username) throws RemoteException;
    
    String getUsername(int serverId) throws RemoteException;
}
