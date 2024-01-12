package project.server.rmi.server;


import project.client.interfaces.ProductsManagerRemote;
import project.server.rmi.DataManagement.ProductsManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ProductsManagerImpl extends UnicastRemoteObject implements ProductsManagerRemote {

    ProductsManager productsManager;

    public ProductsManagerImpl() throws RemoteException {
        super();
        this.productsManager = new ProductsManager();
    }

    @Override
    public void insertStagingOrder(String orderDetails) throws RemoteException {
        productsManager.insertStagingOrder(orderDetails);
    }

    @Override
    public String getOrdersInWarehouse(String warehouseID) throws RemoteException {
        return productsManager.getOrdersInWarehouse(warehouseID);
    }
}
