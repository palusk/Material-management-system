package project.client.interfaces;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteManager extends Remote {
    ProductsLoaderRemote getProductsLoader() throws RemoteException, NotBoundException;
    HierarchyManagerRemote getHierarchyManager() throws RemoteException, NotBoundException;
    ProfilesManagerRemote getProfilesManager() throws RemoteException, NotBoundException;
    AuthenticationLDAPRemote getAuthenticationLDAP() throws RemoteException, NotBoundException;
    DataProviderRemote getDataProvider() throws RemoteException, NotBoundException;
    ProductsManagerRemote getProductsManager() throws RemoteException, NotBoundException;
}
