package servers;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import protocolos.Receiver;
import protocolos.Sender;
import storage.WareHouse;
import users.Group;
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

    

    //Group creation 

    public void createGroup(String groupname){

        try {
            WareHouse.getInstance().getStringToGroup().put(groupname, new Group(groupname));

            messageToResend("grupo " + groupname + " creado.");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void addUserToGroup(String group, String user){

        try {

            if (!WareHouse.getInstance().getStringToGroup().isEmpty()) {
                
                
                WareHouse.getInstance().getStringToGroup().get(group).addParticipant(user);

                messageToResend("usuario " + user + " añadidio al grupo " + group + ".");

            }else messageToResend("por el momento no hay grupos creados.");



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    

    //Group call methods


    

    public void requestGroupCall(String group, Integer port, String address ){


        try {
            WareHouse.getInstance().fillConnectionInfoForGroupParticipant(group, userForDedicated.getUsername(), new ConnectionInfo(port, address));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Group selectedGroup = null;

        try {
            selectedGroup = WareHouse.getInstance().getStringToGroup().get(group);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
        /* for (String key : selectedGroup.getConnectionInfoForGroupOperations().keySet()) {
            System.out.println("Usuario registrado: " + key);
        } */

        


        ArrayList<String> groupParticipants =  selectedGroup.getUsersSubscribed();

        System.out.println( groupParticipants.toString());

        for (String participantString : groupParticipants) {

            if (!participantString.equals(userForDedicated.getUsername())) {
                
                DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(participantString);
    
                Sender.senderPacket(dedicatedServer.out, "setGroupDatagramSocketToReceiveInfo", group);
                
            }
            
        }

        int previous = 0;

        while (groupParticipants.size() > selectedGroup.getConnectionInfoForGroupOperations().size()) {
        int actual = selectedGroup.getConnectionInfoForGroupOperations().size();

            //System.out.println("Verificando xd: " + "suscritos: " + actual + " esperados: " + groupParticipants.size() );
            
            // agregar elementos al map
            for (String key : selectedGroup.getConnectionInfoForGroupOperations().keySet()) {
                System.out.println("Usuario registrado: " + key);
            }
            
            if (actual != previous ) {
        
                messageDisplay("Participantes con información de conexión establecida: " + actual);
                previous = actual;

                
            }
            
        }

        for (String participantString : groupParticipants) {
                
            DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(participantString);
            Sender.senderPacket(dedicatedServer.out, "setSenderToCallGroup", selectedGroup.getConnectionInfoForGroupOperations());
            
        }
    }


    public void fillGroupConnectionInfo(String group, String user, Integer port, String address){

        try {
            WareHouse.getInstance().fillConnectionInfoForGroupParticipant(group,user, new ConnectionInfo(port, address) );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    //One to one methods.

    public void finishCall(String participant){

        DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(participant);
        Sender.senderPacket(dedicatedServer.out, "finishCall", selectedGroup.getConnectionInfoForGroupOperations());
       
    }


    public void requestCall(String recipient, String from, Integer port,  String address){


        DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(recipient);

        Sender.senderPacket(dedicatedServer.out, "reciverRequestCall", from, port, address);

    }

    public void callResponse (String from, Integer port, String address){

        DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(from);

        Sender.senderPacket(dedicatedServer.out, "finalCallConnection", port, address);



    }


    // Audio sender


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

    //Message util

    private String menu(){

        return """
                MENU DE SERVICIOS

                {Cómo se debe escribir | Qué hace}

                /enviarAudio nombreUsuarioDestino

                /audioGrupo nombreDeGru

                /llamar nombreUsuarioDestino

                /callGroup nombreDeGrupo

                /mensaje nombreUsuarioDestino

                /mensajeGrupo nombreDeGrupo

                /crearGrupo nombreDeGrupo

                /addUser nombreDeGrupo nombreDeUsuario nombreDeUsuario.. 

                /verGrupos

                /usuarios

                /finishCall

                """;
        
    }


    public void getAllGroups (){

        try {

            if (WareHouse.getInstance().getStringToGroup().isEmpty()) {

                messageToResend("Por el momento no hay grupos.");


            }else {
                
                messageToResend(WareHouse.getInstance().getAllGroups());

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void usersDisplay (){

        try {

            if (WareHouse.getInstance().getStringToUser().isEmpty()) {

                messageToResend("Por el momento no hay usuarios.");

            }else messageToResend(WareHouse.getInstance().getAllUserKeys());

        } catch (IOException e) {
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

    //getters and setters

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
