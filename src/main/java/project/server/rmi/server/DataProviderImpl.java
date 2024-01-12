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
}
