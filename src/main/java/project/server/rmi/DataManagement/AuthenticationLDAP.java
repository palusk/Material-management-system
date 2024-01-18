package project.server.rmi.DataManagement;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AuthenticationLDAP {

    DirContext connection;

    public AuthenticationLDAP() {
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

    public List<String> getAllUsers() {
        List<String> allUsers = new ArrayList<>();

        String searchFilter = "(objectClass=inetOrgPerson)";
        String[] reqAtt = {"mail"};

        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        try {
            NamingEnumeration<SearchResult> users = connection.search("ou=users,ou=system", searchFilter, controls);

            while (users.hasMore()) {
                SearchResult result = users.next();
                Attributes attr = result.getAttributes();
                String mail = (String) attr.get("mail").get();
                allUsers.add(mail);
            }

        } catch (NamingException e) {
            e.printStackTrace();
        }

        return allUsers;
    }

    public String searchUserID(String cn, String sn) throws NamingException {
        String searchFilter = "(&(cn=" + cn + ")(sn=" + sn + "))"; // warunek and
//        String searchFilter = "(|(uid=1)(uid=2)(cn=Smith))"; // warunek or
        String[] reqAtt = {"uid"};
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

    public String searchUserName(String mail) throws NamingException {
        String searchFilter = "(mail=" + mail + ")";
        String[] reqAtt = {"cn", "sn"};
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

    public boolean addUser(String cn, String sn, String mail) {

        boolean success = false;

        String uid = mail.replace(".", "");
        int atIndex = uid.indexOf("@");
        uid = uid.substring(0, atIndex);

        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("inetOrgPerson");

        attributes.put(attribute);
        // podstawowe dane usera
        attributes.put("cn", cn);
        attributes.put("sn", sn);
        attributes.put("uid", uid);
        try {
            connection.createSubcontext("mail=" + mail + ",ou=users,ou=system", attributes);
            success = true;
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("Creation of new user failed!");
        }
        return success;
    }

    public void addUserToGroup(String username, String groupName) {
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + username + ",ou=users,ou=system");
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
        try {
            connection.modifyAttributes("cn=" + groupName + ",ou=groups,ou=system", mods);
            System.out.println("User has been added to the group!");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String mail) {
        String user = null;
        try {
            user = searchUserName(mail);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        try {
            connection.destroySubcontext("mail=" + mail + ",ou=users,ou=system");
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("Check if provided user exist!");
        }
    }

    public void deleteUserFromGroup(String username, String groupName) {
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + username + ",ou=users,ou=system");
        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
        try {
            connection.modifyAttributes("cn=" + groupName + ",ou=groups,ou=system", mods);
            System.out.println("");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    /* uwierzytelnianie istniejącego usera */
    public String authUser(String mail, String password) {
        if(isPasswordSet(mail)) {
            try {
                Properties env = new Properties();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, "ldap://192.168.1.42:10389");
                env.put(Context.SECURITY_PRINCIPAL, "mail=" + mail + ",ou=users,ou=system");
                env.put(Context.SECURITY_CREDENTIALS, password);
                DirContext con = new InitialDirContext(env);
                System.out.println("LDAP authentication succeeded");
                con.close();
                return "Authorized";
            } catch (NamingException e) {
                System.out.println("LDAP authentication failed: " + e.getMessage());
                return "NonAuthorized";
            } catch (Exception e){
                String exception = e.getMessage();
                return exception;
            }
        }else {return "Unregistered";}
    }

    /* sprawdzanie userów bez hasła */
    public List<String> getUsersWithoutPassword() {
        List<String> usersWithoutPassword = new ArrayList<>();

        String searchFilter = "(&(objectClass=inetOrgPerson)(!(userPassword=*)))";
        String[] reqAtt = {"mail"};

        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        try {
            NamingEnumeration<SearchResult> users = connection.search("ou=users,ou=system", searchFilter, controls);
            while (users.hasMore()) {
                SearchResult result = users.next();
                Attributes attr = result.getAttributes();
                String mail = (String) attr.get("mail").get();
                usersWithoutPassword.add(mail);
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return usersWithoutPassword;
    }


    public boolean isPasswordSet(String mail){
         List<String> list = getUsersWithoutPassword();
             if(list.contains(mail)){
                 return false;
             }
         return true;
    }

    /* update hasła usera */
    public boolean updateUserPassword(String mail, String password) {
        try {
            String dnBase = ",ou=users,ou=system";
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", password));
            connection.modifyAttributes("mail=" + mail + dnBase, mods); //dynamiczne tworzenie DN (nazwy wyróżniającej)
            System.out.println("Password has been changed!");
            return true;
        } catch (Exception e) {
            System.out.println("Change of password has failed: " + e.getMessage());
            return false;
        }
    }

    public void updateUserDetails(String username, String employeeNumber) {
        try {
            String dnBase = ",ou=users,ou=system";
            Attribute attribute = new BasicAttribute("employeeNumber", employeeNumber);
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute);
            connection.modifyAttributes("cn=" + username + dnBase, mods);
            System.out.println("success");
        } catch (Exception e) {
            System.out.println("failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        AuthenticationLDAP testObject = new AuthenticationLDAP();
//        testObject.updateUserPassword("mziecina", "123");
//        testObject.addUser();
//        testObject.getAllUsers();
//        testObject.deleteUser("kpalus");

        List<String> usersWithoutPassword = testObject.getUsersWithoutPassword();

        System.out.println("Users without password:");
        for (String user : usersWithoutPassword) {
            System.out.println(user);

        }

//        List<String> allUsers = testObject.getAllUsers();
//
//        for (String user : allUsers) {
//            System.out.println(user);
//        }

    }
}