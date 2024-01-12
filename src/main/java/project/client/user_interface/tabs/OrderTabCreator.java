package project.client.user_interface.tabs;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import project.client.TableManager;
import project.client.interfaces.DataProviderRemote;
import project.client.interfaces.ProfilesManagerRemote;
import project.client.interfaces.RemoteManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class OrderTabCreator {

    public static Tab create(RemoteManager remoteManager, DataProviderRemote dataProvider) throws NotBoundException, RemoteException {
        Tab tab = new Tab("Orders creator");
        TableManager tableManager = new TableManager();
        ProfilesManagerRemote profilesManager = remoteManager.getProfilesManager();
        TableView<ObservableList<String>> tableView = new TableView<>();

        TableColumn<ObservableList<String>, String> column1 = new TableColumn<>("Product");
        TableColumn<ObservableList<String>, String> column2 = new TableColumn<>("Quantity");

        column1.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        column2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(1)));

        tableView.getColumns().addAll(column1, column2);

        ComboBox<String> selectProductDropdown = new ComboBox<>();
        selectProductDropdown.setValue("Select product");

        ComboBox<String> dropdown = createDropdown(profilesManager.getWarehouseDropdown(2), dataProvider, tableView, selectProductDropdown);

        TextField numericInput = new TextField();
        numericInput.setPromptText("Enter quantity");

        Button addToOrderButton = new Button("Add to Order");
        addToOrderButton.setOnAction(event -> {
            try {
                String selectedProduct = selectProductDropdown.getValue();
                String quantity = numericInput.getText();

                ObservableList<String> row = FXCollections.observableArrayList(selectedProduct, quantity);
                tableView.getItems().add(row);
                tableView.refresh();

                selectProductDropdown.setValue("Select product");
                numericInput.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        VBox vbox = new VBox(dropdown, selectProductDropdown, numericInput, addToOrderButton, tableView);
        vbox.setPadding(new javafx.geometry.Insets(10));

        tab.setContent(vbox);

        return tab;
    }

    private static ComboBox<String> createDropdown(List<String> warehouseDropdown, DataProviderRemote dataProvider, TableView<ObservableList<String>> tableView, ComboBox<String> selectProductDropdown) {
        ComboBox<String> dropdown = new ComboBox<>();

        dropdown.setItems(FXCollections.observableArrayList(warehouseDropdown));
        dropdown.setValue("Select an option");

        dropdown.setOnAction(event -> {
            try {
                String selectedValue = dropdown.getValue();
                int index = selectedValue.indexOf(" ");
                String warehouseID = selectedValue.substring(0, index);

                ObservableList<String> products = extractFirstValuesFromColumns(dataProvider.getWarehouseProducts(Integer.parseInt(warehouseID)));
                selectProductDropdown.setItems(products);
                selectProductDropdown.setValue("Select product");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        return dropdown;
    }

    private static ObservableList<String> extractFirstValuesFromColumns(String data) {
        ObservableList<String> options = FXCollections.observableArrayList();

        String[] rows = data.split("\n");

        for (int i = 1; i < rows.length; i++) {
            String row = rows[i];
            String[] columns = row.split(";");
            if (columns.length > 0) {
                options.add(columns[0]);
            }
        }

        return options;
    }
}