package project.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.client.interfaces.HierarchyManagerRemote;
import project.client.interfaces.ProductsLoaderRemote;
import project.client.interfaces.ProfilesManagerRemote;
import project.client.interfaces.RemoteManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public class UserPanel extends Application {

    @Override
    public void start(Stage primaryStage) throws RemoteException, NotBoundException {
        primaryStage.setTitle("JavaFX Dropdown with Dynamic Options");

        // Tworzymy obiekt ComboBox
        ComboBox<String> dropdown = new ComboBox<>();

        RemoteManager remoteManager = new RemoteManagerImpl();
        ProfilesManagerRemote profilesManager = remoteManager.getProfilesManager();

        // Uzyskujemy dostępne opcje z procedury i ustawiamy je dla dropdowna
        dropdown.setItems(FXCollections.observableArrayList(profilesManager.getWarehouseDropdown(2)));

        // Ustawiamy domyślną wartość
        dropdown.setValue("Select an option");

        // Tworzymy kontener VBox i dodajemy do niego dropdown
        VBox vbox = new VBox(dropdown);
        vbox.setPadding(new Insets(10));

        // Tworzymy scenę i ustawiamy ją na primaryStage
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);

        // Wyświetlamy primaryStage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}