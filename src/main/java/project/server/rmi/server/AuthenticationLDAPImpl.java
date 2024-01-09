package project.server.rmi.server;
import project.client.interfaces.AuthenticationLDAPRemote;
import project.server.rmi.DataManagement.AuthenticationLDAP;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class AuthenticationLDAPImpl extends UnicastRemoteObject implements AuthenticationLDAPRemote {

        private AuthenticationLDAP authLDAP;

        public AuthenticationLDAPImpl() throws RemoteException{
            super();
            this.authLDAP = new AuthenticationLDAP();
        }

        @Override
        public boolean authUser(String username, String password) throws RemoteException{
            return authLDAP.authUser(username, password);
        }


}
