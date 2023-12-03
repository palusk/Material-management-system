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

        try (CallableStatement callableStatement = database.getCon().prepareCall(sqlQuery)) {
            for (String line : inputString) {
                if (!line.trim().isEmpty()) {
                    String[] columns = line.split(";");
                    if (columns.length == 5) {
                        try {
                            callableStatement.setString(1, columns[0]);
                            callableStatement.setString(2, columns[1]);
                            callableStatement.setString(3, columns[2]);
                            callableStatement.setString(4, columns[3]);
                            callableStatement.setInt(5, Integer.parseInt(columns[4]));
                            callableStatement.execute();
                            rowNumber++;
                        } catch (NumberFormatException e) {
                            System.out.println(e);
                            failedRows += rowNumber + ", ";
                        }
                    } else {
                        failedRows += rowNumber + ", ";
                    }
                } else {
                    failedRows += rowNumber + ", ";
                }
                rowNumber++;
            }
        } catch (SQLException e) {
            System.out.println(e);
            return "Connection with database failed";
        }
        return failedRows;
    }

    public static String triggerValidation() {
        String storedProcedureCall = "{call ProcessPendingRowsInEmployees()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(storedProcedureCall)) {
            callableStatement.execute();
            return "Load successful check staging table for errors";
        } catch (SQLException e) {
            System.out.println(e);
            return "Staging table validation failed";
        }
    }

    public String getStagingErrors() {
        StringBuilder result = new StringBuilder();

        String callProcedure = "{CALL GetEmployeesStagingErrors()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(callProcedure)) {
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    int stagingId = resultSet.getInt("staging_id");
                    String errorMessage = resultSet.getString("error_message");
                    Timestamp loadTimestamp = resultSet.getTimestamp("load_timestamp");

                    result.append("Staging ID: ").append(stagingId)
                            .append(", Error Message: ").append(errorMessage)
                            .append(", Load Timestamp: ").append(loadTimestamp)
                            .append("\n");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result.toString();
    }

    public static String clearStaging() {
        String storedProcedureCall = "{call DeleteAllEmployeesStagingRows()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(storedProcedureCall)) {
            callableStatement.execute();
            return "staging cleared";
        } catch (SQLException e) {
            return "Staging clear failed";
        }
    }

    public String getStagingTable() {
        StringBuilder result = new StringBuilder();

        String callProcedure = "{CALL GetEmployeesStagingTable()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(callProcedure)) {
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    int stagingId = resultSet.getInt("staging_id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    String position = resultSet.getString("position");
                    int warehouse_id = resultSet.getInt("warehouse_id");
                    String load_status = resultSet.getString("load_status");
                    String error_message = resultSet.getString("error_message");
                    Timestamp load_timestamp = resultSet.getTimestamp("load_timestamp");


                    result.append("Staging ID: ").append(stagingId)
                            .append(", First name: ").append(first_name)
                            .append(", last name: ").append(last_name)
                            .append(", Email: ").append(email)
                            .append(", Position: ").append(position)
                            .append(", Warehouse ID: ").append(warehouse_id)
                            .append(", Load status: ").append(load_status)
                            .append(", Error message: ").append(error_message)
                            .append(", Load Timestamp: ").append(load_timestamp)
                            .append("\n");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result.toString();
    }

    public static String refreshHierarchy() {
        String storedProcedureCall = "{call CalculateEmployeeHierarchy()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(storedProcedureCall)) {
            callableStatement.execute();
            return "Hierarchy refreshed";
        } catch (SQLException e) {
            return "Refresh failed";
        }
    }

    public String getHierarchy() {
        StringBuilder result = new StringBuilder();

        String callProcedure = "{CALL getHierarchy()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(callProcedure)) {
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    int employee_id = resultSet.getInt("employee_id");
                    int descendant_id = resultSet.getInt("descendant_id");
                    int level = resultSet.getInt("level");
                    int warehouse_id = resultSet.getInt("warehouse_id");


                    result.append("Employee ID: ").append(employee_id)
                            .append(", Descendant ID: ").append(descendant_id)
                            .append(", Level: ").append(level)
                            .append(", Warehouse ID: ").append(warehouse_id)
                            .append("\n");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result.toString();
    }
}
