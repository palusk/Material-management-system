package project.client;

import project.client.interfaces.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RemoteManagerImpl extends UnicastRemoteObject implements RemoteManager {
    public RemoteManagerImpl() throws RemoteException {
        super();
    }

    @Override
    public ProductsLoaderRemote getProductsLoader() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        return (ProductsLoaderRemote) registry.lookup("ProductsLoader");
    }

    @Override
    public HierarchyManagerRemote getHierarchyManager() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        return (HierarchyManagerRemote) registry.lookup("HierarchyManager");
    }

    @Override
    public ProfilesManagerRemote getProfilesManager() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        return (ProfilesManagerRemote) registry.lookup("ProfilesManager");
    }

    @Override
    public AuthenticationLDAPRemote getAuthenticationLDAP() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        return (AuthenticationLDAPRemote) registry.lookup("AuthenticationLDAP");
    }

}