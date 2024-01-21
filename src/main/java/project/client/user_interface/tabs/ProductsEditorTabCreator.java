package project.client.user_interface.tabs;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tab;
import project.client.RemoteManagerImpl;
import project.client.TableManager;
import project.client.interfaces.DataProviderRemote;
import project.client.interfaces.RemoteManager;

import java.rmi.RemoteException;

public class ProductsEditorTabCreator {
    Button addButton;
    Button editButton;
    Button deleteButton;
    TableManager tableManager = new TableManager();
    RemoteManager remoteManager = null;
    DataProviderRemote dataProvider = null;
    TableView<ObservableList<String>> tableView = new TableView<>();
    String tableName;
    TextField productIdField;
    TextField firstValueField;
    TextField secondValueField;
    TextField thirdValueField;
    public Tab create() {
        BorderPane root = new BorderPane();

        try {
            remoteManager = new RemoteManagerImpl();
            dataProvider = remoteManager.getDataProvider();


        } catch (Exception e) {
            e.printStackTrace();
        }


        productIdField = new TextField();
        productIdField.setPromptText("ID");
        firstValueField = new TextField();
        firstValueField.setPromptText("First value");
        secondValueField = new TextField();
        secondValueField.setPromptText("Second value");
        thirdValueField = new TextField();
        thirdValueField.setPromptText("Second value");

        addButton = new Button("Add");
        addButton.setOnAction(e -> {addProduct(productIdField.getText(), firstValueField.getText(), secondValueField.getText()); refreshTable();});

        editButton = new Button("Edit");
        editButton.setOnAction(e -> {editProduct(productIdField.getText(), firstValueField.getText(), secondValueField.getText(), thirdValueField.getText());refreshTable();});

        deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {deleteProduct(productIdField.getText(), firstValueField.getText(), secondValueField.getText());refreshTable();});

        Button showEmployeesButton = new Button("Show Employees");
        showEmployeesButton.setOnAction(e -> handleShowEmployees());

        Button showOrdersButton = new Button("Show Orders");
        showOrdersButton.setOnAction(e -> handleShowOrders());

        Button showProductsButton = new Button("Show Products");
        showProductsButton.setOnAction(e -> handleShowProducts());

        Button showProductsInStockButton = new Button("Show Products in Stock");
        showProductsInStockButton.setOnAction(e -> handleShowProductsInStock());

        Button showTransferHistoryButton = new Button("Show Transfer History");
        showTransferHistoryButton.setOnAction(e -> handleShowTransferHistory());

        Button showWarehousesButton = new Button("Show Warehouses");
        showWarehousesButton.setOnAction(e -> handleShowWarehouses());

        root.setCenter(tableView);

        addButton.setDisable(true);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        BorderPane formPane = new BorderPane();
        formPane.setPadding(new Insets(10));
        formPane.setCenter(new VBox(5, productIdField, firstValueField, secondValueField, thirdValueField));
        formPane.setRight(new VBox(5, addButton, editButton, deleteButton, showEmployeesButton,
                showOrdersButton, showProductsButton, showProductsInStockButton,
                showTransferHistoryButton, showWarehousesButton));

        root.setBottom(formPane);

        Tab databaseTab = new Tab("Database");
        databaseTab.setContent(root);

        return databaseTab;
    }

    private void addProduct(String productId, String firstValue, String secondValue) {
        try {
            dataProvider.add(productId, tableName, firstValue, secondValue);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void editProduct(String productId, String firstValue, String secondValue, String thirdValue) {
        try {

            if (tableName.equals("products_in_stock")){
                dataProvider.edit2(productId, tableName, firstValue, secondValue, thirdValue);
            } else {
                dataProvider.edit(productId, tableName, firstValue, secondValue);
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void deleteProduct(String productId, String firstValue, String secondValue) {
        try {
            dataProvider.delete(productId, tableName, firstValue, secondValue);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void handleShowEmployees() {
        try {
            tableManager.printTable(dataProvider.getAllEmployees(), tableView);
            tableName = "employees";
            addButton.setDisable(true);
            editButton.setDisable(true);
            deleteButton.setDisable(false);
            productIdField.setPromptText("ID");
            productIdField.setDisable(false);
            firstValueField.setDisable(true);
            secondValueField.setDisable(true);
            thirdValueField.setDisable(true);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void handleShowOrders() {
        try {
            tableManager.printTable(dataProvider.getAllOrders(), tableView);
            tableName = "pending_orders";
            addButton.setDisable(true);
            editButton.setDisable(true);
            deleteButton.setDisable(false);
            productIdField.setPromptText("ID");
            productIdField.setDisable(false);
            firstValueField.setDisable(true);
            secondValueField.setDisable(true);
            thirdValueField.setDisable(true);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void handleShowProducts() {
        try {
            tableManager.printTable(dataProvider.getAllProductsAdmin(), tableView);
            tableName = "products";
            addButton.setDisable(false);
            editButton.setDisable(false);
            deleteButton.setDisable(true);
            productIdField.setPromptText("Product ID");
            productIdField.setDisable(false);
            firstValueField.setPromptText("Product Name");
            firstValueField.setDisable(false);
            secondValueField.setPromptText("Unit");
            secondValueField.setDisable(false);
            thirdValueField.setDisable(true);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void handleShowProductsInStock() {
        try {
            tableManager.printTable(dataProvider.getAllProductsInStock(), tableView);
            tableName = "products_in_stock";
            addButton.setDisable(true);
            editButton.setDisable(false);
            deleteButton.setDisable(false);
            productIdField.setPromptText("Product ID");
            productIdField.setDisable(false);
            firstValueField.setPromptText("Warehouse ID");
            firstValueField.setDisable(false);
            secondValueField.setPromptText("Date");
            secondValueField.setDisable(false);
            thirdValueField.setPromptText("Quantity");
            thirdValueField.setDisable(false);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void handleShowTransferHistory() {
        try {
            tableManager.printTable(dataProvider.getTransferHistory(), tableView);
            tableName = "transfer_histo";
            addButton.setDisable(true);
            editButton.setDisable(true);
            deleteButton.setDisable(true);
            productIdField.setDisable(true);
            firstValueField.setDisable(true);
            secondValueField.setDisable(true);
            thirdValueField.setDisable(true);
        }catch (Exception e){
            System.out.println(e);
        }
    }


    private void handleShowWarehouses() {
        try {
            tableManager.printTable(dataProvider.getAllWarehousesAdmin(), tableView);
            tableName = "warehouses";
            addButton.setDisable(false);
            editButton.setDisable(false);
            deleteButton.setDisable(true);
            productIdField.setPromptText("Warehouse ID");
            productIdField.setDisable(false);
            firstValueField.setPromptText("Warehouse name");
            firstValueField.setDisable(false);
            secondValueField.setDisable(true);
            thirdValueField.setDisable(true);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void refreshTable(){
        switch (tableName){
            case "warehouses":
                handleShowWarehouses();
                break;
            case "transfer_histo":
                handleShowTransferHistory();
                break;
            case "products_in_stock":
                handleShowProductsInStock();
                break;
            case "products":
                handleShowProducts();
                break;
            case "pending_orders":
                handleShowOrders();
                break;
            case "employees":
                handleShowEmployees();
                break;
            default:
                break;
        }
    }
}