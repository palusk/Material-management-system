package project.client.user_interface;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

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

        primaryStage.getIcons().add(new Image("C:\\Users\\mateu\\IdeaProjects\\Material-management-system2\\src\\main\\java\\project\\client\\user_interface\\icon.png"));
        primaryStage.setTitle("Material management system");
        primaryStage.setScene(windowManager.getScene());
        primaryStage.show();
    }

}
