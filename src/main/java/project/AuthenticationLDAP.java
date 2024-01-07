package project;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.Properties;

public class AuthenticationLDAP {

    //    public static boolean authenticate(String username, String password) {
//        // Tworzenie hashtable dla ustawień połączenia LDAP
//        Hashtable<String, String> env = new Hashtable<>();
    public static void main(String[] args) {


        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://192.168.1.42:10389");
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin, ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        try {
            DirContext connection = new InitialDirContext(env);
            System.out.println("Connection: " + connection);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}