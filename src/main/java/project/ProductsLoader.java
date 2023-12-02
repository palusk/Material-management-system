package project;

import project.database.Connector;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ProductsLoader {

    private static Connector database = new Connector();
    private static ExcelImporter importer = new ExcelImporter();

    public static String loadProductsFromCSV(String csvFile) throws SQLException {
        try {
            List<String> dataList = importer.csvReader(csvFile);
            String error = ProductsLoader.insertDataIntoStaging(dataList);

            if (error.isEmpty()) {
                error = ProductsLoader.triggerValidation();
            } else {
                error = "Load for following rows failed: " + error;
            }

            return error;
        } catch (Exception e) {
            // Log the exception using a logging framework
            return "CSV read failed";
        }
    }

    public static String insertDataIntoStaging(List<String> inputString) throws SQLException {
        String failedRows = "";
        Integer rowNumber = 0;
        String sqlQuery = "{call InsertStagingProductInStock(?, ?, ?, ?)}";

        try (CallableStatement callableStatement = database.getCon().prepareCall(sqlQuery)) {
            for (String line : inputString) {
                if (!line.trim().isEmpty()) {
                    String[] columns = line.split(";");
                    if (columns.length >= 4) {
                        try {
                            callableStatement.setInt(1, Integer.parseInt(columns[0]));
                            callableStatement.setInt(2, Integer.parseInt(columns[1]));
                            callableStatement.setInt(3, Integer.parseInt(columns[2]));
                            callableStatement.setDate(4, java.sql.Date.valueOf(columns[3]));

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
        String storedProcedureCall = "{call ProcessPendingRowsInStaging()}";
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

        String callProcedure = "{CALL GetStagingErrorsSortedByTimestamp()}";
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
        String storedProcedureCall = "{call DeleteAllStagingRows()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(storedProcedureCall)) {
            callableStatement.execute();
            System.out.println("staging cleared");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public String getStagingTable() {
        StringBuilder result = new StringBuilder();

        String callProcedure = "{CALL GetStagingTable()}";
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

