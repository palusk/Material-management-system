package project.server.rmi.DataManagement;

import project.server.rmi.database.Connector;
import java.sql.SQLException;
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
            System.out.println(error);
            return "CSV succesfull";
        } catch (Exception e) {
            // Log the exception using a logging framework
            return "CSV read failed";
        }
    }

    public static String insertDataIntoStaging(List<String> inputString) throws SQLException {
        return new Connector().insertDataIntoStaging(inputString,"InsertStagingProductInStock",4);
    }

    public static String triggerValidation() {
        return new Connector().callStoredProcedure("ProcessPendingRowsInProductStaging", null, false);
    }

    public String getStagingErrors() {
        return new Connector().callStoredProcedure("GetProductStagingErrors", null, true);
    }

    public static void clearStaging() {
        new Connector().callStoredProcedure("DeleteAllProductStagingRows", null, false);
    }

    public String getStagingTable() {
        return new Connector().callStoredProcedure("GetProductStagingTable", null, true);
    }

}

