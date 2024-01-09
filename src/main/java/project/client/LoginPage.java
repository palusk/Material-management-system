package project.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import project.server.rmi.DataManagement.AuthenticationLDAP;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

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
                try {
                    openMainPanel(primaryStage); // Przełącz do głównego panelu po zalogowaniu
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (NotBoundException ex) {
                    throw new RuntimeException(ex);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                System.out.println("Login failed. Please check your credentials.");
            }
        });

        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton);

        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    // Dodane: Metoda otwierająca główny panel
    private void openMainPanel(Stage primaryStage) throws SQLException, NotBoundException, RemoteException {
        primaryStage.close(); // Zamknij okno logowania
        ResourcesManagementApplication mainPanel = new ResourcesManagementApplication();
        mainPanel.start(new Stage()); // Uruchom główny panel
    }

    private boolean authenticate(String username, String password) {

        AuthenticationLDAP ldapConnect = new AuthenticationLDAP();
        return ldapConnect.authUser(username, password);

    }
}
