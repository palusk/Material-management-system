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
            System.out.println(attr.get("cn"));
            System.out.println(attr.get("sn"));
            System.out.println("Tutaj powinno być UID: " + attr.get("uid"));
            System.out.println("Tutaj powinno być hasło: " + attr.get("userPassword"));

            /* PRZYKŁADOWE USUNIĘCIE USERA Z GRUPY */
//            try {
//                String name = attr.get("uid").get(0).toString();
//            } catch (NamingException e) {
//                throw new RuntimeException(e);
//            }
//            deleteUserFromGroup(name,"Administrators");
        }
    }

    public String searchUserID(String cn, String sn) throws NamingException {
        String searchFilter = "(&(cn=" + cn + ")(sn=" + sn + "))"; // warunek and
//        String searchFilter = "(|(uid=1)(uid=2)(cn=Smith))"; // warunek or
        String[] reqAtt = { "uid" };
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        NamingEnumeration users = connection.search("ou=users,ou=system", searchFilter, controls);

        SearchResult result = null;
        String uidFound = new String();
        while (users.hasMore()) {
            result = (SearchResult) users.next();
            Attributes attr = result.getAttributes();
            System.out.println(attr.get("uid"));
            uidFound = attr.get("uid").get(0).toString();
        }
        return uidFound;
    }

    public String searchUserName(String uid) throws NamingException {
        String searchFilter = "(uid=" + uid +")";
        String[] reqAtt = { "cn", "sn" };
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        NamingEnumeration users = connection.search("ou=users,ou=system", searchFilter, controls);

        SearchResult result = null;
        String nameSurname = new String();
        while (users.hasMore()) {
            result = (SearchResult) users.next();
            Attributes attr = result.getAttributes();
            nameSurname = attr.get("cn").get(0).toString() + " " + attr.get("sn").get(0).toString();
        }
        return nameSurname;
    }

    // --TODO AUTOMATYZUJ TWORZENIE UŻYTKOWNIKÓW NA SERWERZE LDAP PODCZAS ŁADOWANIA PLIKU CSV Z PRACOWNIKAMI
    public void addUser() {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("inetOrgPerson");

        attributes.put(attribute);
        // podstawowe dane usera
        attributes.put("cn", "Kamil");
        attributes.put("sn", "Palus");
        try {
            connection.createSubcontext("uid=kpalus,ou=users,ou=system", attributes);
            System.out.println("New user has been added to the server!");
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("Creation of new user failed!");
        }
    }

    public void addUserToGroup(String username, String groupName) {
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

    public void deleteUser(String uid){
        String user  = null;
        try {
            user = searchUserName(uid);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        try {
            connection.destroySubcontext("uid=" + uid + ",ou=users,ou=system");
            System.out.println("User " + user + " has been deleted!");
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("Check if provided user exist!");
        }
    }

    public void deleteUserFromGroup(String username, String groupName) {
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


    /* uwierzytelnianie istniejącego usera */
    public static boolean authUser(String username, String password)
    {
        try {
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://192.168.1.42:10389");
            env.put(Context.SECURITY_PRINCIPAL, "uid="+username+",ou=users,ou=system");
            env.put(Context.SECURITY_CREDENTIALS, password);
            DirContext con = new InitialDirContext(env);
            System.out.println("LDAP authentication succeeded");
            con.close();
            return true;
        }catch (Exception e) {
            System.out.println("LDAP authentication failed: "+e.getMessage());
            return false;
        }
    }

    /* update hasła usera */
    public void updateUserPassword(String username, String password) {
        try {
            String dnBase=",ou=users,ou=system";
            ModificationItem[] mods= new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", password));
            connection.modifyAttributes("uid="+username +dnBase, mods); //dynamiczne tworzenie DN (nazwy wyróżniającej)
            System.out.println("success");
        }catch (Exception e) {
            System.out.println("failed: "+e.getMessage());
        }
    }

    public void updateUserDetails(String username, String employeeNumber) {
        try {
            String dnBase=",ou=users,ou=system";
            Attribute attribute = new BasicAttribute("employeeNumber", employeeNumber);
            ModificationItem[] mods= new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute);
            connection.modifyAttributes("cn="+username +dnBase, mods);
            System.out.println("success");
        }catch (Exception e) {
            System.out.println("failed: "+e.getMessage());
        }
    }

    public static void main(String[] args) {

        AuthenticationLDAP testObject = new AuthenticationLDAP();
 //       testObject.updateUserPassword("mziecina", "123");
//        testObject.addUser();
        testObject.getAllUsers();
//        testObject.deleteUser("kpalus");



    }
}