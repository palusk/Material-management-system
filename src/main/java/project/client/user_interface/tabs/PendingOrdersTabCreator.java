package project.client.user_interface.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import project.client.TableManager;
import project.client.interfaces.DataProviderRemote;
import project.client.interfaces.ProductsManagerRemote;
import project.client.interfaces.ProfilesManagerRemote;
import project.client.interfaces.RemoteManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class PendingOrdersTabCreator {
    public static Tab create(RemoteManager remoteManager, DataProviderRemote dataProvider) throws NotBoundException, RemoteException {
        Tab tab = new Tab("Pending orders");
        TableManager tableManager = new TableManager();
        ProfilesManagerRemote profilesManager = remoteManager.getProfilesManager();
        ProductsManagerRemote productsManager = remoteManager.getProductsManager();
        TableView<ObservableList<String>> tableView = new TableView<>();
        Label resultLabel = new Label();

        ComboBox<String> secondDropdown = createSecondDropdown(dataProvider, tableView, tableManager);
        ComboBox<String> firstDropdown = createDropdown(profilesManager.getWarehouseDropdown(2), dataProvider, tableView, tableManager, secondDropdown, productsManager);

        Button generateProductsListButton = new Button("Generate Products List");
        generateProductsListButton.setOnAction(event -> {

                if(secondDropdown.equals("Select a value")){
                    resultLabel.setText("Choose an order");
                }else{
                    try {
                        String selectedValue = secondDropdown.getValue();
                        int index = selectedValue.indexOf(" ");
                        String orderID = selectedValue.substring(0, index);
                        tableManager.printTable(productsManager.listProductsToTransfer(orderID), tableView);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
            }
        });

        Button cancelOrder = new Button("Cancel order");
        cancelOrder.setOnAction(event -> {

            if(secondDropdown.equals("Select a value")){
                resultLabel.setText("Choose an order");
            }else{
                try {
                    String selectedValue = secondDropdown.getValue();
                    int index = selectedValue.indexOf(" ");
                    String orderID = selectedValue.substring(0, index);
                    productsManager.cancelOrder(orderID);

                    String selectedWarehouseValue = firstDropdown.getValue();
                    int warehouseIndex = selectedWarehouseValue.indexOf(" ");
                    String warehouseID = selectedWarehouseValue.substring(0, warehouseIndex);
                    String ordersString = productsManager.getOrdersInWarehouse(warehouseID);
                    ordersString = ordersString.replace("\n", "");
                    String[] ordersArray = ordersString.split(";");
                    ObservableList<String> ordersList = FXCollections.observableArrayList(ordersArray);
                    secondDropdown.setItems(ordersList);
                    secondDropdown.setDisable(false);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button completeOrder = new Button("Complete order");
        completeOrder.setOnAction(event -> {

            if(secondDropdown.equals("Select a value")){
                resultLabel.setText("Choose an order");
            }else{
                try {
                    String selectedValue = secondDropdown.getValue();
                    int index = selectedValue.indexOf(" ");
                    String orderID = selectedValue.substring(0, index);
                    productsManager.completeOrder(orderID);

                    String selectedWarehouseValue = firstDropdown.getValue();
                    int warehouseIndex = selectedWarehouseValue.indexOf(" ");
                    String warehouseID = selectedWarehouseValue.substring(0, warehouseIndex);
                    String ordersString = productsManager.getOrdersInWarehouse(warehouseID);
                    ordersString = ordersString.replace("\n", "");
                    String[] ordersArray = ordersString.split(";");
                    ObservableList<String> ordersList = FXCollections.observableArrayList(ordersArray);
                    secondDropdown.setItems(ordersList);
                    secondDropdown.setDisable(false);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        VBox vbox = new VBox(firstDropdown, secondDropdown, generateProductsListButton, resultLabel,tableView, cancelOrder, completeOrder);
        vbox.setPadding(new Insets(10));

        tab.setContent(vbox);

        return tab;
    }

    private static ComboBox<String> createDropdown(List<String> warehouseDropdown, DataProviderRemote dataProvider, TableView<ObservableList<String>> tableView, TableManager tableManager, ComboBox<String> secondDropdown, ProductsManagerRemote productsManager) {
        ComboBox<String> dropdown = new ComboBox<>();

        dropdown.setItems(FXCollections.observableArrayList(warehouseDropdown));
        dropdown.setValue("Select a warehouse");

        dropdown.setOnAction(event -> {
            try {
                secondDropdown.setValue("Select a value");
                String selectedValue = dropdown.getValue();
                int index = selectedValue.indexOf(" ");
                String warehouseID = selectedValue.substring(0, index);
                // Pobierz jeden długi ciąg znaków
                String ordersString = productsManager.getOrdersInWarehouse(warehouseID);
                ordersString = ordersString.replace("\n", "");
                String[] ordersArray = ordersString.split(";");
                ObservableList<String> ordersList = FXCollections.observableArrayList(ordersArray);
                secondDropdown.setItems(ordersList);
                secondDropdown.setDisable(false);
                System.out.println(ordersList);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        return dropdown;
    }

    private static ComboBox<String> createSecondDropdown(DataProviderRemote dataProvider, TableView<ObservableList<String>> tableView, TableManager tableManager) {
        ComboBox<String> dropdown = new ComboBox<>();

        // Początkowo drugi dropdown jest pusty
        dropdown.setValue("Select a value");
        dropdown.setDisable(true);
        String selectedValue = dropdown.getValue();
        dropdown.setOnAction(event -> {
            if(selectedValue.isEmpty()){
            try {
                int index = selectedValue.indexOf(" ");
                String orderID = selectedValue.substring(0, index);
                tableManager.printTable(dataProvider.getOrderDetails(orderID), tableView);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }});

        return dropdown;
    }
}
