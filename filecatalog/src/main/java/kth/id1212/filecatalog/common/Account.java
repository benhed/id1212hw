/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kth.id1212.filecatalog.common;

import java.io.Serializable;
/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class Account implements Serializable {
    private final String username, password;
    
    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getPassword(){
        return password;
    }
}
