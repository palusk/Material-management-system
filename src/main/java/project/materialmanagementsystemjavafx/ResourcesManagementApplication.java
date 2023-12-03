package project.materialmanagementsystemjavafx;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.HierarchyManager;
import project.ProductsLoader;

import java.io.File;
import java.sql.SQLException;

public class ResourcesManagementApplication extends Application {

    private File selectedFile;
    private Label resultLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX App");
        ProductsLoader productsLoader = new ProductsLoader();
        HierarchyManager hierarchyManager = new HierarchyManager();
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv")
        );

        Button selectCSVButton = new Button("Select CSV File");
        selectCSVButton.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(primaryStage);
        });

        resultLabel = new Label();

        Button loadProductData = new Button("Load Product data");
        loadProductData.setOnAction(e -> {
            if (selectedFile != null) {
                try {
                    String result = productsLoader.loadProductsFromCSV(selectedFile.getAbsolutePath());
                    System.out.println(result);
                    resultLabel.setText("Load Data Result: " + result);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                resultLabel.setText("Please select a CSV file first.");
            }
        });

        Button errorsButton = new Button("Get Staging Errors");
        errorsButton.setOnAction(e -> {
            String stagingErrors = productsLoader.getStagingErrors();
            System.out.println(stagingErrors);
            resultLabel.setText("Staging Errors: " + stagingErrors);
        });

        Button clearStagingButton = new Button("Clear Staging");
        clearStagingButton.setOnAction(e -> {
            productsLoader.clearStaging();
            resultLabel.setText("Staging cleared");
        });

        Button showStagingTableButton = new Button("Show Staging table");
        showStagingTableButton.setOnAction(e -> {
            String stagingTable = productsLoader.getStagingTable();
            resultLabel.setText(stagingTable);
        });



        Button loadEmployeeButton = new Button("Load employee data");
        loadEmployeeButton.setOnAction(e -> {
            System.out.println("test");
            if (selectedFile != null) {
                try {
                    System.out.println("test");
                    String result = hierarchyManager.loadEmployeesFromCSV(selectedFile.getAbsolutePath());
                    System.out.println(result);
                    resultLabel.setText("Load Data Result: " + result);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                resultLabel.setText("Please select a CSV file first.");
            }
        });

        VBox vBox = new VBox(selectCSVButton, loadProductData,errorsButton, clearStagingButton, showStagingTableButton, loadEmployeeButton, resultLabel);
        Scene scene = new Scene(vBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}