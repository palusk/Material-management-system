package project.client.user_interface.tabs;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import project.client.interfaces.DataProviderRemote;
import project.client.interfaces.ProductsManagerRemote;
import project.client.interfaces.ProfilesManagerRemote;
import project.client.interfaces.RemoteManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderTabCreator {

    public static Tab create(RemoteManager remoteManager, DataProviderRemote dataProvider) throws NotBoundException, RemoteException {

        ProductsManagerRemote productsManager = remoteManager.getProductsManager();

        Tab tab = new Tab("Orders creator");
        ProfilesManagerRemote profilesManager = remoteManager.getProfilesManager();
        TableView<ObservableList<String>> tableView = new TableView<>();

        TableColumn<ObservableList<String>, String> column1 = new TableColumn<>("Product");
        TableColumn<ObservableList<String>, String> column2 = new TableColumn<>("Quantity");

        column1.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        column2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(1)));

        tableView.getColumns().addAll(column1, column2);

        Label resultLabel = new Label();

        ComboBox<String> productsDropdown = new ComboBox<>();
        productsDropdown.setValue("Select product");

        ComboBox<String> warehouseDropdown = createDropdown(profilesManager.getWarehouseDropdown(2), dataProvider, tableView, productsDropdown);

        TextField numericInput = new TextField();
        numericInput.setPromptText("Enter quantity");

        Button addToOrderButton = new Button("Add to Order");
        addToOrderButton.setOnAction(event -> {

            if (productsDropdown.getValue().equals("Select product")) {
                resultLabel.setText("Choose product first");
            } else {
            try {
                String selectedProduct = productsDropdown.getValue();
                String quantity = numericInput.getText();

                ObservableList<String> row = FXCollections.observableArrayList(selectedProduct, quantity);
                tableView.getItems().add(row);
                tableView.refresh();

                productsDropdown.setValue("Select product");
                numericInput.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }});

        // Dodaj przycisk "Create Order"
        Button createOrderButton = new Button("Create Order");
        createOrderButton.setOnAction(event -> {
            System.out.println("test");
            if (warehouseDropdown.getValue().equals("Select warehouse")) {
                resultLabel.setText("Choose warehouse first");
            } else {
                try {
                    // Pobierz dane z tabeli
                    StringBuilder orderDetails = new StringBuilder();
                    int wareHouseID = Integer.parseInt(warehouseDropdown.getValue().substring(0, warehouseDropdown.getValue().indexOf(" ")));
                    for (ObservableList<String> row : tableView.getItems()) {
                        String product = row.get(0);
                        String quantity = row.get(1);
                        int productID = Integer.parseInt(product.substring(0, product.indexOf(" ")));
                        orderDetails.append(productID).append(";").append(wareHouseID).append(";").append(quantity).append(";");
                    }
                    System.out.println(orderDetails.toString());
                    System.out.println(mergeAndSumOrders(orderDetails.toString()));
                    productsManager.insertStagingOrder(mergeAndSumOrders(orderDetails.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        VBox vbox = new VBox(warehouseDropdown, productsDropdown, numericInput, addToOrderButton, tableView, createOrderButton, resultLabel);
        vbox.setPadding(new javafx.geometry.Insets(10));

        tab.setContent(vbox);

        return tab;
    }

    private static ComboBox<String> createDropdown(List<String> warehouseDropdown, DataProviderRemote dataProvider, TableView<ObservableList<String>> tableView, ComboBox<String> productsDropdown) throws RemoteException {
        ComboBox<String> dropdown = new ComboBox<>();

        dropdown.setItems(FXCollections.observableArrayList(warehouseDropdown));
        dropdown.setValue("Select warehouse");

        ObservableList<String> products = extractFirstValuesFromColumns(dataProvider.getAllProducts());
        productsDropdown.setItems(products);
        productsDropdown.setValue("Select product");

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

    public static String mergeAndSumOrders(String inputString) {
        StringBuilder resultStringBuilder = new StringBuilder();
        Map<Integer, Integer> productQuantities = new HashMap<>();
        Map<Integer, Integer> productWarehouses = new HashMap<>();

        String[] values = inputString.split(";");
        int valueCount = 0;

        int product = -1;
        int warehouseID = -1;
        int lastQuantity = -1;

        for (String value : values) {
            switch (valueCount % 3) {
                case 0:
                    product = Integer.parseInt(value);
                    break;
                case 1:
                    warehouseID = Integer.parseInt(value);
                    break;
                case 2:
                    lastQuantity = Integer.parseInt(value);

                    if (productQuantities.containsKey(product)) {
                        int totalQuantity = productQuantities.get(product) + lastQuantity;
                        productQuantities.put(product, totalQuantity);
                    } else {
                        productQuantities.put(product, lastQuantity);
                    }

                    productWarehouses.put(product, warehouseID);
                    break;
            }

            valueCount++;
        }

        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            int mergedProduct = entry.getKey();
            int mergedWarehouseID = productWarehouses.get(mergedProduct);
            int mergedQuantity = entry.getValue();
            resultStringBuilder.append(mergedProduct).append(";").append(mergedWarehouseID).append(";").append(mergedQuantity).append(";");
        }

        return resultStringBuilder.toString();
    }
}
