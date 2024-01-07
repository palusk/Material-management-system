package project.client.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProfilesManagerRemote extends Remote {
    String updateProfiles() throws RemoteException;
    String getProfiles() throws RemoteException;
}
