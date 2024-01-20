package project.server.rmi.server;


import project.client.interfaces.ProductsManagerRemote;
import project.server.rmi.DataManagement.ProductsManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

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


        sessionTest.registerClient();
        System.out.println(sessionTest.getConnectedClients());
        return productsManager.listProductsToTransfer(orderID);
    }

    @Override
    public String cancelOrder(String orderID) throws RemoteException {
        return productsManager.cancelOrder(orderID);
    }

    @Override
    public String completeOrder(String orderID) throws RemoteException {
        return productsManager.completeOrder(orderID);
    }

    @Override
    public List<String> getAllWarehouses() throws RemoteException {
        return productsManager.getAllWarehouses();
    }

    @Override
    public List<String> getAllWarehousesWithIgnore(String ignoreString, int employeeType) throws RemoteException {
        return productsManager.getAllWarehousesWithIgnore(ignoreString,employeeType);
    }

    @Override
    public String getOrderStatus(int orderID) throws RemoteException {
        return productsManager.getOrderStatus(orderID);
    }


}
