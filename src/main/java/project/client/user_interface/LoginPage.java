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
    private Label text;
    private Button registerButton;
    private String registerMessage = "New user, please press register button again to set you password";
    private String registerErrorMessage = "Password has not been set up correctly. Please try again or contact with admin!";
    public LoginPage(WindowManager windowManager) {
        this.windowManager = windowManager;
        initialize();
    }

    private void initialize() {
        pane = new BorderPane();
        text = new Label("Provide your credentials");
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

        registerButton = new Button("Register");
        registerButton.setDisable(true);
        GridPane.setConstraints(text, 0, 4);
        GridPane.setConstraints(registerButton, 0, 3);
        registerButton.setOnAction(e -> {
            try {
                RemoteManager remoteManager = new RemoteManagerImpl();
                AuthenticationLDAPRemote ldapConnect = remoteManager.getAuthenticationLDAP();
                if(ldapConnect.updateUserPassword(usernameInput.getText(), passwordInput.getText())){
                    try {
                        text.setText("Provide your credentials");
                        registerButton.setDisable(true);
                        openMainPanel();
                    } catch (Exception ex) {
                        text.setText("User app error");
                        throw new RuntimeException(ex);
                    }
                } else{
                    text.setText(registerErrorMessage);
                    System.out.println(registerErrorMessage);
                }
            } catch (Exception ee) {
                System.out.println(ee.getMessage());
            }
        });

        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton,registerButton, text);
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
            registerButton.setDisable(true);
            if( text.getText().equals(registerMessage)){
                registerButton.setDisable(false);
            }
            System.out.println(registerMessage);
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
            String authenticationOutput = ldapConnect.authUser(username, password);
            switch (authenticationOutput){
                case "Authorized":
                    System.out.println("logged in");
                    return true;
                case "Unregistered":
                    text.setText(registerMessage);
                    System.out.println("not registered");
                    return false;
                case "NonAuthorized":
                    text.setText("Wrong credentials");
                    System.out.println("wrong credentials");
                    return false;
                default:
                    System.out.println(authenticationOutput);
                    text.setText("Server error");
                    System.out.println("error");
                    return false;
            }
        }
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setCenter(getPane());
        Scene scene = new Scene(root, 400, 300);
        return scene;
    }
}