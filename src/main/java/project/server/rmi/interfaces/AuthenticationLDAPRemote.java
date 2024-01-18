package project.server.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthenticationLDAPRemote extends Remote {
    String authUser(String username, String password) throws RemoteException;
    boolean updateUserPassword(String mail, String password) throws RemoteException;
}
