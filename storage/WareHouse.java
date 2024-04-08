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


    //Constructors

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

    // Storage methods



    //Group methods


    public void addUserToGroup (String group, String user){

        getStringToGroup().get(group).getUsersSubscribed().add(user);

    }

    public void fillConnectionInfoForGroupParticipant(String group, String username, ConnectionInfo connectionInfo){

        getStringToGroup().get(group).getConnections().put(username, connectionInfo);

    }

    //Init methods

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
            System.out.println(message);
            
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
    }

    //util

    public String getAllGroups() {
        StringBuilder groups = new StringBuilder();
        int count = 1;
        for (String key : stringToGroup.keySet()) {
            groups.append(count).append(". ").append(key).append("\n");
            count++;
        }
        return groups.toString();
    }

    public String getAllUserKeys() {
        StringBuilder keys = new StringBuilder();
        int count = 1;
        for (String key : stringToUser.keySet()) {
            keys.append(count).append(". ").append(key).append("\n");
            count++;
        }
        return keys.toString();
    }

    public static String messageCreator(String message){
    
        return "DataBase:" + message;
        
    }


    //getters and Setters.

    public Map<String, User> getStringToUser() {
        return stringToUser;
    }


    public Map<String, Group> getStringToGroup() {
        return stringToGroup;
    }


    public Map<User, ConnectionInfo> getUserToSocket() {
        return userToSocket;
    }

    
}