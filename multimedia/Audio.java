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
        this.queue = queue;
        this.audioFormatWrapper = audioFormatWrapper;
    }


    public Queue<byte[]> getQueue() {
        return queue;
    }


    public AudioFormatWrapper getAudioFormatWrapper() {
        return audioFormatWrapper;
    }
    
  
}


