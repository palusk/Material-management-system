package project.client.user_interface.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import project.client.TableManager;
import project.client.UserSession;
import project.client.interfaces.*;
import project.server.rmi.DataManagement.AuthenticationLDAP;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class PendingOrdersTabCreator {
    public static Tab create(RemoteManager remoteManager, DataProviderRemote dataProvider) throws NotBoundException, RemoteException {


        AuthenticationLDAPRemote ldapConnect = remoteManager.getAuthenticationLDAP();
        boolean highPermission = false;
        AuthenticationLDAP ldapObject = new AuthenticationLDAP();
        int employeeType = Integer.parseInt(ldapObject.getUserEmployeeType(UserSession.userLogin));
        Tab tab = new Tab("All orders");
        TableManager tableManager = new TableManager();
        ProfilesManagerRemote profilesManager = remoteManager.getProfilesManager();
        ProductsManagerRemote productsManager = remoteManager.getProductsManager();
        TableView<ObservableList<String>> tableView = new TableView<>();
        Label resultLabel = new Label();


        Button generateProductsListButton = new Button("Generate Products List");
        Button cancelOrder = new Button("Cancel order");
        Button completeOrder = new Button("Complete order");

        generateProductsListButton.setDisable(true);
        cancelOrder.setDisable(true);
        completeOrder.setDisable(true);

        ComboBox<String> secondDropdown = createSecondDropdown(dataProvider, tableView, tableManager, productsManager,
                generateProductsListButton, cancelOrder, completeOrder, resultLabel);
        ComboBox<String> firstDropdown = createDropdown(profilesManager.getWarehouseDropdown(employeeType,
                UserSession.userLogin), dataProvider, tableView, tableManager, secondDropdown, productsManager, resultLabel);


        generateProductsListButton.setOnAction(event -> {

                if(secondDropdown.equals("Select a value")){
                    resultLabel.setText("Choose an order");
                }else{
                    try {
                        String selectedValue = secondDropdown.getValue();
                        int index = selectedValue.indexOf(" ");
                        String orderID = selectedValue.substring(0, index);
                        tableManager.printTable(productsManager.listProductsToTransfer(orderID), tableView);
                        resultLabel.setText("order list generated");
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
            }
        });

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
                    resultLabel.setText("order canceled");
                    generateProductsListButton.setDisable(true);
                    cancelOrder.setDisable(true);
                    completeOrder.setDisable(true);

                    secondDropdown.setDisable(false);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        completeOrder.setOnAction(event -> {

            if(secondDropdown.equals("Select a value")){
                resultLabel.setText("Choose an order");
            }else{
                try {
                    String selectedValue = secondDropdown.getValue();
                    int index = selectedValue.indexOf(" ");
                    String orderID = selectedValue.substring(0, index);
                    String output = productsManager.completeOrder(orderID);

                    if(!output.contains("error")){

                        String selectedWarehouseValue = firstDropdown.getValue();
                        int warehouseIndex = selectedWarehouseValue.indexOf(" ");
                        String warehouseID = selectedWarehouseValue.substring(0, warehouseIndex);
                        String ordersString = productsManager.getOrdersInWarehouse(warehouseID);
                        ordersString = ordersString.replace("\n", "");
                        String[] ordersArray = ordersString.split(";");
                        ObservableList<String> ordersList = FXCollections.observableArrayList(ordersArray);

                        secondDropdown.setItems(ordersList);
                        secondDropdown.setDisable(false);
                        resultLabel.setText("order completed");
                        generateProductsListButton.setDisable(true);
                        cancelOrder.setDisable(true);
                        completeOrder.setDisable(true);

                    } else{
                        resultLabel.setText("Order not completed! Please contact your admnistrator");
                    }

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

    private static ComboBox<String> createDropdown(List<String> warehouseDropdown, DataProviderRemote dataProvider,
                                                   TableView<ObservableList<String>> tableView, TableManager tableManager,
                                                   ComboBox<String> secondDropdown, ProductsManagerRemote productsManager,
                                                   Label resultLabel) {
        ComboBox<String> dropdown = new ComboBox<>();

        dropdown.setItems(FXCollections.observableArrayList(warehouseDropdown));
        dropdown.setValue("Select a warehouse");

        dropdown.setOnAction(event -> {
            try {
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
                resultLabel.setText("");
                System.out.println(ordersList);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        return dropdown;
    }

    private static ComboBox<String> createSecondDropdown(DataProviderRemote dataProvider, TableView<ObservableList<String>> tableView,
                                                         TableManager tableManager, ProductsManagerRemote productsManager,
                                                        Button generateProductsList, Button cancelOrder, Button completeOrder,
                                                         Label resultLabel) {
        ComboBox<String> dropdown = new ComboBox<>();

        // Początkowo drugi dropdown jest pusty
        dropdown.setValue("Select a value");
        dropdown.setDisable(true);
        dropdown.setOnAction(event -> {
            String selectedValue = dropdown.getValue();
            if(selectedValue != null){
            try {
                int index = selectedValue.indexOf(" ");
                String orderID = selectedValue.substring(0, index);
                System.out.println(orderID);
                System.out.println(dataProvider.getOrderDetails(orderID));
                tableManager.printTable(dataProvider.getOrderDetails(orderID), tableView);
                String orderStatus = productsManager.getOrderStatus(Integer.parseInt(orderID));
                System.out.println(orderStatus);
                resultLabel.setText("");
                if(orderStatus.equals("pending")){
                    generateProductsList.setDisable(false);
                    cancelOrder.setDisable(false);
                    completeOrder.setDisable(false);
                }else{
                    generateProductsList.setDisable(true);
                    cancelOrder.setDisable(true);
                    completeOrder.setDisable(true);
                }

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }});

        return dropdown;
    }
}
