package project.server.rmi.server;


import project.client.interfaces.ProductsManagerRemote;
import project.server.rmi.DataManagement.ProductsManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ProductsManagerImpl extends UnicastRemoteObject implements ProductsManagerRemote {

    RMISessionManagerImpl sessionTest = new RMISessionManagerImpl();

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

    @Override
    public String listProductsToTransfer(String orderID) throws RemoteException {

        // --TODO TESTOWANIE SESJI KLIENTA
        sessionTest.registerClient();
        System.out.println(sessionTest.getConnectedClients());
        return productsManager.listProductsToTransfer(orderID);
    }
}
