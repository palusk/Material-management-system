package project.server.rmi.server;


import project.server.rmi.DataManagement.DataProvider;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DataProviderImpl extends UnicastRemoteObject implements project.client.interfaces.DataProviderRemote {
    private DataProvider dataProvider;

    public DataProviderImpl() throws RemoteException {
        super();
        this.dataProvider = new DataProvider();
    }

    @Override
    public String getWarehouseProducts(int warehouseID) throws RemoteException {
        return dataProvider.getWarehouseProducts(warehouseID);
    }

    @Override
    public String getAllProducts() throws RemoteException {
        return dataProvider.getAllProducts();
    }

    @Override
    public String getOrderDetails(String orderID) throws RemoteException {
        return dataProvider.getOrderDetails(orderID);
    }

    @Override
    public String getAllWarehouses() throws RemoteException {
        return dataProvider.getAllWarehouses();
    }

    @Override
    public String getTransferHistory() throws RemoteException {
        return dataProvider.getTransferHistory();
    }

    @Override
    public String getAllEmployees() throws RemoteException {
        return dataProvider.getAllEmployees();
    }

    @Override
    public String getAllProductsInStock() throws RemoteException {
        return dataProvider.getAllProductsInStock();
    }

    @Override
    public String getAllOrders() throws RemoteException {
        return dataProvider.getAllOrders();
    }

    @Override
    public String getAllProductsAdmin() throws RemoteException {
        return dataProvider.getAllProductsAdmin();
    }

    @Override
    public String getAllWarehousesAdmin() throws RemoteException {
        return dataProvider.getAllWarehousesAdmin();
    }

    @Override
    public void delete(String ID, String table, String frstValue, String secondValue) throws RemoteException {
        dataProvider.delete(ID, table, frstValue, secondValue);
    }

    @Override
    public void edit(String ID, String table, String frstValue, String secondValue) throws RemoteException {
        dataProvider.edit(ID, table, frstValue, secondValue);
    }

    @Override
    public void edit2(String ID, String table, String frstValue, String secondValue, String thirdValue) throws RemoteException {
        dataProvider.edit2(ID, table, frstValue, secondValue, thirdValue);
    }

    @Override
    public void add(String ID, String table, String frstValue, String secondValue) throws RemoteException {
        dataProvider.add(ID, table, frstValue, secondValue);
    }

}
