package servers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import storage.WareHouse;
import utilities.PropertiesConfig;


public class Receptionist {
    
    
    private  int port;
    private ServerSocket  serverSocket;
    private Map<String,Socket> userToSocket;
    private Map<String, DedicatedServer> userToDedicatedServer;


    ExecutorService threadpool;

    public static void main(String[] args) {

        try {
            WareHouse.getInstance();
            Receptionist receptionist = new Receptionist();
            receptionist.listen();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }


    public void listen() throws IOException{

        System.out.println("Conectado al Receptionist en el puerto : " + " " + port);


        while (true) {
            
            Socket clientSocket = serverSocket.accept(); 
            System.out.println("Solicitud de cliente recibida.");
            threadpool.execute(new DedicatedServer(clientSocket, this));
        
        }

    }
    //Constructors and getters and setters.


    

    private Receptionist() throws IOException {
        this.port = Integer.parseInt(PropertiesConfig.getProperty("port.receptionist"));
        this.serverSocket = new ServerSocket(port, 50, InetAddress.getByName(PropertiesConfig.getProperty("server.address")));
        threadpool = Executors.newFixedThreadPool(8);
        this.userToSocket = new HashMap<>();
        this.userToDedicatedServer = new HashMap<>();
    }

    public int getPort() {
        return port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Map<String, Socket> getUserToSocket() {
        return userToSocket;
    }
    

    public ExecutorService getThreadpool() {
        return threadpool;
    }


    public Map<String, DedicatedServer> getUserToDedicatedServer() {
        return userToDedicatedServer;
    } 
    
    
}
