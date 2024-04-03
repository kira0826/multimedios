package client;


import multimedia.*;
import packets.PacketFormat;

import java.io.*;
import java.net.Socket;

import java.util.*;
import javax.sound.sampled.*;

import protocolos.*;

public class ClientServices implements   Runnable{


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
        System.out.println("EN PETICIÓN DEDICADA");
        Receiver.receivePacket(Client.class, in, this);
    }
    
    
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

    public void receiveAudio(Audio audio) {

        System.out.println("Reproduciendo audio...");
        try{
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