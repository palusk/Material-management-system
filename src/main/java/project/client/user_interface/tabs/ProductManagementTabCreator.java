package project.client.user_interface.tabs;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import project.client.ExcelGenerator;
import project.client.RemoteManagerImpl;
import project.client.TableManager;
import project.client.interfaces.ProductsLoaderRemote;
import project.client.interfaces.RemoteManager;
import project.client.user_interface.WindowManager;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class ProductManagementTabCreator {

    private TableView<ObservableList<String>> tableView;
    private FileChooser fileChooser;
    private File selectedProductsFile;
    private Label resultLabel;
    private RemoteManager remoteManager;
    private ProductsLoaderRemote productsLoader;
    private TableManager tableManager;
    private ExcelGenerator excelGenerator;
    private WindowManager windowManager;

    public ProductManagementTabCreator(FileChooser fileChooser, WindowManager windowManager, RemoteManager remoteManager, ProductsLoaderRemote productsLoader) {
        this.tableView = new TableView<>();
        this.fileChooser = fileChooser;
        this.resultLabel = new Label();
        this.remoteManager = remoteManager;
        this.productsLoader = productsLoader;
        this.tableManager = new TableManager();
        this.excelGenerator = new ExcelGenerator();
        this.windowManager = windowManager;
    }

    public Tab create() {
        Tab productTab = new Tab("Product Management");

        // VBox zawierający TableView i resultLabel
        VBox vbox = new VBox(tableView, resultLabel);

        // Dodanie przycisków do VBox
        vbox.getChildren().addAll(
                createButton("Select Product CSV File", this::handleSelectProductFile),
                createButton("Load Product Data", this::handleLoadProductData),
                createButton("Get Product Staging Errors", this::handleGetProductStagingErrors),
                createButton("Clear Product Staging", this::handleClearProductStaging),
                createButton("Show Product Staging Table", this::handleShowProductStagingTable),
                createButton("Generate Product Excel", this::handleGenerateProductExcel)
        );

        productTab.setContent(vbox);
        return productTab;
    }

    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(e -> action.run());
        return button;
    }

    private void handleSelectProductFile() {
        selectedProductsFile = fileChooser.showOpenDialog(windowManager.getStage());
    }

    private void handleLoadProductData() {
        if (selectedProductsFile != null) {
            String result = null;
            try {
                result = productsLoader.loadProductsFromCSV(selectedProductsFile.getAbsolutePath());
                resultLabel.setText("Load Data Result: " + result);
            } catch (RemoteException e) {
                resultLabel.setText("Error loading data: Remote error - " + e.getMessage());
                e.printStackTrace(); // Tutaj możesz również obsłużyć błąd bardziej szczegółowo
            } catch (SQLException e) {
                resultLabel.setText("Error loading data: SQL error - " + e.getMessage());
                e.printStackTrace(); // Tutaj możesz również obsłużyć błąd bardziej szczegółowo
            } catch (Exception e) {
                resultLabel.setText("Error loading data: " + e.getMessage());
                e.printStackTrace(); // Tutaj możesz również obsłużyć błąd bardziej szczegółowo
            }
        } else {
            resultLabel.setText("Please select a CSV file first.");
        }
    }

    private void handleGetProductStagingErrors() {
        try {
            String stagingErrors = productsLoader.getStagingErrors();
            System.out.println(stagingErrors);
            tableManager.printTable(stagingErrors, tableView);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleClearProductStaging() {
        try {
            productsLoader.clearStaging();
            resultLabel.setText("Staging cleared");
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleShowProductStagingTable() {
        try {
            String stagingTable = productsLoader.getStagingTable();
            System.out.println(stagingTable);
            tableManager.printTable(stagingTable, tableView);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleGenerateProductExcel() {
        excelGenerator.generateExcelFile("Product Data", new String[]{"product_id", "quantity", "warehouse_id", "expiration_date"});
    }
}