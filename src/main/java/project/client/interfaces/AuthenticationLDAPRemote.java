package project.client.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthenticationLDAPRemote extends Remote {
    boolean authUser(String username, String password) throws RemoteException;
}
