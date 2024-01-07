package project.server.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface ProductsLoaderRemote extends Remote {
    String loadProductsFromCSV(String filePath) throws RemoteException, SQLException;
    String getStagingErrors() throws RemoteException;
    void clearStaging() throws RemoteException;
    String getStagingTable() throws RemoteException;
}