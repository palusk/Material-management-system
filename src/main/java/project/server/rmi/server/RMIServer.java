package project.server.rmi.server;

import project.client.interfaces.AuthenticationLDAPRemote;
import project.client.interfaces.HierarchyManagerRemote;
import project.client.interfaces.ProductsLoaderRemote;
import project.client.interfaces.ProfilesManagerRemote;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            // Rejestruj obiekty zdalne w rejestrze RMI
            Registry registry = LocateRegistry.createRegistry(1099);

            ProductsLoaderRemote productsLoaderRemote = new ProductsLoaderImpl();
            registry.rebind("ProductsLoader", productsLoaderRemote);

            HierarchyManagerRemote hierarchyManagerRemote = new HierarchyManagerImpl();
            registry.rebind("HierarchyManager", hierarchyManagerRemote);

            ProfilesManagerRemote profilesManagerRemote = new ProfilesManagerImpl();
            registry.rebind("ProfilesManager", profilesManagerRemote);

            AuthenticationLDAPRemote authenticationLDAPRemote = new AuthenticationLDAPImpl();
            registry.rebind("AuthenticationLDAP", authenticationLDAPRemote);

            System.out.println("RMI Server is running.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}