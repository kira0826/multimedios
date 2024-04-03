package utilities;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionInfo implements Serializable {
    private static final long serialVersionUID = 1L; 

    private int port; 
    private String address;


    public ConnectionInfo(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public Socket toSocket(){
        try {
            return new Socket(InetAddress.getByName(getAddress()),getPort());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    } 


    public int getPort() {
        return port;
    }


    public String getAddress() {
        return address;
    }

    

    
}
