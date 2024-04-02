package protocols;

import java.beans.MethodDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.*;
import java.sql.Time;

import packets.PacketFormat;

public class Receiver <T> {


    public static <T> void  receivePacket ( Class< ? extends T> clazz, ObjectInputStream in, T object){
        
        try {
        Object response = in.readObject();

        if(response instanceof String ){
            System.out.print("Se ha recibido el mensaje: ");
            System.out.println(response);
            return;
        }

        PacketFormat packet = (PacketFormat) response;

        System.out.println("Se ha recibido el paquete.");

        String operation = packet.getOperation();
        Serializable[] parameters = packet.getParameters();


        Class<?> [] parameterTypes = new Class <?> [parameters.length];

        System.out.println("Metodo a invocar: " + operation );

        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = parameters[i].getClass();
            System.out.print("Parameter Type:  " + i + ": " + parameterTypes[i].getCanonicalName() +  " | " );
        }

        Object[] castedParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            castedParameters[i] = parameterTypes[i].cast(parameters[i]);
        }

        Method method = clazz.getMethod(operation, parameterTypes);

        if (method.canAccess(object)) {

            System.out.println("El metodo puede acceder la instancia.");
            
            method.invoke(object, castedParameters);

            System.out.println("Se invoca el método " + operation );

            
        }else{
            System.out.println("NO se puede acceder la instancia con el metodo " + method.getName());
        }

            
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
}
