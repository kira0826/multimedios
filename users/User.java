package users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import multimedia.Audio;
import multimedia.Message;
import utilities.ConnectionInfo;

public class User implements Serializable{

    private static final long serialVersionUID = 1L; // It's a good practice to include a serialVersionUID for Serializable classes
    private String username;
    private ConnectionInfo connectionInfo;
    private List<Audio> audioHistory; 
    private List<Message> messages; 

    public User(String username, ConnectionInfo connectionInfo) {
        
        this.username = username;
        this.connectionInfo = connectionInfo;
        this.audioHistory = new ArrayList<>();
        this.messages = new ArrayList<>();

    }


    

    public void listAudioHistory() {
        for (int i = 0; i < audioHistory.size(); i++) {
            Audio audio = audioHistory.get(i);
            System.out.println("Audio #" + (i ) + ")" + " | Fuente: " + audio.getFrom()+ " | To: "  + audio.getTo());
        }
    }

    public void listMessageHistory() {
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            System.out.println("Mensaje #" + (i ) + ") " + message.getValue()  + " | Fuente: " + message.getFrom()+ " | To:"  + message.getTo());
        }
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

    
    public List<Message> getMessages() {
        return messages;
    }

    

    
}
