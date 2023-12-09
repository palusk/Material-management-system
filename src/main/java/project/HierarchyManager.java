package project;

import project.database.Connector;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
            // Log the exception using a logging framework
            return "CSV read failed";
        }
    }

    private static String insertDataIntoStaging(List<String> inputString) {
        String failedRows = "";
        Integer rowNumber = 0;
        String sqlQuery = "{call InsertStagingEmployees(?, ?, ?, ?, ?)}";


        for (String line : inputString) {
            if (!line.trim().isEmpty()) {
                String[] columns = line.split(";");
                if (columns.length == 5) {
                    new Connector().call("InsertStagingEmployees", columns, false);

                } else {
                    failedRows += rowNumber + ", ";
                }
            } else {
                failedRows += rowNumber + ", ";
            }
            rowNumber++;
        }
        return failedRows;
    }

    public static String triggerValidation() {
        return new Connector().call("ProcessPendingRowsInEmployees", null, false);
    }

    public String getStagingErrors() {
        return new Connector().call("GetEmployeesStagingErrors", null, true);
    }

    public static String clearStaging() {
        return new Connector().call("DeleteAllEmployeesStagingRows", null, false);
    }

    public String getStagingTable() {
        return new Connector().call("GetEmployeesStagingTable", null, true);
    }

    public static String refreshHierarchy() {
        return new Connector().call("CalculateEmployeeHierarchy", null, false);
    }

    public String getHierarchy() {
        return new Connector().call("getHierarchy", null, true);
    }
}
