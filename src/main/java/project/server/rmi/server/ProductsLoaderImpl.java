package project.server.rmi.server;

import project.server.rmi.DataManagement.ProductsLoader;
import project.client.interfaces.ProductsLoaderRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class ProductsLoaderImpl extends UnicastRemoteObject implements ProductsLoaderRemote {
    private ProductsLoader productsLoader;

    public ProductsLoaderImpl() throws RemoteException {
        super();
        this.productsLoader = new ProductsLoader();
    }

    @Override
    public String loadProductsFromCSV(String filePath) throws RemoteException, SQLException {
        return productsLoader.loadProductsFromCSV(filePath);
    }

    @Override
    public String getStagingErrors() throws RemoteException {
        return productsLoader.getStagingErrors();
    }

    @Override
    public void clearStaging() throws RemoteException {
        productsLoader.clearStaging();
    }

    @Override
    public String getStagingTable() throws RemoteException {

        return productsLoader.getStagingTable();
    }

}