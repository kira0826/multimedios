package storage;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import users.Group;
import users.User;
import utilities.ConnectionInfo;
import utilities.PropertiesConfig;

/**
 * WareHouse, with singleton approach.
 */
public class WareHouse {

    private static WareHouse instance;
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService threadpool;


    private Map<String, User> stringToUser;
    private Map<User,ConnectionInfo> userToSocket;
    private Map<String, Group> stringToGroup; 


    public static void main(String[] args) {
        
        try {
            getInstance().listen();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void listen() throws IOException{

        System.out.println("DataBase:" + " " + port + ": ");
        while (true) {

            Socket clienSocket = serverSocket.accept();
            System.out.println( "DataBase:" + " " + port + ": " + "Solicitud a base de datos recibida.");
            threadpool.execute(new WareHouseServices(clienSocket));
        }
    }

    private WareHouse() throws IOException {
        this.port = Integer.parseInt(PropertiesConfig.getProperty("port.db"));
        this.serverSocket = new ServerSocket(port, 50, InetAddress.getByName(PropertiesConfig.getProperty("db.address")));
        this.stringToUser = new HashMap<String,User>();
        this.userToSocket = new HashMap<User,ConnectionInfo>();
        this.stringToGroup = new HashMap<String,Group>();
        this.threadpool = Executors.newFixedThreadPool(5);

    }

    public static WareHouse getInstance() throws IOException {
        if (instance == null) {
            instance = new WareHouse();
        }
        return instance;
    }


    public int getPort() {
        return port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ExecutorService getThreadpool() {
        return threadpool;
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

    

    
}