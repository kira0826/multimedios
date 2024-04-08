
El primer paso a ejecutar es generar los archivos .class, para ello, estando ubicaodo en la carpeta
multimedios se ejecuta lo siguiente:

javac storage/*.java
javac protocolos/*.java
javac utilities/*.java
javac client/*.java
javac servers/*.java
java servers.Receptionist

De esta manera se cargan las clases y se pone a correr el servidor. En caso tal de tener problemas
con el puerto 15000 (usado para el server),  se puede modificar el .properties para cambiar el puerto
a usar,  modificando la propiedad "port.receptionist= 15000". 

luego, en otra terminal se debe ejecutar la sentencia:

java client.Client

Para así disponer de un cliente. Si se desea más clientes, se repite el paso anterior en otra terminal
se recomienda el uso de terminator.

Lo primero que se solicita al ejecutar el cliente es ingresar un nombre, y luego se muestra el menu,
en el cual se indica el funcionamiento de los comandos.

Para dejar claro el funcionamiento de los comandos, se procede a desglozar una sentencia:

Sea el comando "/addUser nombreDeGrupo nombreDeUsuarioA nombreDeUsuarioB ...", la sección "/addUser"
es usada para indicar qué comando se va a usar, luego se recibe al información necesaria separada por 
UN ESPACIO. por ejemplo si se deasea agregar los usario "pedro" y "juan" en el grupo "Verde" el comando
queda de la forma:


/addUser Verde juan pedro

De igual forma el comando /menu explica cada comando y su forma de escribir {Cómo se debe escribir | Qué hace}.
