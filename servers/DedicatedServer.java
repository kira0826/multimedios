package servers;

import java.io.*;
import java.net.*;

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
    private Receptionist receptionist;

    public DedicatedServer (Socket socket ,Receptionist receptionist){

        try {

        this.socketClient = socket;

        this.out = new ObjectOutputStream(socketClient.getOutputStream());
        this.in = new ObjectInputStream(socketClient.getInputStream());
        this.receptionist = receptionist;
        
    
            
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
            

        ConnectionInfo connectionInfo = WareHouse.getInstance().getClientInfoConnection(recipient);;

        if (connectionInfo == null) {

            messageToResend(messageCreator("No se pudo obtener la infoConnection de " + recipient, socketClient));
            return;
        }


        
        System.out.println("ADDRESS:" + InetAddress.getByName(connectionInfo.getAddress()));
        System.out.println("PORT:" +connectionInfo.getPort());
            
        DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(recipient);

        System.out.println("previo envio");
        Sender.senderPacket(dedicatedServer.getOut(), "receiveAudio", audioBytes);
        System.out.println("Se envia");
        
        } catch (UnknownHostException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();} 


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
            receptionist.getUserToSocket().put(user.getUsername(), socketClient);
            receptionist.getUserToDedicatedServer().put(user.getUsername(), this);

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

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public Socket getSocketClient() {
        return socketClient;
    }

    public User getUserForDedicated() {
        return userForDedicated;
    }

    public Receptionist getReceptionist() {
        return receptionist;
    }   
    
}
