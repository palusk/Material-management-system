package project.client.user_interface.tabs;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import project.client.user_interface.WindowManager;

public class ProfileTabCreator {

    public static Tab create(WindowManager windowManager) {
        Tab tab = new Tab("Profile");

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            windowManager.logout();
        });

        VBox vbox = new VBox(logoutButton);
        vbox.setPadding(new Insets(10));

        // Create a new BorderPane instance for each scene
        tab.setContent(new VBox(new BorderPane(), vbox));

        return tab;
    }
}