package project.server.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataProviderRemote extends Remote {
    String getWarehouseProducts(int warehouseID) throws RemoteException;
}
