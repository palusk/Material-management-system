package project.client;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.client.interfaces.HierarchyManagerRemote;
import project.client.interfaces.ProductsLoaderRemote;
import project.client.interfaces.ProfilesManagerRemote;
import project.client.interfaces.RemoteManager;


import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class ResourcesManagementApplication extends Application {

    private File selectedProductsFile;
    private File selectedEmployeesFile;
    private Label resultLabel;
    private ExcelGenerator excelGenerator;


    @Override
    public void start(Stage primaryStage) throws RemoteException, NotBoundException, SQLException {
        primaryStage.setTitle("JavaFX App");

        RemoteManager remoteManager = new RemoteManagerImpl();
        ProductsLoaderRemote productsLoader = remoteManager.getProductsLoader();
        HierarchyManagerRemote hierarchyManager = remoteManager.getHierarchyManager();
        ProfilesManagerRemote profilesManager = remoteManager.getProfilesManager();
        TableManager tableManager = new TableManager();

        FileChooser fileChooser = new FileChooser();

        excelGenerator = new ExcelGenerator();

        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.getItems().clear();
        tableView.getColumns().clear();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv")
        );

        // Przyciski dla zarządzania danymi produktów
        VBox productButtonsVBox = new VBox(
                new Button("Select Product CSV File"),
                new Button("Load Product Data"),
                new Button("Get Product Staging Errors"),
                new Button("Clear Product Staging"),
                new Button("Show Product Staging Table"),
                new Button("Generate Product Excel")
        );

        // Przyciski dla zarządzania danymi pracowników
        VBox employeeButtonsVBox = new VBox(
                new Button("Select Employee CSV File"),
                new Button("Load Employee Data"),
                new Button("Refresh Hierarchy"),
                new Button("Show Employees Staging Table"),
                new Button("Clear Employees Staging Table"),
                new Button("Show Employees Staging Errors"),
                new Button("Show Hierarchy"),
                new Button("Refresh Profiles"),
                new Button("Show Users Profiles"),
                new Button("Generate Employee Excel")

        );

        // Zakładki
        TabPane tabPane = new TabPane();
        Tab productTab = new Tab("Product Management", productButtonsVBox);
        Tab employeeTab = new Tab("Employee Management", employeeButtonsVBox);
        tabPane.getTabs().addAll(productTab, employeeTab);

        resultLabel = new Label();

        ((Button) productButtonsVBox.getChildren().get(0)).setOnAction(e -> {
            selectedProductsFile = fileChooser.showOpenDialog(primaryStage);
        });

        ((Button) productButtonsVBox.getChildren().get(1)).setOnAction(e -> {
            if (selectedProductsFile != null) {
                try {
                    String result = productsLoader.loadProductsFromCSV(selectedProductsFile.getAbsolutePath());
                    resultLabel.setText("Load Data Result: " + result);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                resultLabel.setText("Please select a CSV file first.");
            }
        });

        ((Button) productButtonsVBox.getChildren().get(2)).setOnAction(e -> {
            String stagingErrors = null;
            try {
                stagingErrors = productsLoader.getStagingErrors();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            tableManager.printTable(stagingErrors, tableView);
        });

        ((Button) productButtonsVBox.getChildren().get(3)).setOnAction(e -> {
            try {
                productsLoader.clearStaging();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            resultLabel.setText("Staging cleared");
        });

        ((Button) productButtonsVBox.getChildren().get(4)).setOnAction(e -> {
            String stagingTable = null;
            try {
                stagingTable = productsLoader.getStagingTable();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            tableManager.printTable(stagingTable, tableView);
        });

        ((Button) productButtonsVBox.getChildren().get(5)).setOnAction(e -> {
            excelGenerator.generateExcelFile("Product Data", new String[]{"product_id", "quantity", "warehouse_id", "expiration_date"});
        });


        ((Button) employeeButtonsVBox.getChildren().get(0)).setOnAction(e -> {
            selectedEmployeesFile = fileChooser.showOpenDialog(primaryStage);
        });

        ((Button) employeeButtonsVBox.getChildren().get(1)).setOnAction(e -> {
            if (selectedEmployeesFile != null) {
                try {
                    String result = hierarchyManager.loadEmployeesFromCSV(selectedEmployeesFile.getAbsolutePath());
                    resultLabel.setText("Load Data Result: " + result);
                } catch (SQLException | RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                resultLabel.setText("Please select a CSV file first.");
            }
        });

        ((Button) employeeButtonsVBox.getChildren().get(2)).setOnAction(e -> {
            String stagingTable = null;
            try {
                stagingTable = hierarchyManager.refreshHierarchy();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            resultLabel.setText(stagingTable);
        });

        ((Button) employeeButtonsVBox.getChildren().get(3)).setOnAction(e -> {
            String stagingTable = null;
            try {
                stagingTable = hierarchyManager.getStagingTable();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            tableManager.printTable(stagingTable, tableView);
        });

        ((Button) employeeButtonsVBox.getChildren().get(4)).setOnAction(e -> {
            String stagingTable = null;
            try {
                stagingTable = hierarchyManager.clearStaging();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            resultLabel.setText(stagingTable);
        });

        ((Button) employeeButtonsVBox.getChildren().get(5)).setOnAction(e -> {
            String stagingErrorsTable = null;
            try {
                stagingErrorsTable = hierarchyManager.getStagingErrors();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            tableManager.printTable(stagingErrorsTable, tableView);
        });

        ((Button) employeeButtonsVBox.getChildren().get(6)).setOnAction(e -> {
            String hierarchyTable = null;
            try {
                hierarchyTable = hierarchyManager.getHierarchy();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            tableManager.printTable(hierarchyTable, tableView);
        });

        ((Button) employeeButtonsVBox.getChildren().get(7)).setOnAction(e -> {
            String stagingTable = null;
            try {
                stagingTable = profilesManager.updateProfiles();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            resultLabel.setText(stagingTable);
        });

        ((Button) employeeButtonsVBox.getChildren().get(8)).setOnAction(e -> {
            String profilesTable = null;
            try {
                profilesTable = profilesManager.getProfiles();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            tableManager.printTable(profilesTable, tableView);
        });

        ((Button) employeeButtonsVBox.getChildren().get(9)).setOnAction(e -> {
            excelGenerator.generateExcelFile("Employee Data", new String[]{"firstname", "lastname", "email", "position", "warehouse_id"});
        });

        // Utworzenie głównego kontenera
        VBox mainVBox = new VBox(tabPane, resultLabel, tableView);
        Scene scene = new Scene(mainVBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}