package project.server.rmi.DataManagement;

import project.server.rmi.database.Connector;
import java.sql.SQLException;
import java.util.List;

public class HierarchyManager {
    private static Connector database = new Connector();
    static ExcelImporter importer = new ExcelImporter();

    public static String loadEmployeesFromCSV(String csvFile) throws SQLException {
        try {
            List<String> dataList = importer.csvReader(csvFile);
            String error = HierarchyManager.insertDataIntoStaging(dataList);

            if (error.isEmpty()) {
                error = HierarchyManager.triggerValidation();
            } else {
                error = "Load for following rows failed: " + error;
            }

            return error;
        } catch (Exception e) {
            return "CSV read failed";
        }
    }

    private static String insertDataIntoStaging(List<String> inputString) {
        return new Connector().insertDataIntoStaging(inputString,"InsertStagingEmployees",5);
    }

    public static String triggerValidation() {
        return new Connector().callStoredProcedure("ProcessPendingRowsInEmployees", null, false);
    }

    public String getStagingErrors() {
        return new Connector().callStoredProcedure("GetEmployeesStagingErrors", null, true);
    }

    public static String clearStaging() {
        return new Connector().callStoredProcedure("DeleteAllEmployeesStagingRows", null, false);
    }

    public String getStagingTable() {
        return new Connector().callStoredProcedure("GetEmployeesStagingTable", null, true);
    }

    public static String refreshHierarchy() {
        return new Connector().callStoredProcedure("CalculateEmployeeHierarchy", null, false);
    }

    public String getHierarchy() {
        return new Connector().callStoredProcedure("getHierarchy", null, true);
    }
}
