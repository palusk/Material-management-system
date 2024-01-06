package project;

public class AuthenticationLDAP {

    import javax.naming.AuthenticationException;
        import javax.naming.Context;
        import javax.naming.NamingException;
        import javax.naming.directory.DirContext;
        import javax.naming.directory.InitialDirContext;
        import java.util.Hashtable;

    public class LDAPAuthentication {
        public static boolean authenticate(String username, String password) {
            // Tworzenie hashtable dla ustawień połączenia LDAP
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://your-ldap-server:389");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "cn=" + username + ",ou=users,dc=example,dc=com");
            env.put(Context.SECURITY_CREDENTIALS, password);

            try {
                // Próba nawiązania połączenia z serwerem LDAP
                DirContext context = new InitialDirContext(env);

                // Jeśli połączenie udane, zamknij context i zwróć true
                context.close();
                return true;
            } catch (AuthenticationException e) {
                // Błąd uwierzytelniania, użytkownik nieprawidłowy
                return false;
            } catch (NamingException e) {
                // Błąd nawiązywania połączenia lub inne błędy
                e.printStackTrace();
                return false;
            }
        }
    }

}
