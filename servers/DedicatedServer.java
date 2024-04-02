package servers;

import java.io.*;
import java.net.*;
import interfaces.*;
import packets.PacketFormat;
import protocols.Receiver;
import protocols.Sender;
import users.User;
import utilities.*;

public class DedicatedServer implements Runnable, IServerService{

    
    private ObjectInputStream in; 
    private ObjectOutputStream out;
    private ObjectInputStream inDB; 
    private ObjectOutputStream outDB;
    
    private Socket socketClient; 
    private Socket socketDB;

    private User userForDedicated;

    public DedicatedServer (Socket socket){

        try {

        this.socketClient = socket;

        this.out = new ObjectOutputStream(socketClient.getOutputStream());
        this.in = new ObjectInputStream(socketClient.getInputStream());

        int portDB  = Integer.parseInt(PropertiesConfig.getProperty("port.db"));
        String address  = PropertiesConfig.getProperty("db.address");
        this.socketDB = new Socket(InetAddress.getByName(address), portDB);

        this.outDB = new ObjectOutputStream(socketDB.getOutputStream());
        this.inDB = new ObjectInputStream(socketDB.getInputStream());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }

    @Override
    public void run() {
        try{

            System.out.println("Servidor dedicado iniciado.");


            Thread clientListener = new Thread(()->{


            // Database income

                while (true) {

                    System.out.println("Listo para recibir paquetes. ServidorDedicado:Server:ListenerOfDB");
                    Receiver.receivePacket(DedicatedServer.class, inDB, this);
                    
                }
                
            });

            clientListener.start();

            //Server Income

            while (true) {
                System.out.println("Listo para recibir paquetes. ServidorDedicado:Server:ListenerOfClient");
                Receiver.receivePacket(DedicatedServer.class, in, this);
            }

        }catch (Exception e){

            e.printStackTrace();

        }
    }

    public void messageToShow(String message){

        System.out.println(message);
    }


    public void messageToResend (String response){

        Sender.senderPacket(out, "messageDisplay", response);

    }

    public void messageDisplay(String message){
        System.out.println(message);
    }


    public void initialUserSuscribeToDB(String username) {

        
        User user = new User(username,
            new ConnectionInfo(socketClient.getPort(),
            socketClient.getInetAddress().getHostAddress()));

        userForDedicated = user;

        Sender.senderPacket(outDB, "initialUserSuscribeToDB", user);


    }

    public void saveUserConnectionInfo(ConnectionInfo connectionInfo, String user) {

        Sender.senderPacket(outDB, "saveUserConnectionInfo", connectionInfo,  user);
    }   
}
