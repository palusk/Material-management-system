package project.materialmanagementsystemjavafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginPage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Window");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Username Label
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);

        // Username Input
        TextField usernameInput = new TextField();
        GridPane.setConstraints(usernameInput, 1, 0);

        // Password Label
        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);

        // Password Input
        PasswordField passwordInput = new PasswordField();
        GridPane.setConstraints(passwordInput, 1, 1);

        // Login Button
        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        loginButton.setOnAction(e -> {
            // Tutaj umieść kod do sprawdzenia poprawności logowania
            boolean isValid = authenticate(usernameInput.getText(), passwordInput.getText());
            if (isValid) {
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed. Please check your credentials.");
            }
        });

        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton);

        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private boolean authenticate(String username, String password) {
        // Tutaj możesz umieścić kod do sprawdzenia poprawności logowania,
        // np. porównanie z danymi w bazie danych.
        // W tym przykładzie logujemy się, jeśli username to "admin" a hasło to "password".
        return username.equals("admin") && password.equals("password");
    }
}




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

