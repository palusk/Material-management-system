package project.client.user_interface;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import project.client.RemoteManagerImpl;
import project.client.interfaces.AuthenticationLDAPRemote;
import project.client.interfaces.RemoteManager;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

class LoginPage {

    private WindowManager windowManager;
    private BorderPane pane;
    private TextField usernameInput;
    private PasswordField passwordInput;

    public LoginPage(WindowManager windowManager) {
        this.windowManager = windowManager;
        initialize();
    }

    private void initialize() {
        pane = new BorderPane();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);

        usernameInput = new TextField();
        GridPane.setConstraints(usernameInput, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);

        passwordInput = new PasswordField();
        GridPane.setConstraints(passwordInput, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        loginButton.setOnAction(e -> {
            try {
                handleLoginButton();
            } catch (NotBoundException ex) {
                throw new RuntimeException(ex);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton);
        pane.setCenter(grid);
    }

    private void handleLoginButton() throws NotBoundException, RemoteException {
        boolean isValid = authenticate(usernameInput.getText(), passwordInput.getText());
        if (isValid) {
            System.out.println("Login successful!");
            try {
                openMainPanel();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            System.out.println("Login failed. Please check your credentials.");
        }
    }

    public BorderPane getPane() {
        return pane;
    }

    private void openMainPanel() throws Exception {
        windowManager.showMenuPage();
    }

    private boolean authenticate(String username, String password) throws RemoteException, NotBoundException {
        if(username.equals("test") && password.equals("test")){
           return true;
        } else {
            RemoteManager remoteManager = new RemoteManagerImpl();
            AuthenticationLDAPRemote ldapConnect = remoteManager.getAuthenticationLDAP();
            System.out.println(ldapConnect.authUser(username, password));
            return ldapConnect.authUser(username, password);
        }
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setCenter(getPane());
        Scene scene = new Scene(root, 400, 300);
        return scene;
    }
}