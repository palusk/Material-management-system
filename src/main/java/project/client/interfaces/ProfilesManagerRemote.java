package project.client.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ProfilesManagerRemote extends Remote {
    String updateProfiles() throws RemoteException;
    String getProfiles() throws RemoteException;
    List<String> getWarehouseDropdown(int userID) throws RemoteException;
}
