package project.server.rmi.DataManagement;

import project.server.rmi.database.Connector;

public class ProductsManager {

    public static void insertStagingOrder(String orderDetails) {
        System.out.println(orderDetails);
        new Connector().callStoredProcedure("insertStagingOrder", new Object[]{orderDetails}, false);
    }

    public static String getOrdersInWarehouse(String warehouseID) {

        return new Connector().callStoredProcedure("getOrdersInWarehouse", new Object[]{warehouseID}, true,false);
    }

    public static String listProductsToTransfer(String orderIDs) {
        return new Connector().callStoredProcedure("listProductsToTransfer", new Object[]{orderIDs}, true,true);
    }

    public static String cancelOrder(String orderIDs) {
        return new Connector().callStoredProcedure("cancelOrder", new Object[]{orderIDs}, false,false);
    }

    public static String completeOrder(String orderIDs) {
        new Connector().callStoredProcedure("completeOrder", new Object[]{orderIDs}, false,false);
        return new Connector().callStoredProcedure("completeOrder", new Object[]{orderIDs}, false,false);
    }
}
