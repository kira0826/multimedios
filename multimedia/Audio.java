package multimedia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Audio implements Serializable {


    private LinkedList <byte[]> queue;
    private AudioFormatWrapper audioFormatWrapper;
    private String to;
    private String from;
    
    
    public LinkedList<byte[]> getQueueCopy() {
        LinkedList<byte[]> copy = new LinkedList<>();
        for (byte[] data : queue) {
            byte[] dataCopy = Arrays.copyOf(data, data.length);
            copy.add(dataCopy);
        }
        return copy;
    }


        /** 
      // Método para la serialización personalizada
      private void writeObject(ObjectOutputStream out) throws IOException {

        System.out.println("Estyo en el escritor");
        out.defaultWriteObject(); // Serializa los campos no transientes automáticamente

        // Escribir la longitud de la cola y luego los elementos

        System.out.println(queue.size());

        out.writeInt(queue.size());
        for (byte[] data : queue) {

            System.out.println("trabajando");
            out.writeObject(data);
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        
        System.out.println("Estyo en el reader");
        
        in.defaultReadObject(); // Deserializa los campos no transientes automáticamente
        
        // Leer la longitud de la cola y luego los elementos
        int size = in.readInt();
        queue = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            queue.add((byte[]) in.readObject());
            System.out.println("trabajando reading");
        }
    }
    
    */
    public Audio(LinkedList<byte[]> queue, AudioFormatWrapper audioFormatWrapper) {

        this.to = "No one.";
        this.from = "No one.";
        this.queue = queue;
        this.audioFormatWrapper = audioFormatWrapper;
    }



    
    public Queue<byte[]> getQueue() {
        return queue;
    }


    public AudioFormatWrapper getAudioFormatWrapper() {
        return audioFormatWrapper;
    }


    public String getTo() {
        return to;
    }


    public void setQueue(LinkedList<byte[]> queue) {
        this.queue = queue;
    }


    public void setAudioFormatWrapper(AudioFormatWrapper audioFormatWrapper) {
        this.audioFormatWrapper = audioFormatWrapper;
    }


    public void setTo(String to) {
        this.to = to;
    }


    public String getFrom() {
        return from;
    }


    public void setFrom(String from) {
        this.from = from;
    }

    
    
  
}


