package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig {
    
    private static Properties prop = new Properties();


    static {
        InputStream input = null;
        try {
            // Carga el archivo de propiedades desde la ubicación relativa al paquete de la clase
            // Esta cosa asegura que se busque en en la raiz y no entro zona.
            input = PropertiesConfig.class.getResourceAsStream("/.properties");
            if (input == null) {
                throw new IOException("No se pudo encontrar el archivo de propiedades.");
            }
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}