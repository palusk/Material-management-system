package project.client.user_interface.tabs;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.client.ExcelGenerator;
import project.client.TableManager;
import project.client.interfaces.HierarchyManagerRemote;
import project.client.interfaces.ProductsLoaderRemote;
import project.client.interfaces.ProfilesManagerRemote;
import project.client.interfaces.RemoteManager;
import project.client.user_interface.WindowManager;
import project.server.rmi.DataManagement.HierarchyManager;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class EmployeeManagementTabCreator {

    private TableView<ObservableList<String>> tableView;
    private FileChooser fileChooser;
    private File selectedEmployeesFile;
    private Label resultLabel;
    private HierarchyManagerRemote hierarchyManager;
    private ProfilesManagerRemote profilesManager;
    private TableManager tableManager;
    private ExcelGenerator excelGenerator;
    private WindowManager windowManager;

    public EmployeeManagementTabCreator(FileChooser fileChooser, WindowManager windowManager, RemoteManager remoteManager, HierarchyManagerRemote hierarchyManager, ProfilesManagerRemote profilesManager) {
        this.tableView =  new TableView<>();
        this.fileChooser = fileChooser;
        this.resultLabel =  new Label();
        this.hierarchyManager = hierarchyManager;
        this.profilesManager = profilesManager;
        this.tableManager = new TableManager();
        this.excelGenerator = new ExcelGenerator();
        this.windowManager = windowManager;
    }

    public Tab create() {
        Tab employeeTab = new Tab("Employee Management");
        VBox employeeButtonsVBox = new VBox(
                tableView,
                resultLabel,
                createButton("Select Employee CSV File", this::handleSelectEmployeeFile),
                createButton("Load Employee Data", this::handleLoadEmployeeData),
                createButton("Refresh Hierarchy", this::handleRefreshHierarchy),
                createButton("Show Employees Staging Table", this::handleShowEmployeeStagingTable),
                createButton("Clear Employees Staging Table", this::handleClearEmployeeStagingTable),
                createButton("Show Employees Staging Errors", this::handleShowEmployeeStagingErrors),
                createButton("Show Hierarchy", this::handleShowHierarchy),
                createButton("Refresh Profiles", this::handleRefreshProfiles),
                createButton("Show Users Profiles", this::handleShowUserProfiles),
                createButton("Generate Employee Excel", this::handleGenerateEmployeeExcel)
        );
        employeeTab.setContent(employeeButtonsVBox);
        return employeeTab;
    }

    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(e -> action.run());
        return button;
    }

    private void handleSelectEmployeeFile() {
        selectedEmployeesFile = fileChooser.showOpenDialog(windowManager.getStage());
    }

    private void handleLoadEmployeeData() {
        if (selectedEmployeesFile != null) {
            String result = null;
            try {
                result = hierarchyManager.loadEmployeesFromCSV(selectedEmployeesFile.getAbsolutePath());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            resultLabel.setText("Load Data Result: " + result);
        } else {
            resultLabel.setText("Please select a CSV file first.");
        }
    }

    private void handleRefreshHierarchy() {
        try {
            String stagingTable = hierarchyManager.refreshHierarchy();
            resultLabel.setText(stagingTable);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleShowEmployeeStagingTable() {
        try {
            String stagingTable = hierarchyManager.getStagingTable();
            tableManager.printTable(stagingTable, tableView);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleClearEmployeeStagingTable() {
        try {
            String stagingTable = hierarchyManager.clearStaging();
            resultLabel.setText(stagingTable);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleShowEmployeeStagingErrors() {
        try {
            String stagingErrorsTable = hierarchyManager.getStagingErrors();
            tableManager.printTable(stagingErrorsTable, tableView);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleShowHierarchy() {
        try {
            String hierarchyTable = hierarchyManager.getHierarchy();
            tableManager.printTable(hierarchyTable, tableView);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleRefreshProfiles() {
        try {
            String stagingTable = profilesManager.updateProfiles();
            resultLabel.setText(stagingTable);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleShowUserProfiles() {
        try {
            String profilesTable = profilesManager.getProfiles();
            tableManager.printTable(profilesTable, tableView);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleGenerateEmployeeExcel() {
        excelGenerator.generateExcelFile("Employee Data", new String[]{"firstname", "lastname", "email", "position", "warehouse_id"});
    }

}