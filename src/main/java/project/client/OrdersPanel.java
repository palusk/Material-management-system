package project.client;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.client.interfaces.DataProviderRemote;
import project.client.interfaces.ProfilesManagerRemote;
import project.client.interfaces.RemoteManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class OrdersPanel extends Application {

    @Override
    public void start(Stage primaryStage) throws RemoteException, NotBoundException {
        primaryStage.setTitle("JavaFX Dropdown with Dynamic Options");

        RemoteManager remoteManager = new RemoteManagerImpl();
        DataProviderRemote dataProvider = remoteManager.getDataProvider();

        // Tworzymy zakładki
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createTab("Tab 1", remoteManager, dataProvider),
                createTab2("Tab 2", remoteManager, dataProvider)
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

        // Tworzymy obiekt ComboBox
        ComboBox<String> dropdown = new ComboBox<>();

        // Uzyskujemy dostępne opcje z procedury i ustawiamy je dla dropdowna
        dropdown.setItems(FXCollections.observableArrayList(profilesManager.getWarehouseDropdown(2)));

        // Ustawiamy domyślną wartość
        dropdown.setValue("Select an option");

        // uruchom po wybraniu wartosci
        dropdown.setOnAction(event -> {
            try {
                // Pobieramy wybraną wartość z ComboBox
                String selectedValue = dropdown.getValue();
                int index = selectedValue.indexOf(" ");
                String warehouseID = selectedValue.substring(0, index);

                // Pobieramy dane z serwera na podstawie wybranej wartości i aktualizujemy TableView
                tableManager.printTable(dataProvider.getWarehouseProducts(Integer.parseInt(warehouseID)), tableView);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        // Tworzymy kontener VBox i dodajemy do niego dropdown
        VBox vbox = new VBox(dropdown, tableView);
        vbox.setPadding(new Insets(10));

        // Ustawiamy zawartość zakładki
        tab.setContent(vbox);

        return tab;
    }

    private Tab createTab2(String tabTitle, RemoteManager remoteManager, DataProviderRemote dataProvider) throws NotBoundException, RemoteException {
        Tab tab = new Tab(tabTitle);

        TableManager tableManager = new TableManager();
        ProfilesManagerRemote profilesManager = remoteManager.getProfilesManager();
        TableView<ObservableList<String>> tableView2 = new TableView<>();
        TableColumn<ObservableList<String>, String> column1Tab2 = new TableColumn<>("Product");
        TableColumn<ObservableList<String>, String> column2Tab2 = new TableColumn<>("Quantity");

        column1Tab2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        column2Tab2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(1)));

        tableView2.getColumns().addAll(column1Tab2, column2Tab2);

        // Tworzymy obiekt ComboBox
        ComboBox<String> dropdown = new ComboBox<>();

        // Uzyskujemy dostępne opcje z procedury i ustawiamy je dla dropdowna
        dropdown.setItems(FXCollections.observableArrayList(profilesManager.getWarehouseDropdown(2)));

        // Ustawiamy domyślną wartość
        dropdown.setValue("Select an option");

        // Tworzymy obiekt ComboBox dla pierwszej kolumny
        ComboBox<String> dropdown1 = new ComboBox<>();
        dropdown1.setValue("Select product");

        // Pole tekstowe do wprowadzania liczby
        TextField numericInput = new TextField();
        numericInput.setPromptText("Enter quantity");

        // Przycisk "Add to Order"
        Button addToOrderButton = new Button("Add to Order");
        addToOrderButton.setOnAction(event -> {
            try {
                String selectedProduct = dropdown1.getValue();
                String quantity = numericInput.getText();

                // Dodaj nowy wiersz do tabeli
                ObservableList<String> row = FXCollections.observableArrayList(selectedProduct, quantity);
                tableView2.getItems().add(row);

                // Odśwież widok tabeli
                tableView2.refresh();

                // Wyczyść pola po dodaniu do zamówienia
                dropdown1.setValue("Select product");
                numericInput.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // uruchom po wybraniu wartości
        dropdown.setOnAction(event -> {
            try {
                // Pobieramy wybraną wartość z ComboBox
                String selectedValue = dropdown.getValue();
                int index = selectedValue.indexOf(" ");
                String warehouseID = selectedValue.substring(0, index);

                // Tworzymy nowy ComboBox dla produktów na podstawie wybranego magazynu
                ObservableList<String> products = extractFirstValuesFromColumns(dataProvider.getWarehouseProducts(Integer.parseInt(warehouseID)));
                dropdown1.setItems(products);
                dropdown1.setValue("Select product");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        // Dodajemy elementy do VBox
        VBox vbox = new VBox(dropdown, dropdown1, numericInput, addToOrderButton, tableView2);
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

        // Rozpocznij pętlę od 1, aby pominąć pierwszy wiersz z nazwami kolumn
        for (int i = 1; i < rows.length; i++) {
            String row = rows[i];
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
