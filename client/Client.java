package client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.*;

import multimedia.*;
import protocolos.*;
import users.User;
import utilities.*;

public class Client {

    private ServerSocket serverSocket;
    private Socket socket; 

    private ConnectionInfo serverConnectionInfo;
    private ExecutorService threaadpool; 
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner;
    


    public Client() throws IOException  {

        this.socket = new Socket(PropertiesConfig.getProperty("server.address"),
                        Integer.parseInt(PropertiesConfig.getProperty("port.receptionist")));
        this.threaadpool = Executors.newFixedThreadPool(5);

        this.serverSocket = new ServerSocket(0);

        this.serverConnectionInfo = new ConnectionInfo(serverSocket.getLocalPort(), serverSocket.getInetAddress().getHostAddress());
        
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.scanner = new Scanner(System.in);
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

        System.out.println("Se establece el servidor:" + "port:" + serverSocket.getLocalPort() + ":address:" + serverSocket.getInetAddress().getHostAddress());

        System.out.println("Por favor escribe el nombre de usuario");

                
            String username = scanner.nextLine();
            
            User user = new User(username, serverConnectionInfo);

            Sender.senderPacket(out,"initialUserSuscribeToDB" , user);

            
    }

    private void start () throws IOException{ 
            
        Thread writeMessages = new Thread(() -> {

            String message = "";
                
            while (true) {
                System.out.println("Listo para tu petición: ");
                message = scanner.nextLine();

                String [] messageDiv = message.split(" ");
                
                if (messageDiv[0].startsWith("/enviarAudio")) {

                    
                    sendAudio(messageDiv[1]);

                    System.out.println("Enviar audiooo falga");
                    
                }


            }

            });

            writeMessages.start();

            //Recepción de mensajes. 

            Thread messageReceptionThread = new Thread(() -> {

                try {

                    System.out.println(messageCreator("En escucha de peticiones."));
                    serverSocket.accept();
                    System.out.println(messageCreator("Solucitud recibida."));
                    threaadpool.execute(new ClientServices(getSocket()));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            messageReceptionThread.start();



            while (socket.isConnected()) {

            Receiver.receivePacket(this.getClass(), in, this);
            System.out.println("oppaaaa");
        }
    }


    // Services

    public void sendAudio(String recepientName){

        try{

        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        
        Audio  audio = recordAudio(audioFormat);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(audio);

        byte [] audioBytesSerialize =  outputStream.toByteArray();
            

        System.out.println("PREVIO AL ENVIO");

        Sender.senderPacket(out, "sendAudio", audioBytesSerialize , recepientName);

        System.out.println("BUEN ENVÍO");

        }catch(IOException exception){
            exception.printStackTrace();
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

    private String messageCreator (String message){

        return "ClientServer:" + getServerConnectionInfo().getPort() + ": "+ message;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }



    public Socket getSocket() {
        return socket;
    }



    public ConnectionInfo getServerConnectionInfo() {
        return serverConnectionInfo;
    }



    public ExecutorService getThreaadpool() {
        return threaadpool;
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

    


    
}
