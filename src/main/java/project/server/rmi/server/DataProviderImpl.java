package project.server.rmi.server;

import project.client.interfaces.ProfilesManagerRemote;
import project.server.rmi.DataManagement.DataProvider;
import project.server.rmi.DataManagement.HierarchyManager;
import project.server.rmi.DataManagement.ProfilesManager;
import project.server.rmi.interfaces.DataProviderRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

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
