package kth.id1212.filecatalog.server.model;

import java.io.Serializable;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class FileDTO implements Serializable {
    private final String filename,  fileAccess, fileOwner, filePermission;
    private final int fileSize, notifyId, notifyValue;
    
    public FileDTO(String filename, int fileSize, String fileOwner, String fileAccess, String filePermission, int notifyId, int notifyValue){
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileOwner = fileOwner;
        this.fileAccess = fileAccess;
        this.filePermission = filePermission;
        this.notifyId = notifyId;
        this.notifyValue = notifyValue;
    }
    
    public String getFileName(){
        return this.filename;
    }
    public int getFileSize(){
        return this.fileSize;
    }
    public String getFileOwner(){
        return this.fileOwner;
    }
    public String getFileAccess(){
        return this.fileAccess;
    }
    public String getFilePermission(){
        return this.filePermission;
    }
    public int getNotifyId(){
        return this.notifyId;
    }
    public int getNotifyValue(){
        return this.notifyValue;
    }
}
