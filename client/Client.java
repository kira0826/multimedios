package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import packets.PacketFormat;
import protocols.Receiver;
import protocols.Sender;
import utilities.ConnectionInfo;
import utilities.PropertiesConfig;

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

        System.out.println("Por favor escribe el nombre de usuario");
                
            String username = scanner.nextLine(); 

            Sender.senderPacket(out,"initialUserSuscribeToDB" , username);

            Receiver.receivePacket(this.getClass(), in, this);

    }

    private void start () throws IOException{ 
            
        Thread writeMessages = new Thread(() -> {

            String message = "";
                
            while (true) {
                System.out.println("Listo para tu petición: ");
                message = scanner.nextLine();
                try {

                    out.writeObject(message);
                    out.flush();
                    System.out.println("Mensaje enviado");

                } catch (IOException e) {
                    e.printStackTrace();
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
            
        }
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
