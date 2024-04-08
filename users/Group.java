package users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import utilities.ConnectionInfo;

public class Group implements Serializable {

    private String name;
    private ArrayList<String> usersSubscribed;
// En tu clase WareHouse
    private ConcurrentHashMap<String, ConnectionInfo> connections = new ConcurrentHashMap<>();
    

    //constructors

    public Group(String name) {

        this.name = name;
        this.usersSubscribed = new ArrayList<>();
        this.connections = new ConcurrentHashMap<>();
    }

    //getters and setters

    public ArrayList<String> getUsersSubscribed() {
        return usersSubscribed;
    }
    
    

    public void addParticipant(String user){

        getUsersSubscribed().add(user);
    }

    
    public String getUsersWithPosition() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < usersSubscribed.size(); i++) {
            sb.append(i + 1).append(". ").append(usersSubscribed.get(i)).append("\n");
        }
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public ConcurrentHashMap<String, ConnectionInfo> getConnections() {
        return connections;
    }
}
