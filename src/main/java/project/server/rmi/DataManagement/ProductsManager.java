package project.server.rmi.DataManagement;

import project.client.user_interface.tabs.database.Connector;

import java.util.Arrays;
import java.util.List;

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
        //new Connector().callStoredProcedure("completeOrder", new Object[]{orderIDs}, false,false);
        return new Connector().callStoredProcedure("completeOrder", new Object[]{orderIDs}, false,false);
    }

    public static String getAllWarehousesString() {
        return new Connector().callStoredProcedure("getAllWarehouses", null, true,false);
    }

    public static String getAllWarehousesStringWithIgnore(String ignoreString) {
        return new Connector().callStoredProcedure("GetAllWarehousesWithIgnore",  new Object[]{ignoreString}, true,false);
    }


    public List<String> getAllWarehouses(){
            String warehouseString = getAllWarehousesString();
            List<String> warehouseList = Arrays.asList(warehouseString.split("\n"));
            System.out.println(warehouseList);
            return warehouseList;
    }

    public List<String> getAllWarehousesWithIgnore(String loginUser, int employeeType){
        if(employeeType == 0) {
            String warehouseString = getAllWarehousesString();
            List<String> warehouseList = Arrays.asList(warehouseString.split("\n"));
            System.out.println(warehouseList);
            return warehouseList;
        }else{
            String warehouseString = getAllWarehousesStringWithIgnore(loginUser);
            List<String> warehouseList = Arrays.asList(warehouseString.split("\n"));
            System.out.println(warehouseList);
            return warehouseList;
        }
    }

    public static String getOrderStatus(int orderID) {
        return new Connector().callStoredProcedure("getOrderStatus",  new Object[]{orderID}, true,false);
    }

}
