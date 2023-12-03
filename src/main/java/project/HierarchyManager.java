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

        String callProcedure = "{CALL GetProductStagingErrors()}";
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

    public static void clearStaging() {
        String storedProcedureCall = "{call DeleteAllProductStagingRows()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(storedProcedureCall)) {
            callableStatement.execute();
            System.out.println("staging cleared");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public String getStagingTable() {
        StringBuilder result = new StringBuilder();

        String callProcedure = "{CALL GetProductStagingTable()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(callProcedure)) {
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    int stagingId = resultSet.getInt("staging_id");
                    int productId = resultSet.getInt("product_id");
                    int quantity = resultSet.getInt("quantity");
                    int warehouseId = resultSet.getInt("warehouse_id");
                    String expirationdate = resultSet.getString("expiration_date");
                    String errorMessage = resultSet.getString("error_message");
                    Timestamp loadTimestamp = resultSet.getTimestamp("load_timestamp");

                    result.append("Staging ID: ").append(stagingId)
                            .append(", Product Id: ").append(productId)
                            .append(", Quantity: ").append(quantity)
                            .append(", Warehouse Id: ").append(warehouseId)
                            .append(", Expiration date: ").append(expirationdate)
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
}
