package project.client.user_interface;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import project.server.rmi.DataManagement.AuthenticationLDAP;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class MenuPage {

    private WindowManager windowManager;
    private BorderPane pane;

    public MenuPage(WindowManager windowManager, boolean highPermission) {
        this.windowManager = windowManager;
        initialize(highPermission);
    }

    private void initialize(boolean highPermission) {
        pane = new BorderPane();

        Button userPageButton = new Button("Go to User Page");
        userPageButton.setOnAction(e -> windowManager.showUserPage());
        BorderPane.setAlignment(userPageButton, javafx.geometry.Pos.CENTER);
        pane.setTop(userPageButton);

        // --TODO SPRAWDZIĆ CZY MOŻNA WYCIĄGNĄĆ PARAMETR DO ROZPOZNANIA POZIOMU DANEGO UŻYTKOWNIKA (W CELU UZYSKANIA LUB OGRANICZENIA WYBRANYCH OPCJI SYSTEMU)

        if(highPermission) {
            Button adminPageButton = new Button("Go to Admin Page");
            adminPageButton.setOnAction(e -> {
                try {
                    windowManager.showAdminPage();
                } catch (NotBoundException ex) {
                    throw new RuntimeException(ex);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            });
            BorderPane.setAlignment(adminPageButton, javafx.geometry.Pos.CENTER);
            pane.setCenter(adminPageButton);
        }

        Button loginPageButton = new Button("Logout");
        loginPageButton.setOnAction(e -> handleLogoutButton());
        BorderPane.setAlignment(loginPageButton, javafx.geometry.Pos.CENTER);
        pane.setBottom(loginPageButton);
    }

    private void handleLogoutButton() {
        windowManager.logout();
    }

    public Scene getScene() {
        return new Scene(new BorderPane(pane), 400, 300);
    }
}