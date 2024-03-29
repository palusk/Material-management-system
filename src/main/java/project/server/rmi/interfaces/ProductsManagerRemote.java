package project.server.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ProductsManagerRemote extends Remote {
    void insertStagingOrder(String orderDetails) throws RemoteException;
    String getOrdersInWarehouse(String warehouseID) throws RemoteException;
    String listProductsToTransfer(String orderID) throws RemoteException;
    String cancelOrder(String orderID) throws RemoteException;
    String completeOrder(String orderID) throws RemoteException;
    List<String> getAllWarehouses() throws RemoteException;
    List<String> getAllWarehousesWithIgnore(String ignoreString, int employeeType) throws RemoteException;
    String getOrderStatus(int orderID) throws RemoteException;
}
