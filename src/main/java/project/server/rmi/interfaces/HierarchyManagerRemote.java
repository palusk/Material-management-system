package project.server.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface HierarchyManagerRemote extends Remote {
    String loadEmployeesFromCSV(String filePath) throws RemoteException, SQLException;
    String refreshHierarchy() throws RemoteException;
    String getStagingTable() throws RemoteException;
    String clearStaging() throws RemoteException;
    String getStagingErrors() throws RemoteException;
    String getHierarchy() throws RemoteException;
    String getAllEmployees() throws RemoteException;
}