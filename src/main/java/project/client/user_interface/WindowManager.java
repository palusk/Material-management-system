package project.client.user_interface;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class WindowManager {

    private Stage primaryStage;
    public LoginPage loginPage;
    private UserPage userPage;
    private AdminPage adminPage;
    private MenuPage menuPage;

    public WindowManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setLoginPage(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    public void showMenuPage(boolean highPermission) {
        menuPage = new MenuPage(this, highPermission);
        primaryStage.setScene(menuPage.getScene());
    }

    public void showUserPage() {
        userPage = new UserPage(this);
        primaryStage.setScene(userPage.getScene());
    }

    public void showAdminPage() throws NotBoundException, RemoteException {
        adminPage = new AdminPage(this);
        primaryStage.setScene(adminPage.getScene());
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setCenter(loginPage.getPane());
        Scene scene = new Scene(root, 400, 300);
        return scene;
    }

    public void logout() {
        primaryStage.setScene(getScene());
    }

    public Stage getStage() {
        return primaryStage;
    }
}