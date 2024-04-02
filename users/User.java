package users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import multimedia.Audio;
import utilities.ConnectionInfo;

public class User implements Serializable{

    private String username;
    private ConnectionInfo connectionInfo;
    private List<Audio> audioHistory; 


    

    public User(String username, ConnectionInfo connectionInfo) {
        
        this.username = username;
        this.connectionInfo = connectionInfo;
        this.audioHistory = new ArrayList<>();

    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public List<Audio> getAudioHistory() {
        return audioHistory;
    }
    public void setAudioHistory(List<Audio> audioHistory) {
        this.audioHistory = audioHistory;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    

    
}
