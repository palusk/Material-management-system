package project.client.user_interface;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private WindowManager windowManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        windowManager = new WindowManager(primaryStage);

        LoginPage loginPage = new LoginPage(windowManager);
        windowManager.setLoginPage(loginPage);

        primaryStage.setTitle("Material management system");
        primaryStage.setScene(windowManager.getScene());
        primaryStage.show();
    }
}
