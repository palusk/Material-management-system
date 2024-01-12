package project.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.client.TableManager;
import project.client.interfaces.DataProviderRemote;
import project.client.interfaces.ProfilesManagerRemote;
import project.client.interfaces.RemoteManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserPanel extends Application {

    @Override
    public void start(Stage primaryStage) throws RemoteException, NotBoundException {
        primaryStage.setTitle("JavaFX Dropdown with Dynamic Options");

        RemoteManager remoteManager = new RemoteManagerImpl();
        DataProviderRemote dataProvider = remoteManager.getDataProvider();

        // Tworzymy zakładki
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createTab("Tab 1", remoteManager, dataProvider),
                createTab("Tab 2", remoteManager, dataProvider)
        );

        // Tworzymy scenę i ustawiamy ją na primaryStage
        Scene scene = new Scene(tabPane, 600, 400);
        primaryStage.setScene(scene);

        // Wyświetlamy primaryStage
        primaryStage.show();
    }

    private Tab createTab(String tabTitle, RemoteManager remoteManager, DataProviderRemote dataProvider) throws NotBoundException, RemoteException {
        Tab tab = new Tab(tabTitle);

        TableManager tableManager = new TableManager();
        ProfilesManagerRemote profilesManager = remoteManager.getProfilesManager();
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.getItems().clear();
        tableView.getColumns().clear();

        // Tworzymy obiekt ComboBox dla pierwszej kolumny
        ComboBox<String> dropdown1 = createComboBoxFromWarehouseProducts(dataProvider.getWarehouseProducts(1));

        // Tworzymy obiekt ComboBox dla drugiej kolumny
        ComboBox<String> dropdown2 = createComboBoxFromWarehouseProducts(dataProvider.getWarehouseProducts(2));

        // Stary dropdown, który pozostaje niezmieniony
        ComboBox<String> oldDropdown = new ComboBox<>();
        oldDropdown.setItems(FXCollections.observableArrayList(profilesManager.getWarehouseDropdown(2)));
        oldDropdown.setValue("Select an option");

        // Pole tekstowe do wprowadzania dowolnej liczby
        TextField numericInput = new TextField();
        numericInput.setPromptText("Wprowadź liczbę");

        // Przycisk "Start"
        Button startButton = new Button("Start");
        startButton.setOnAction(event -> {
            try {
                // Pobieramy wprowadzoną liczbę
                String numericValue = numericInput.getText();
                // Wykonujemy operacje związane z wprowadzoną liczbą (możesz dostosować według potrzeb)
                System.out.println("Wprowadzona liczba: " + numericValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Tworzymy kontener VBox i dodajemy do niego oba ComboBoxy, stary dropdown, pole tekstowe i przycisk
        VBox vbox = new VBox(dropdown1, dropdown2, oldDropdown, numericInput, startButton, tableView);
        vbox.setPadding(new Insets(10));

        // Ustawiamy zawartość zakładki
        tab.setContent(vbox);

        return tab;
    }

    private ComboBox<String> createComboBoxFromWarehouseProducts(String warehouseProducts) {
        ComboBox<String> comboBox = new ComboBox<>();
        ObservableList<String> options = extractFirstValuesFromColumns(warehouseProducts);
        comboBox.setItems(options);
        comboBox.setValue("Select an option");

        comboBox.setOnAction(event -> {
            try {
                String selectedValue = comboBox.getValue();
                // Możesz dodać tutaj logikę na podstawie wybranej opcji
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return comboBox;
    }

    private ObservableList<String> extractFirstValuesFromColumns(String data) {
        ObservableList<String> options = FXCollections.observableArrayList();

        String[] rows = data.split("\n");
        for (String row : rows) {
            String[] columns = row.split(";");
            if (columns.length > 0) {
                options.add(columns[0]);
            }
        }

        return options;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
