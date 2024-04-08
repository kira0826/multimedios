package servers;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

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

            sendMenu();

            while (true) {
                System.out.println("Listo para recibir paquetes. ServidorDedicado:Server:ListenerOfClient");
                Receiver.receivePacket(DedicatedServer.class, in, this);

            }


        }catch (Exception e){

            e.printStackTrace();

        }
    }


    //Messages 

    public void sendMessageToUser (String to,  String ms){

        DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(to);
    
        Sender.senderPacket(dedicatedServer.out, "messageDisplay", ms);
            
    }

    public void sendMessageToGroup (String group,  String ms){


        ArrayList<String> arrayList = null;


        try {
            arrayList = WareHouse.getInstance().getStringToGroup().get(group).getUsersSubscribed();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        for (String participantString : arrayList) {
                
            DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(participantString);
            Sender.senderPacket(dedicatedServer.out, "messageDisplay", ms);
            
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

        while (groupParticipants.size() > selectedGroup.getConnections().size()) {
        int actual = selectedGroup.getConnections().size();

            //System.out.println("Verificando xd: " + "suscritos: " + actual + " esperados: " + groupParticipants.size() );
            
            // agregar elementos al map
            for (String key : selectedGroup.getConnections().keySet()) {
                System.out.println("Usuario registrado: " + key);
            }
            
            if (actual != previous ) {
        
                messageDisplay("Participantes con información de conexión establecida: " + actual);
                previous = actual;

                
            }
            
        }

        for (String participantString : groupParticipants) {
                
            DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(participantString);
            Sender.senderPacket(dedicatedServer.out, "setSenderToCallGroup", selectedGroup.getConnections());
            
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

    public void finishCall(ConcurrentHashMap<String, ConnectionInfo> toFinishCall){

        System.out.println("Integrantes: " + toFinishCall.keySet().toString());

        for (String  participant : toFinishCall.keySet()) {

            DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(participant);
            Sender.senderPacket(dedicatedServer.out, "finishCall", null);

        }       
    }


    public void requestCall(String recipient, String from, Integer port,  String address){


        DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(recipient);

        Sender.senderPacket(dedicatedServer.out, "reciverRequestCall", from, port, address);

    }

    public void callResponse (String from, String sender, Integer port, String address){

        DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(from);

        Sender.senderPacket(dedicatedServer.out, "finalCallConnection", sender, port, address);



    }


    // Audio sender


    public void sendAudio(byte [] audioBytes,String recipient ){
                
        DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(recipient);

        Sender.senderPacket(dedicatedServer.getOut(), "receiveAudio", audioBytes);
        
    }


    public void sendAudioToGroup (byte [] audioBytes, String group){

        ArrayList<String> arrayList = null;


        try {
            arrayList = WareHouse.getInstance().getStringToGroup().get(group).getUsersSubscribed();

        } catch (IOException e) {

            e.printStackTrace();

        }


        for (String participantString : arrayList) {
                
            DedicatedServer dedicatedServer = receptionist.getUserToDedicatedServer().get(participantString);
            Sender.senderPacket(dedicatedServer.out, "receiveAudio", audioBytes);
            
        }

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
                MENU DE COMANDOS

                {Cómo se debe escribir | Qué hace}

                --------------------------------------------------

                COMANDO GENERAL:

                /menu | Solicita el menu de operaciones.

                /usuarios   |   Visualiza los clientes.

                --------------------------------------------------
                COMANDOS PARA LA CREACIÓN DE GRUPO:

                /crearGrupo nombreDeGrupo   | Crea un grupo.

                /verGrupos  |   Visualiza los grupos creados.

                /addUser nombreDeGrupo nombreDeUsuarioA nombreDeUsuarioB ...  

                --------------------------------------------------

                COMANDOS PARA LOS AUDIOS:

                /enviarAudio nombreUsuarioDestino   |   Enviar audio a un usuario.

                /Gaudio nombreDeGrupo | Enviar audio a grupo.

                /audios     | ver historial de audios.

                /reproducir numeroAsociado | Reproduce un audio dado su índice, para ello despliegue el historial de audios.

                --------------------------------------------------

                COMANDOS PARA MENSAJES:

                /msj nombreUsuarioDestino mensaje | Enviar mensajes

                /Gmsj nombreDeGrupo mensaje | Enviar mensaje a grupo 

                /Hmsj   | Ver historial de mensajes.

                --------------------------------------------------

                COMANDOS PARA LLAMADAS:

                /llamar nombreUsuarioDestino    | Llamada uno a uno.

                /callGroup nombreDeGrupo    |   Llamada grupal.

                x   | Escribe x y presiona enter para finalizar una llamada.

                --------------------------------------------------

                Listo para tu petición:

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

    public void sendMenu(){

        Sender.senderPacket(out, "messageDisplay", menu());
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
