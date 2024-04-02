package interfaces;

import users.User;
import utilities.ConnectionInfo;

public interface IServerANDDataBaseAndClientServices {

    void initialUserSuscribeToDB (User user);

    void saveUserConnectionInfo (ConnectionInfo connectionInfo, String user);
    
}
