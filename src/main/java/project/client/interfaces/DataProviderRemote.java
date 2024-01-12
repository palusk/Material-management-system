package project.client.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DataProviderRemote extends Remote {
    String getWarehouseProducts(int warehouseID) throws RemoteException;
}
