package protocolos;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import packets.PacketFormat;

public class Sender  {


    public static void senderPacket( ObjectOutputStream outer, String operation, Serializable ... parameters){
     
        PacketFormat packet = new PacketFormat(operation, parameters);
    
        try {                
                outer.writeObject(packet);
                outer.flush();
            } catch (IOException  e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
    
        }
}
