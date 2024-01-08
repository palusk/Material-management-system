package project.server.rmi.DataManagement;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Properties;

public class AuthenticationLDAP {

    DirContext connection;

    public AuthenticationLDAP(){
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://192.168.1.42:10389");
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin, ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        try {
            connection = new InitialDirContext(env);
            System.out.println("Connected: " + connection);
        } catch (AuthenticationException ex) {
            System.out.println(ex.getMessage());
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void getAllUsers() {
        String searchFilter = "(objectClass=inetOrgPerson)";
        String[] reqAtt = { "cn", "sn" };
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        NamingEnumeration users = null;
        try {
            users = connection.search("ou=users,ou=system", searchFilter, controls);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        SearchResult result = null;
        while (true) {
            try {
                if (!users.hasMore()) break;
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
            try {
                result = (SearchResult) users.next();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }

            Attributes attr = result.getAttributes();
            System.out.println(attr.get("uid"));
            System.out.println(attr.get("cn"));
            System.out.println(attr.get("sn"));
            System.out.println(attr.get("userPassword"));

            // ------------- PRZYKŁADOWE USUNIĘCIE USERA Z GRUPY
//            try {
//                String name = attr.get("uid").get(0).toString();
//            } catch (NamingException e) {
//                throw new RuntimeException(e);
//            }
//            deleteUserFromGroup(name,"Administrators");

        }

    }

    public void addUser() {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("inetOrgPerson");

        attributes.put(attribute);
        // user details
        attributes.put("cn", "Mateusz");
        attributes.put("sn", "Ziecina");
        try {
            connection.createSubcontext("uid=mziecina,ou=users,ou=system", attributes);
            System.out.println("New user has been added to the server!");
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("Creation of new user failed!");
        }
    }

    public void addUserToGroup(String username, String groupName)
    {
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attribute = new BasicAttribute("uniqueMember","cn="+username+",ou=users,ou=system");
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
        try {
            connection.modifyAttributes("cn="+groupName+",ou=groups,ou=system", mods);
            System.out.println("User has been added to the group!");
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    public void deleteUser()
    {
        try {
            connection.destroySubcontext("cn=Mateusz,ou=users,ou=system");
            System.out.println("User has been deleted!");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void deleteUserFromGroup(String username, String groupName)
    {
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attribute = new BasicAttribute("uniqueMember","cn="+username+",ou=users,ou=system");
        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
        try {
            connection.modifyAttributes("cn="+groupName+",ou=groups,ou=system", mods);
            System.out.println("");
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        AuthenticationLDAP testObject = new AuthenticationLDAP();
        testObject.getAllUsers();
        testObject.addUser();
        testObject.deleteUser();

    }

}