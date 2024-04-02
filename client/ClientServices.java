package client;

import interfaces.IClientUtil;
import interfaces.IServerWithClientServices;
import multimedia.*;
import packets.PacketFormat;
import protocols.Receiver;

import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.*;

public class ClientServices implements  IServerWithClientServices, IClientUtil, Runnable{


    private Scanner scanner;
    private ObjectInputStream in; 
    private ObjectOutputStream out;
    private Socket clienSocket;

    public ClientServices(Socket clientSocket) throws IOException {
        
        this.scanner = new Scanner(System.in);

        this.clienSocket = clientSocket;

        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.in = new ObjectInputStream(clientSocket.getInputStream());

    }

    @Override
    public void run() {


        while (true) {
            
            Receiver.receivePacket(Client.class, in, this);

        }

        
    }
    
    @Override
    public void sendAudio(String username, Audio audio) {

        PacketFormat packet = new PacketFormat("CHANGE THIS!!!", audio, username);
 
        try {
            out.writeObject(packet);
            out.flush();
            String response = (String) in.readObject();
            out.writeObject(response);
            out.flush();
                
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Audio recordAudio(AudioFormat audioFormat) {

        try {

            System.out.println("Se inicia la grabación del audio.");
            System.out.println("Presiona 0 y enter para parar la grabación y enviar el audio.");

            
            TargetDataLine line = AudioSystem.getTargetDataLine(audioFormat);
            line.open(audioFormat);
            line.start();

            byte[] buffer = new byte[10000];
            Queue <byte[]> queue = new LinkedList<>();

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
    

    @Override
    public void recieveAudio(Audio audio) {

        System.out.println("Reproduciendo audio...");
        try{
        AudioFormat audioFormat = audio.getAudioFormatWrapper().toAudioFormat();
        Queue <byte[]> queue = audio.getQueue();

        SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        
        int counter=0;

        while (!queue.isEmpty()) {
            byte[] bytes = queue.poll();
            sourceDataLine.write(bytes, 0, bytes.length);
            counter++;
            System.out.println("Paquetes leidos: " + counter);
        }

        System.out.println("Reproducción finalizada.");
        sourceDataLine.close();
    }catch (LineUnavailableException e){
        e.printStackTrace();
    }
    }

    public void messageSDisplay (String message){
        System.out.println("Mensaje recibido:" + message);
    }


    public void saveUserConnectionInfo(){

        

    }


}