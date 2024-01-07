package project.server.rmi.server;

import project.server.rmi.DataManagement.ExcelImporter;
import project.server.rmi.DataManagement.HierarchyManager;
import project.client.interfaces.HierarchyManagerRemote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;


public class HierarchyManagerImpl extends UnicastRemoteObject implements HierarchyManagerRemote {

    private HierarchyManager hierarchyManager;
    static ExcelImporter importer = new ExcelImporter();

    public HierarchyManagerImpl() throws RemoteException {
        super();
        this.hierarchyManager = new HierarchyManager();
    }

    @Override
    public String loadEmployeesFromCSV(String filePath) throws RemoteException, SQLException {
        return hierarchyManager.loadEmployeesFromCSV(filePath);
    }

    @Override
    public String refreshHierarchy() throws RemoteException {
        return hierarchyManager.refreshHierarchy();
    }

    @Override
    public String getStagingTable() throws RemoteException {
        return hierarchyManager.getStagingTable();
    }

    @Override
    public String clearStaging() throws RemoteException {
        return hierarchyManager.clearStaging();
    }

    @Override
    public String getStagingErrors() throws RemoteException {
        return hierarchyManager.getStagingErrors();
    }

    @Override
    public String getHierarchy() throws RemoteException {
        return hierarchyManager.getHierarchy();
    }

}