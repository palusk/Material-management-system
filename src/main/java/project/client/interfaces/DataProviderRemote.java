package project.client.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataProviderRemote extends Remote {
    String getWarehouseProducts(int warehouseID) throws RemoteException;
    String getAllProducts() throws RemoteException;
    String getOrderDetails(String orderID) throws RemoteException;
    String getAllWarehouses() throws RemoteException;
    String getTransferHistory() throws RemoteException;
    String getAllEmployees() throws RemoteException;
    String getAllProductsInStock() throws RemoteException;
    String getAllOrders() throws RemoteException;
    String getAllProductsAdmin() throws RemoteException;
    String getAllWarehousesAdmin() throws RemoteException;
    void delete(String ID, String table, String frstValue, String secondValue) throws RemoteException;
    void edit(String ID, String table, String frstValue, String secondValue) throws RemoteException;
    void edit2(String ID, String table, String frstValue, String secondValue, String thirdValue) throws RemoteException;
    void add(String ID, String table, String frstValue, String secondValue) throws RemoteException;
}
