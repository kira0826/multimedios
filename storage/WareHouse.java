package storage;

import java.io.IOException;
import java.util.*;


import users.Group;
import users.User;
import utilities.ConnectionInfo;
/**
 * WareHouse, with singleton approach.
 */
public class WareHouse {

    private static WareHouse instance;
    private Map<String, User> stringToUser;
    private Map<User,ConnectionInfo> userToSocket;
    private Map<String, Group> stringToGroup; 

    private WareHouse() {
        
        this.stringToGroup = new HashMap<>();
        this.stringToUser = new HashMap<>();
        this.userToSocket = new HashMap<>();
    }

    public static WareHouse getInstance() throws IOException {
        if (instance == null) {
            instance = new WareHouse();
        }
        return instance;
    }

    public Map<String, User> getStringToUser() {
        return stringToUser;
    }


    public Map<String, Group> getStringToGroup() {
        return stringToGroup;
    }


    public Map<User, ConnectionInfo> getUserToSocket() {
        return userToSocket;
    }

    public ConnectionInfo getClientInfoConnection(String username){

        try {

            User user = WareHouse.getInstance().getStringToUser().get(username);
            ConnectionInfo connectionInfo = WareHouse.getInstance().getUserToSocket().get(user);

            return connectionInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    
    
    public void initialUserSuscribeToDB(User user) {
        
        try {
            
            
            
            WareHouse.getInstance().getStringToUser().put(user.getUsername(), user);
            WareHouse.getInstance().getUserToSocket().put(user, user.getConnectionInfo() );
            
            String message = messageCreator("Se almacena el usuario " + user.getUsername() + ".");
            
            System.out.println(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public void saveUserConnectionInfo(ConnectionInfo connectionInfo, String user) {
        
        try {
            User userObject = WareHouse.getInstance().getStringToUser().get(user);
            WareHouse.getInstance().getUserToSocket().put(userObject, connectionInfo);
            
            String message = messageCreator("Se almacena connectionInfo de " + user + ".");
            

            
            System.out.println("message");
            
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
    }
    
    public static String messageCreator(String message){
    
        return "DataBase:" + message;
        
    }

    
}