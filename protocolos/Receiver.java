package protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import packets.PacketFormat;

public class Receiver <T> {


    public static <T> void  receivePacket ( Class< ? extends T> clazz, ObjectInputStream in, T object){
        
        try {
        Object response = in.readObject();

        if(response instanceof String ){
            System.out.println(response);
            return;
        }

        PacketFormat packet = (PacketFormat) response;


        String operation = packet.getOperation();
        Serializable[] parameters = packet.getParameters();

        Method method = null;

        if (parameters == null) {

            method = clazz.getMethod(operation, null);

        if (method.canAccess(object)) {

            
            method.invoke(object, null);


            
        }else{
            System.out.println("NO se puede acceder la instancia con el metodo " + method.getName());
        }


            
        }else{

        Class<?> [] parameterTypes = new Class <?> [parameters.length];


        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = parameters[i].getClass();
        }

        Object[] castedParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            castedParameters[i] = parameterTypes[i].cast(parameters[i]);
        }

        method = clazz.getMethod(operation, parameterTypes);

        if (method.canAccess(object)) {

            
            method.invoke(object, castedParameters);

            System.out.println("Se invoca el metodo " + operation);
            
        }else{
            System.out.println("NO se puede acceder la instancia con el metodo " + method.getName());
        }


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
