package project.server.rmi.server;

import project.server.rmi.DataManagement.ProfilesManager;
import project.client.interfaces.ProfilesManagerRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ProfilesManagerImpl extends UnicastRemoteObject implements ProfilesManagerRemote {
    private ProfilesManager profilesManager;

    public ProfilesManagerImpl() throws RemoteException {
        super();
        this.profilesManager = new ProfilesManager();
    }

    @Override
    public String updateProfiles() throws RemoteException {
        return profilesManager.updateProfiles();
    }

    @Override
    public String getProfiles() throws RemoteException {
        return profilesManager.getProfiles();
    }

}
