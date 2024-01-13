package project.server.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProductsManagerRemote extends Remote {
    void insertStagingOrder(String orderDetails) throws RemoteException;
    String getOrdersInWarehouse(String warehouseID) throws RemoteException;
    String listProductsToTransfer(String orderID) throws RemoteException;
}