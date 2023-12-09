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

            return "CSV succesfull";
        } catch (Exception e) {
            // Log the exception using a logging framework
            return "CSV read failed";
        }
    }

    public static String insertDataIntoStaging(List<String> inputString) throws SQLException {
        String failedRows = "";
        Integer rowNumber = 0;
            for (String line : inputString) {
                if (!line.trim().isEmpty()) {
                    String[] columns = line.split(";");
                    if (columns.length == 4) {
                        try {
                            new Connector().call("InsertStagingProductInStock", columns, false);
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
        return failedRows;
    }

    public static String triggerValidation() {
        return new Connector().call("ProcessPendingRowsInProductStaging", null, true);
    }

    public String getStagingErrors() {
        return new Connector().call("GetProductStagingErrors", null, true);
    }

    public static void clearStaging() {
        new Connector().call("DeleteAllProductStagingRows", null, false);
    }

    public String getStagingTable() {
        return new Connector().call("GetProductStagingTable", null, true);
    }

}

