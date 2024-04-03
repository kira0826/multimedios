package servers;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import interfaces.*;
import multimedia.Audio;
import packets.PacketFormat;
import protocolos.Receiver;
import protocolos.Sender;
import storage.WareHouse;
import users.User;
import utilities.*;

public class DedicatedServer implements Runnable{


    private ObjectInputStream in; 
    private ObjectOutputStream out;
    private Socket socketClient; 
    private User userForDedicated;

    public DedicatedServer (Socket socket){

        try {

        this.socketClient = socket;

        this.out = new ObjectOutputStream(socketClient.getOutputStream());
        this.in = new ObjectInputStream(socketClient.getInputStream());

    

            
        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }

    @Override
    public void run() {
        try{

            while (true) {
                System.out.println("Listo para recibir paquetes. ServidorDedicado:Server:ListenerOfClient");
                Receiver.receivePacket(DedicatedServer.class, in, this);

                messageToResend(menu());
            }


        }catch (Exception e){

            e.printStackTrace();

        }
    }

    private String menu(){

        return """
                MENU DE SERVICIOS

                {Cómo se debe escribir | Qué hace}

                1. /enviarAudio nombreUsuarioDestino | Envio de audio. 


                """;
        
    }




    public void sendAudio(byte [] audioBytes,String recipient ){

        
        try {
            
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        ObjectInputStream reader = new ObjectInputStream(bais);
    
        Audio audio = (Audio) reader.readObject();  

        ConnectionInfo connectionInfo = WareHouse.getInstance().getClientInfoConnection(recipient);;

        if (connectionInfo == null) {

            messageToResend(messageCreator("No se pudo obtener la infoConnection de " + recipient, socketClient));
            return;
        }
        
        System.out.println("ADDRESS:" + InetAddress.getByName(connectionInfo.getAddress()));
        System.out.println("PORT:" +connectionInfo.getPort());

        Socket socket = new Socket(InetAddress.getByName(connectionInfo.getAddress()), connectionInfo.getPort());
        

        ObjectOutputStream outClientServer = new ObjectOutputStream(socket.getOutputStream());

        Sender.senderPacket(outClientServer, "receiveAudio", audio);

        System.out.println("Envio de audio para reproducir el hilo dedicado.");

        outClientServer.close();
        socket.close();
        
        } catch (UnknownHostException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();} catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private String messageCreator(String response, Socket socket){

        return "DedicatedServer:" + socket.getLocalPort() + response;
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


    public void initialUserSuscribeToDB(User user) {


        try {
            WareHouse.getInstance().initialUserSuscribeToDB(user);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        userForDedicated = user;

        saveUserConnectionInfo();


    }

    public void saveUserConnectionInfo() {

        try {
            WareHouse.getInstance().saveUserConnectionInfo(userForDedicated.getConnectionInfo(), userForDedicated.getUsername());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }   
}
