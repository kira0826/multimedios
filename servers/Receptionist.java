package servers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import utilities.PropertiesConfig;


public class Receptionist {
    
    
    private  int port;
    private ServerSocket  serverSocket;

    ExecutorService threadpool;

    public static void main(String[] args) {

        try {
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
            threadpool.execute(new DedicatedServer(clientSocket));
        
        }

    }
    //Constructors and getters and setters.

    public Receptionist() throws IOException {
        this.port = Integer.parseInt(PropertiesConfig.getProperty("port.receptionist"));
        this.serverSocket = new ServerSocket(port, 50, InetAddress.getByName(PropertiesConfig.getProperty("server.address")));
        threadpool = Executors.newFixedThreadPool(8);
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
    
    
}
