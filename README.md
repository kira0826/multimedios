![ICESI University Logo](https://www.icesi.edu.co/launiversidad/images/La_universidad/logo_icesi.png)

## Chat múltiple cliente - texto y audio.

### **Autores** ✒️

- Santiago Escobar León
- Kevin Steven Nieto Curaca
- Yeison Antonio Rodríguez Zuluaga
- Ricardo Urbina Ospina

### **Descripción del Proyecto**

Este proyecto tiene como objetivo diseñar e implementar un sistema de mensajería instantánea capaz de cumplir con requisitos específicos:

Crear grupos de chat: Se utilizará el protocolo TCP para esta funcionalidad. La elección de este protocolo se basa en la necesidad de una comunicación confiable y ordenada para garantizar que todos los miembros del grupo reciban los mensajes correctamente y en el orden correcto.

Enviar un mensaje de texto a un usuario o grupo específico: También se empleará el protocolo TCP para esta funcionalidad. El envío de mensajes de texto requiere confiabilidad y orden, lo que hace que TCP sea la opción más adecuada.

Enviar una nota de voz a un usuario o grupo específico: Se utilizará el protocolo TCP para esta tarea.

Realizar una llamada a un usuario o grupo: Se utilizará el protocolo UDP para las llamadas. Este protocolo es ideal para la transmisión de datos en tiempo real debido a su baja latencia y tolerancia a la pérdida de datos.


### **Pasos para ejecutar el proyecto**

El primer paso es modificar el archivo .properties, modificando la dirección del servidor y del host (dirección de la máquina en la que voy a ejecutar la aplicación).

Luego, debemos generar los archivos .class, para ello, estando ubicado en la carpeta
multimedios se ejecuta lo siguiente:

```
javac storage/*.java
javac protocolos/*.java
javac utilities/*.java
javac client/*.java
javac servers/*.java
java servers.Receptionist
```

De esta manera se cargan las clases y se pone a correr el servidor. En caso tal de tener problemas
con el puerto 15000 (usado para el server),  se puede modificar el .properties para cambiar el puerto
a usar, modificando la propiedad ```port.receptionist= 15000```.

Luego, en otra terminal se debe ejecutar la sentencia:

```
java client.Client
```

Para así disponer de un cliente. Si se desea más clientes, se repite el paso anterior en otra terminal
se recomienda el uso de **terminator**.

Lo primero que se solicita al ejecutar el cliente es ingresar un nombre, y luego se muestra el menu,
en el cual se indica el funcionamiento de los comandos.

Para dejar claro el funcionamiento de los comandos, se procede a desglosar una sentencia:

Sea el comando ```/addUser nombreDeGrupo nombreDeUsuarioA nombreDeUsuarioB ...```, la sección ```/addUser```
es usada para indicar qué comando se va a usar, luego se recibe la información necesaria **separada por
UN ESPACIO**. Por ejemplo si se desea agregar los usuarios "pedro" y "juan" en el grupo "Verde" el comando
queda de la forma:

```
/addUser Verde juan pedro
```

De igual forma el comando ```/menu``` explica cada comando y su forma de escribir {Cómo se debe escribir | Qué hace}.


### **Construido Con** 🛠️

<div style="text-align: left">
    <p>
        <a href="https://www.jetbrains.com/idea/" target="_blank"> <img alt="IntelliJ IDEA" src="https://cdn.svgporn.com/logos/intellij-idea.svg" height="60" width = "60"></a>
        <a href="https://www.java.com/" target="_blank"> <img alt="Java" src="https://cdn.svgporn.com/logos/java.svg" height="60" width = "60"></a>
    </p>
</div>

Este proyecto requiere las siguientes versiones:

- **jdk**: 21
