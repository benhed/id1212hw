/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kth.id1212.filecatalog.common;

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public interface Client extends Remote{
    void output(String msg) throws RemoteException;
}
