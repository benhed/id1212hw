/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kth.id1212.filecatalog.client.startup;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import kth.id1212.filecatalog.client.view.ViewManager;
import kth.id1212.filecatalog.common.DatabaseCommands;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class Main {
    public static void main(String[] args){
        try{
            DatabaseCommands dbCmds = (DatabaseCommands) Naming.lookup("hw3DB");
            new ViewManager().start(dbCmds);
        } catch (MalformedURLException | RemoteException | NotBoundException e){
            System.err.println("Client couldn't start");
        }
    }
}
