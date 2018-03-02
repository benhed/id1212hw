
package kth.id1212.filecatalog.server.integration;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import kth.id1212.filecatalog.common.Account;
import kth.id1212.filecatalog.server.model.FileDTO;
import java.util.List;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class AccountCatalogDAO {
    //account
    private static final String CATALOG = "CATALOG";
    private static final String ACCOUNT = "ACCOUNT";
    //files
    private static final String FILENAME = "FILENAME";
    private static final String SIZE = "SIZE";
    private static final String OWNER = "OWNER";
    private static final String ACCESS = "ACCESS";
    private static final String PERMISSIONS = "PERMISSIONS";
    private static final String NOTIFYVALUE = "NOTIFYVALUE";
    private static final String NOTIFY = "NOTIFY";
    //user
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    //database
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "Bajsranka123";

    //account statements
    private PreparedStatement getUsernameStatement;
    private PreparedStatement registerAccountStatement;
    private PreparedStatement getPasswordStatement;
    private PreparedStatement removeAccountStatement;
    //files statements
    private PreparedStatement getFilesStatement;
    private PreparedStatement uploadFileStatement;
    private PreparedStatement removeFileStatement;
    
    public AccountCatalogDAO(String source) throws RuntimeException{
        try{
            Connection connection = createSource(source);
            prepareStatements(connection);
        } catch (ClassNotFoundException | SQLException e){
            throw new RuntimeException("Error while connecting to database.", e);
        }
    }
    
    private Connection createSource(String source) throws ClassNotFoundException, SQLException{
        Connection connection = connectToDatabase(source);
        Statement statement = connection.createStatement();
        if(!tableExists(connection, ACCOUNT)){
            statement.executeUpdate("CREATE TABLE " + ACCOUNT + " ("+USERNAME+" VARCHAR(32) PRIMARY KEY, " + PASSWORD + " VARCHAR(32))");
        }
        if(!tableExists(connection, CATALOG)){
            statement.executeUpdate("CREATE TABLE " + CATALOG+ " (" + FILENAME + " VARCHAR(32) PRIMARY KEY, " + SIZE + " INT, "
                   + OWNER + " VARCHAR(32), " + ACCESS + " VARCHAR(32), " + PERMISSIONS + " VARCHAR(32), " + NOTIFY + " INT, " + NOTIFYVALUE + " INT)");
        }
        return connection;
    }
    
    private Connection connectToDatabase(String source) throws ClassNotFoundException, SQLException {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + source + "?autoReconnect=true&useSSL=false", MYSQL_USERNAME, MYSQL_PASSWORD);
    }
    
    private boolean tableExists(Connection connection, String tablename) throws SQLException{
        DatabaseMetaData data = connection.getMetaData();
        try(ResultSet result = data.getTables(null, null, null, null)){
            for(; result.next();){
                if(result.getString(3).equalsIgnoreCase(tablename)){
                    return true;
                }
            }
            return false;
        } 
    }
    
    public boolean userExists(String username) throws RuntimeException{
        try{
            getUsernameStatement.setString(1, username);
            ResultSet rs = getUsernameStatement.executeQuery();
            if(rs.next()){
                if(rs.getString(1).equals(username)){
                    return true;
                }
            }  
                
            return false;          
        } 
        catch (SQLException e){
            throw new RuntimeException("Woopsie! We are aware of this problem, the pixies are fixing it. Hold tight!", e);
        }
    }
    
    public boolean loginCheck(String username, String password) throws RuntimeException{
        try{
            if(userExists(username)){
                getPasswordStatement.setString(1, username);
                ResultSet passwordInDB = getPasswordStatement.executeQuery();
                if(passwordInDB.next()){
                    if(passwordInDB.getString(1).equals(password)){
                        return true;
                    }
                }    
            }
            else{
                System.out.println("No account with that username exists.");
            }
            return false;
        } catch (Exception e){
            throw new RuntimeException("Something went wrong.", e);
        }
        
    }
    
    public void register(Account account) throws RuntimeException{
        try{            
            registerAccountStatement.setString(1, account.getUsername());
            registerAccountStatement.setString(2, account.getPassword());
            registerAccountStatement.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Somehow we could not register that account. Shame on us!", e);
        } catch(RuntimeException e){
            throw new RuntimeException("What happened? huh?", e);
        }
    }
    
    public void unregister(String username){
        try{
            removeAccountStatement.setString(1, username);
            removeAccountStatement.executeUpdate();
        } catch (SQLException e){
            System.out.println("Error whilst unregistering that account.");
        }
    }
    
    public List<FileDTO> listFiles() throws SQLException{
        List<FileDTO> files = new ArrayList<>();
        try(ResultSet file = getFilesStatement.executeQuery()){
            while(file.next()){
                files.add(new FileDTO(file.getString(1), file.getInt(2), file.getString(3), file.getString(4), file.getString(5), file.getInt(6),file.getInt(7)));
            }
        } catch (SQLException e){
            throw new RuntimeException("Our pixies could not list the files. They don't get paid much, it's okay.", e);
        }
        return files;
    }
    
    public void addFile(FileDTO file){
        try{
            uploadFileStatement.setString(1, file.getFileName());
            uploadFileStatement.setInt(2, file.getFileSize());
            uploadFileStatement.setString(3, file.getFileOwner());
            uploadFileStatement.setString(4, file.getFileAccess());
            uploadFileStatement.setString(5, file.getFilePermission());
            uploadFileStatement.setInt(6, file.getNotifyId());
            uploadFileStatement.setInt(7, file.getNotifyValue());

            uploadFileStatement.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("''HEY! YEAH YOU! ADD THAT FILE FOR THIS GENTLEMAN THIS INSTANT! What are you saying?! You can't?! WELL YOU'RE FIRED! HOW BOW DAH!?''", e);
        }
    }
    
    public void removeFile(FileDTO file){
        try{
            removeFileStatement.setString(1, file.getFileName());
            removeFileStatement.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Somehow we could not delete that file for you. Try again.", e);
        }
    }
    
    public FileDTO getFile(String filename){
        try{
            List<FileDTO> files = listFiles();
            for(FileDTO file : files){
                if(file.getFileName().equals(filename)){
                    return file;
                }
            }
            return null;
        } catch(SQLException e){
            throw new RuntimeException("We did our best to get that file for you, but we failed, sadly.");
        }
    }
    
    public boolean fileExists(String filename){
        try{
           List<FileDTO> files = listFiles();
           for(FileDTO file : files){
               if(file.getFileName().equals(filename)){
                   return true;
               }
           }
           return false;
        } catch (SQLException e){
            throw new RuntimeException("Our pixies could not locate that file. Apparently he broke his leg on the way to the file stash. Try again and another pixie might do a better job.", e);
        }
    }
    
    private void prepareStatements(Connection connection) throws SQLException{
        getUsernameStatement = connection.prepareStatement("SELECT " + USERNAME + " FROM " + ACCOUNT + " WHERE " + USERNAME + "=?");
        registerAccountStatement = connection.prepareStatement("INSERT INTO " + ACCOUNT + " VALUES (?, ?)");
        removeAccountStatement = connection.prepareStatement("DELETE FROM " + ACCOUNT + " WHERE username=?");
        getPasswordStatement = connection.prepareStatement("SELECT " + PASSWORD + " FROM " + ACCOUNT + " WHERE " + USERNAME + "=?");
        
        getFilesStatement = connection.prepareStatement("SELECT * FROM " + CATALOG);
        uploadFileStatement = connection.prepareStatement("INSERT INTO " + CATALOG + " VALUES (?, ?, ?, ?, ?, ?, ?)");
        removeFileStatement = connection.prepareStatement("DELETE FROM " + CATALOG + " WHERE filename=?");
    }
}
