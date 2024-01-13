package project.server.rmi.interfaces;

import java.rmi.RemoteException;
import java.util.List;

public interface RMISessionManager {

    void registerClient() throws RemoteException;
    void unregisterClient() throws RemoteException;
    List<String> getConnectedClients();
}
