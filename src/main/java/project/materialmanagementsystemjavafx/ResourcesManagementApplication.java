package project.materialmanagementsystemjavafx;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.HierarchyManager;
import project.ProductsLoader;
import project.ProfilesManager;

import java.io.File;
import java.sql.SQLException;

public class ResourcesManagementApplication extends Application {

    private File selectedProductsFile;
    private File selectedEmployeesFile;
    private Label resultLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX App");
        ProductsLoader productsLoader = new ProductsLoader();
        HierarchyManager hierarchyManager = new HierarchyManager();
        ProfilesManager profilesManager = new ProfilesManager();
        FileChooser fileChooser = new FileChooser();

        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.getItems().clear();
        tableView.getColumns().clear();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv")
        );

        // Przyciski dla zarządzania danymi produktów
        VBox productButtonsVBox = new VBox(
                new Button("Select CSV File"),
                new Button("Load Product data"),
                new Button("Get Staging Errors"),
                new Button("Clear Staging"),
                new Button("Show Product Staging table")
        );

        // Przyciski dla zarządzania danymi pracowników
        VBox employeeButtonsVBox = new VBox(
                new Button("Select CSV File"),
                new Button("Load employee data"),
                new Button("Refresh hierarchy"),
                new Button("Show Employees Staging table"),
                new Button("Clear Employees Staging table"),
                new Button("Show Employees Staging Errors"),
                new Button("Show Hierarchy"),
                new Button("Refresh Profiles"),
                new Button("Show Users Profiles")
        );

        // Zakładki
        TabPane tabPane = new TabPane();
        Tab productTab = new Tab("Product Management", productButtonsVBox);
        Tab employeeTab = new Tab("Employee Management", employeeButtonsVBox);
        tabPane.getTabs().addAll(productTab, employeeTab);

        resultLabel = new Label();


        ((Button)productButtonsVBox.getChildren().get(0)).setOnAction(e -> {
            selectedProductsFile = fileChooser.showOpenDialog(primaryStage);
        });

        ((Button)productButtonsVBox.getChildren().get(1)).setOnAction(e -> {
            if (selectedProductsFile != null) {
                try {
                    String result = productsLoader.loadProductsFromCSV(selectedProductsFile.getAbsolutePath());
                    resultLabel.setText("Load Data Result: " + result);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                resultLabel.setText("Please select a CSV file first.");
            }
        });

        ((Button)productButtonsVBox.getChildren().get(2)).setOnAction(e -> {
            String stagingErrors = productsLoader.getStagingErrors();
            printTable(stagingErrors,tableView);
        });

        ((Button)productButtonsVBox.getChildren().get(3)).setOnAction(e -> {
            productsLoader.clearStaging();
            resultLabel.setText("Staging cleared");
        });

        ((Button)productButtonsVBox.getChildren().get(4)).setOnAction(e -> {
            String stagingTable = productsLoader.getStagingTable();
            printTable(stagingTable,tableView);
        });


        ((Button)employeeButtonsVBox.getChildren().get(0)).setOnAction(e -> {
            selectedEmployeesFile = fileChooser.showOpenDialog(primaryStage);

        });

        ((Button)employeeButtonsVBox.getChildren().get(1)).setOnAction(e -> {
            if (selectedEmployeesFile != null) {
                try {
                    String result = hierarchyManager.loadEmployeesFromCSV(selectedEmployeesFile.getAbsolutePath());
                    resultLabel.setText("Load Data Result: " + result);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                resultLabel.setText("Please select a CSV file first.");
            }
        });

        ((Button)employeeButtonsVBox.getChildren().get(2)).setOnAction(e -> {
            String stagingTable = hierarchyManager.refreshHierarchy();
            resultLabel.setText(stagingTable);
        });

        ((Button)employeeButtonsVBox.getChildren().get(3)).setOnAction(e -> {
            String stagingTable = hierarchyManager.getStagingTable();
            printTable(stagingTable,tableView);
        });

        ((Button)employeeButtonsVBox.getChildren().get(4)).setOnAction(e -> {
            String stagingTable = hierarchyManager.clearStaging();
            resultLabel.setText(stagingTable);
        });

        ((Button)employeeButtonsVBox.getChildren().get(5)).setOnAction(e -> {
            String stagingErrorsTable = hierarchyManager.getStagingErrors();
            printTable(stagingErrorsTable,tableView);
        });

        ((Button)employeeButtonsVBox.getChildren().get(6)).setOnAction(e -> {
            String hierarchyTable = hierarchyManager.getHierarchy();
            printTable(hierarchyTable,tableView);
        });

        ((Button)employeeButtonsVBox.getChildren().get(7)).setOnAction(e -> {
            String stagingTable = profilesManager.updateProfiles();
            resultLabel.setText(stagingTable);
        });

        ((Button)employeeButtonsVBox.getChildren().get(8)).setOnAction(e -> {
            String profilesTable = profilesManager.getProfiles();
            printTable(profilesTable,tableView);
        });


        // Utworzenie głównego kontenera
        VBox mainVBox = new VBox(tabPane, resultLabel, tableView);
        Scene scene = new Scene(mainVBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<ObservableList<String>> parseData(String dataString) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        String[] rows = dataString.split("\n");

        for (String row : rows) {
            String[] columns = row.split(";");
            ObservableList<String> rowData = FXCollections.observableArrayList(columns);
            data.add(rowData);
        }
        return data;
    }

    private void printTable(String tableData, TableView<ObservableList<String>> tableView) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        ObservableList<ObservableList<String>> data = parseData(tableData);

        if (data.isEmpty() || data.get(0).isEmpty()) {
            addNoDataColumn(tableView);
        } else {
            for (int i = 0; i < data.get(0).size(); i++) {
                final int index = i;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(data.get(0).get(i));

                column.setCellValueFactory(cellDataFeatures -> {
                    ObservableList<String> rowValues = cellDataFeatures.getValue();
                    return new SimpleStringProperty(rowValues.get(index));
                });

                tableView.getColumns().add(column);
            }

            data.remove(0);

            tableView.setItems(data);
        }
    }

    private void addNoDataColumn(TableView<ObservableList<String>> tableView) {
        TableColumn<ObservableList<String>, String> column = new TableColumn<>("No Data");
        column.setCellValueFactory(cellDataFeatures -> new SimpleStringProperty("NO DATA"));
        tableView.getColumns().add(column);

        // Ustawienie pustego wiersza, aby tekst "NO DATA" został wyświetlony
        tableView.setItems(FXCollections.emptyObservableList());
    }
}
