package project.server.rmi.server;

import project.server.rmi.interfaces.RMISessionManager;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMISessionManagerImpl extends UnicastRemoteObject implements RMISessionManager {

    private List<String> connectedClients;

    public RMISessionManagerImpl() throws RemoteException {
        super();
        this.connectedClients = new ArrayList<>();
    }

    @Override
    public void registerClient() throws RemoteException {
        try {
            // Get the client's host address and add it to the list
            String clientAddress = RemoteServer.getClientHost();
            connectedClients.add(clientAddress);

            System.out.println("Client connected from: " + clientAddress);
        } catch (ServerNotActiveException e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterClient() throws RemoteException {
        try {
            // Get the client's host address and remove it from the list
            String clientAddress = RemoteServer.getClientHost();
            connectedClients.remove(clientAddress);

            System.out.println("Client disconnected from: " + clientAddress);
        } catch (ServerNotActiveException e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    // Add other remote methods as needed

    public List<String> getConnectedClients() {
        return connectedClients;
    }
}