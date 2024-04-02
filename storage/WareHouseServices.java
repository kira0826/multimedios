package storage;

import java.io.*;
import java.net.Socket;

import interfaces.IServerANDDataBaseAndClientServices;
import protocols.*;
import users.User;
import utilities.ConnectionInfo;


public class WareHouseServices implements   Runnable{

    private Socket socketClient;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public WareHouseServices(Socket socketClient) {
        
        try {

            this.socketClient = socketClient;
            this.out = new ObjectOutputStream(socketClient.getOutputStream());
            this.in = new ObjectInputStream(socketClient.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {


        while (true) {
            System.out.println("Listo para recibir paquetes. ServidorDedicado:" + messageCreator(""));
            Receiver.receivePacket(WareHouseServices.class, in,this);
            
        }

    }
    
    // Services: 
    
    public Socket getSocketClient() {
        return socketClient;
    }

    
    public void initialUserSuscribeToDB(User user) {

        try {

            

            WareHouse.getInstance().getStringToUser().put(user.getUsername(), user);
            WareHouse.getInstance().getUserToSocket().put(user, user.getConnectionInfo() );

            String message = messageCreator("Se almacena el usuario " + user.getUsername() + ".");

            Sender.senderPacket(out, "messageToResend", message);
            
            if (socketClient.isConnected()) {
                System.out.println("Conectado al socket: PORT:" +  socketClient.getPort() + " ADDRESS:" + socketClient.getInetAddress().getHostAddress());

            }else{
                System.out.println("NO esta conectado.");
            }
            System.out.println("putasssss");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public void saveUserConnectionInfo(ConnectionInfo connectionInfo, String user) {
        
        try {

            User userObject = WareHouse.getInstance().getStringToUser().get(user);
            WareHouse.getInstance().getUserToSocket().put(userObject, connectionInfo);

            String message = messageCreator("Se almacena connectionInfo de " + user + ".");

            Sender.senderPacket(out, "messageToResend", message);

            
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
    }
    
    public static String messageCreator(String message){
        try {
            return "DataBase:" + WareHouse.getInstance().getPort() +": " + message;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    
    }

}
