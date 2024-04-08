package client;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.*;

import multimedia.*;
import protocolos.*;
import users.User;
import utilities.*;

public class Client {

    private Socket socket; 
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner;
    private User user; 
    private AtomicBoolean onCall;
    private ConcurrentHashMap<String, ConnectionInfo> groupConnections;



    public Client() throws IOException  {

        this.socket = new Socket(PropertiesConfig.getProperty("server.address"),
                        Integer.parseInt(PropertiesConfig.getProperty("port.receptionist")));
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.scanner = new Scanner(System.in);
        this.onCall = new AtomicBoolean(true);
        this.groupConnections = new ConcurrentHashMap<>();
    }



    public static void main(String[] args) {
        try {

            Client client = new Client();
            client.init();
            client.start();

        } catch (IOException e) {
            e.printStackTrace();

        } catch(Exception e ){
            e.printStackTrace();
        }
    }

    private void init () throws IOException {


        System.out.println("Por favor escribe el nombre de usuario");

                
            String username = scanner.nextLine();
            
            User user = new User(username, new ConnectionInfo(socket.getLocalPort(), socket.getLocalAddress().getHostAddress()));

            this.user = user;

            Sender.senderPacket(out,"initialUserSuscribeToDB" , user);

            
    }

    private void start () throws IOException{ 
            
        Thread writeMessages = new Thread(() -> {

            String messagev = "";
                
            while (true) {

                messagev = scanner.nextLine();

                String [] messageDiv = messagev.split(" ");
                
                if (messageDiv[0].equals("/enviarAudio")) {

                    
                    sendAudio(messageDiv[1], false);
                    
                }else if (messageDiv[0].equals("/llamar")) {

                requestCall(messageDiv[1]);   

                }else if (messageDiv[0].equals("/crearGrupo")){

                    createGroup(messageDiv[1]);

                }else if (messageDiv[0].equals("/addUser")){

                    for (int i = 2; i < messageDiv.length; i++) {

                        addUserToGroup(messageDiv[1], messageDiv[i]);
                        
                    }

                }else if (messageDiv[0].equals("/verGrupos")){

                    Sender.senderPacket(out, "getAllGroups", null);

                }else if (messageDiv[0].equals("/usuarios")){

                    Sender.senderPacket(out, "usersDisplay", null);

                }else if (messageDiv[0].equals("/callGroup")){

                    requestGroupCall(messageDiv[1]);

                }else if (messageDiv[0].equals("x")){

                    intialCallFinisher();
                }else if (messageDiv[0].equals("/reproducir")){
                    
                    reproduceAudioSelected(Integer.parseInt(messageDiv[1]));
                }else if (messageDiv[0].equals("/audios")){
                    
                    user.listAudioHistory();


                }else if (messageDiv[0].equals("/Hmsj")){
                    
                    user.listMessageHistory();

                }else if (messageDiv[0].equals("/Gaudio")){
                    
                    sendAudio(messageDiv[1], true);

                }else if (messageDiv[0].equals("/msj")){

                    String msj = "";

                    for (int i = 2; i < messageDiv.length; i++) {

                        msj+= messageDiv[i] + " ";
                
                    }
                    
                    sendMessage(messageDiv[1], msj, false);

                }else if (messageDiv[0].equals("/Gmsj")){

                    String msj = "";

                    for (int i = 2; i < messageDiv.length; i++) {

                        msj+= messageDiv[i] + " ";
                
                    }

                    sendMessage(messageDiv[1], msj, true);
                }else if (messageDiv[0].equals("/menu")){
                    
                    menu();

                }else{

                    System.out.println("Comando incorrecto :(");
                }

                try {
                    Thread.sleep(1000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Listo para tu petición: ");


            }
            });

            writeMessages.start();

            while (true) {

            Receiver.receivePacket(this.getClass(), in, this);
        }
    }

    public void menu(){

        Sender.senderPacket(out, "sendMenu", null);
    }


    //Mensajes 


    public void sendMessage(String to,  String message, boolean toGroup){

        String header = user.getUsername() + ": " + message;

        Message ms = new Message(header, user.getUsername(), to);

        user.getMessages().add(ms);

        if (toGroup) {

            Sender.senderPacket(out, "sendMessageToGroup", to, header);
            
        }else Sender.senderPacket(out, "sendMessageToUser",to,  header);

    }



    // Group creation methods

    public void createGroup (String groupName){

        Sender.senderPacket(out, "createGroup", groupName);

    }


    public void addUserToGroup (String groupName, String user ){

        Sender.senderPacket(out, "addUserToGroup", groupName, user);

    }

    public void finishCall(){


        System.out.println("Seteo falso no incial");
        onCall.set(false);

    }

    

    // Group call methods

    public void intialCallFinisher(){

        onCall.set(false);

        
            for (String user : groupConnections.keySet()) {
                System.out.println(user);
            }
        

        Sender.senderPacket(out, "finishCall", groupConnections);

    }


    public void setSenderToCallGroup(ConcurrentHashMap<String, ConnectionInfo> connections){

        groupConnections = connections;

        onCall.set(true);


        Thread calling  = new Thread(()-> {

                try {
                    senderCall(null, -1,connections);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            calling.start();

    } 

    public void setGroupDatagramSocketToReceiveInfo(String fromGroupName){

        onCall.set(true);

        try{
            DatagramSocket datagramSocket =  new DatagramSocket(0);

            Integer portMine = datagramSocket.getLocalPort();
            String addressMine = datagramSocket.getLocalAddress().getHostName();

            Sender.senderPacket(out, "fillGroupConnectionInfo", fromGroupName, user.getUsername(),  portMine, addressMine);

            Thread receiver = new Thread(() -> {

                callReceiver(datagramSocket);

            });

            receiver.start();

        }catch(IOException e){

            e.printStackTrace();
        }
    }

    public void requestGroupCall(String group){

        onCall.set(true);

        
        try {
            DatagramSocket datagramSocket =  new DatagramSocket(0);

            Integer port = datagramSocket.getLocalPort();
            String address = datagramSocket.getLocalAddress().getHostName();

            Sender.senderPacket(out, "requestGroupCall", group, port, address);

            Thread caller = new Thread(() -> {

                callReceiver(datagramSocket);

            });

            caller.start();

        } catch (SocketException e) {
            e.printStackTrace();
        }
        

    }





    // One to One Call Methods


    public void finalCallConnection(String from, Integer port,  String address){

            groupConnections.put(from, new ConnectionInfo(port, address));

            Thread calling  = new Thread(()-> {

                try {
                    senderCall(address, port,null);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

            calling.start();
        
    } 


    public void reciverRequestCall (String sender, Integer port, String  address){

        groupConnections.put(sender, new ConnectionInfo(port, address));



        try{
            DatagramSocket datagramSocket =  new DatagramSocket(0);

            Integer portMine = datagramSocket.getLocalPort();
            String addressMine = datagramSocket.getLocalAddress().getHostName();

            Sender.senderPacket(out, "callResponse", sender, user.getUsername(),  portMine, addressMine);

            Thread receiver = new Thread(() -> {

                callReceiver(datagramSocket);

            });
            receiver.start();

            Thread calling = new Thread(() ->{
                
                try {
                    senderCall(address, port,null );
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            });
            calling.start();




        }catch(IOException e){

            e.printStackTrace();
        }
    }



    public void requestCall(String recipient){

        
        try {
            DatagramSocket datagramSocket =  new DatagramSocket(0);

            Integer port = datagramSocket.getLocalPort();
            String address = datagramSocket.getLocalAddress().getHostName();

            Sender.senderPacket(out, "requestCall", recipient, user.getUsername(), port, address);

            System.out.println("Despues de enviar.");
            
            Thread caller = new Thread(() -> {

                callReceiver(datagramSocket);

            });

            caller.start();



        } catch (SocketException e) {
            e.printStackTrace();
        }
        

    }


    // Functional call


    public void callReceiver(DatagramSocket socket){

        System.out.println("Entro en el call receiver");
        onCall.set(true);

        try {
            final int BUFFER_SIZE = 1024 + 4;

            byte[] buffer = new byte[BUFFER_SIZE];

            // Configurar el reproductor de audio
            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
            SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            System.out.println("despues del dataline");
            PlayerThread playerThread = new PlayerThread(audioFormat,BUFFER_SIZE, onCall);

            System.out.println("Boolean:" + onCall.get());
            playerThread.start();
            System.out.println("Desdepues del player start");
            // Recibir los paquetes y reproducir el audio
            int count = 0;
            while (onCall.get()) {

                System.out.println("aca en el while de arriba");

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                System.out.println("antes del receive");
                socket.receive(packet);
                System.out.println("Despues del receive");

                buffer = packet.getData();
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                int packetCount = byteBuffer.getInt();
                if (packetCount == -1) {
                    //System.out.println("Received last packet " + count);
                    break;
                } else {

                    if (!onCall.get()) break;

                    byte[] data = new byte[1024];
                    byteBuffer.get(data, 0, data.length);
                    // System.arraycopy(buffer, 0, data, 0, data.length);
                    playerThread.addBytes(data);
                    //System.out.println("Received packet " + packetCount + " current: " + count);
                    System.out.println("aca en el while de abajo");

                }
                count++;
            }

            playerThread.join();

            System.out.println("Finaliza el callReceiver.");
            sourceDataLine.close();
            socket.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public  void senderCall(String ip, int port, ConcurrentHashMap<String, ConnectionInfo> map) throws Exception {
        onCall.set(true);


        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        
        // Obtener la línea de entrada del micrófono
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);

        // Abrir la línea de entrada del micrófono y comenzar la captura de audio
        line.open(format);
        line.start();

        System.out.println("Capturando audio del micrófono...");

        SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(format);
        sourceDataLine.open(format);
        sourceDataLine.start();

        // Buffer para almacenar los datos de audio capturados
        byte[] buffer = new byte[1024];
        int bytesRead;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1028);

        DatagramSocket socket = new DatagramSocket();



        while (onCall.get()) {
            byteBuffer.clear();
            bytesRead = line.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                byteBuffer.putInt(bytesRead);
                byteBuffer.put(buffer, 0, bytesRead);

                if (map == null ){
                    sendAudioCall(ip, port, byteBuffer.array(), socket);
                }else{

                    sendAudioToGroup(map, byteBuffer.array(), socket);
                }
            }
        }
        line.close();
        socket.close();
    }

    public  void sendAudioCall(String ip, int port, byte[] audioData, DatagramSocket socket) throws Exception {
        InetAddress address = InetAddress.getByName(ip);
        DatagramPacket packet = new DatagramPacket(audioData, audioData.length, address, port);
        socket.send(packet);
    }

    public void sendAudioToGroup(ConcurrentHashMap<String, ConnectionInfo> connections,byte[] audioData, DatagramSocket socket ) throws Exception {


        for (ConnectionInfo cInfo : connections.values()) {

            InetAddress address = InetAddress.getByName(cInfo.getAddress());
            DatagramPacket packet = new DatagramPacket(audioData, audioData.length, address, cInfo.getPort());
            socket.send(packet);
   
        }
    }

// Envio de audios:


    public void reproduceAudioSelected (int position){


        Audio audio = user.getAudioHistory().get(position);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(audio);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        byte [] audioBytesSerialize =  outputStream.toByteArray();

        receiveAudio(audioBytesSerialize);

    }

    public void sendAudio(String recepientName, boolean toGroup){

        try{

        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        
        Audio  audio = recordAudio(audioFormat);

        audio.setTo(recepientName);
        audio.setFrom(user.getUsername());

        user.getAudioHistory().add(audio);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(audio);

        byte [] audioBytesSerialize =  outputStream.toByteArray();
            

        if (toGroup) {

            Sender.senderPacket(out, "sendAudioToGroup", audioBytesSerialize , recepientName);
            
        }else Sender.senderPacket(out, "sendAudio", audioBytesSerialize , recepientName);
        


        }catch(IOException exception){
            exception.printStackTrace();
        }
    }

    public void receiveAudio(byte [] audioBytes) {


        
        try{

        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        ObjectInputStream reader = new ObjectInputStream(bais);
    
        Audio audio = (Audio) reader.readObject();  

        System.out.println("Reproduciendo audio...");
        AudioFormat audioFormat = audio.getAudioFormatWrapper().toAudioFormat();

        
        Queue <byte[]> queue = audio.getQueueCopy();

        SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        
        int counter=0;

        while (!queue.isEmpty()) {
            byte[] bytes = queue.poll();
            sourceDataLine.write(bytes, 0, bytes.length);
            counter++;
        }

        System.out.println("Reproducción finalizada.");
        sourceDataLine.close();
    }catch (LineUnavailableException e){
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    
    }

    public Audio recordAudio(AudioFormat audioFormat) {

        try {

            System.out.println("Se inicia la grabación del audio.");
            System.out.println("Presiona 0 y enter para parar la grabación y enviar el audio.");

            
            TargetDataLine line = AudioSystem.getTargetDataLine(audioFormat);
            line.open(audioFormat);
            line.start();

            byte[] buffer = new byte[10000];
            LinkedList <byte[]> queue = new LinkedList<>();

            AtomicBoolean recording = new AtomicBoolean(true);

            Thread recordingThread = new Thread(() -> {
                while (recording.get()) {
                    int byteRead = line.read(buffer, 0, buffer.length);
                    if (byteRead > 0) {
                        byte[] audioData = new byte[byteRead];
                        System.arraycopy(buffer, 0, audioData, 0, byteRead);
                        queue.add(audioData);
                    }

                }
                System.out.println("Recording stoped");

            });

            recordingThread.start();

            String input = "";
            
            while (scanner.hasNextLine() && !(input = scanner.nextLine()).equals("0")) {

            }

            if (input.equals("0")) {
                recording.set(false);
            }

            recordingThread.join();
            line.close();
            
            System.out.println("Se encapsula el audio para ser enviado por TCP.");
            return new Audio(queue, AudioFormatWrapper.fromAudioFormatToWrapper(audioFormat) );

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return  null;
        
    }

    public void messageDisplay(String message){

        System.out.println(message);

    }


    public ObjectOutputStream getOut() {
        return out;
    }



    public ObjectInputStream getIn() {
        return in;
    }

    

    public Scanner getScanner() {
        return scanner;
    }

    public AtomicBoolean getOnCall() {
        return onCall;
    }



    public Socket getSocket() {
        return socket;
    }



    public User getUser() {
        return user;
    }



    public ConcurrentHashMap<String, ConnectionInfo> getGroupConnections() {
        return groupConnections;
    }



    
    


    
}
