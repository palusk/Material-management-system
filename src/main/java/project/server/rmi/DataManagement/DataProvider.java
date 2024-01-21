package project.server.rmi.DataManagement;

import project.client.user_interface.tabs.database.Connector;

public class DataProvider {

    public String getWarehouseProducts(int warehouseID) {
        return new Connector().callStoredProcedure("GetProductInfoInWarehouse", new Object[]{warehouseID}, true, true);
    }

    public String getAllProducts() {
        return new Connector().callStoredProcedure("getAllProducts", null, true, true);
    }

    public String getOrderDetails(String orderID) {
        return new Connector().callStoredProcedure("getOrderDetails", new Object[]{orderID}, true, true);
    }

    public String getAllWarehouses() {
        return new Connector().callStoredProcedure("getAllWarehouses", null, true, true);
    }

    public String getTransferHistory() {
        return new Connector().callStoredProcedure("getTransferHistory", null, true, true);
    }

    public String getAllEmployees() {
        return new Connector().callStoredProcedure("getAllEmployeesAdmin", null, true, true);
    }

    public String getAllProductsInStock() {
        return new Connector().callStoredProcedure("getAllProductsInStock", null, true, true);
    }

    public String getAllOrders() {
        return new Connector().callStoredProcedure("getAllOrders", null, true, true);
    }

    public String getAllProductsAdmin() {
        return new Connector().callStoredProcedure("getAllProductsAdmin", null, true, true);
    }


    public void delete(String ID, String table, String frstValue, String secondValue){
        new Connector().callStoredProcedure("execDelete", new Object[]{ID,table,frstValue,secondValue}, true, true);
    }

    public void edit(String ID, String table, String frstValue, String secondValue){
        new Connector().callStoredProcedure("execEdit", new Object[]{ID,table,frstValue,secondValue,""}, true, true);
    }

    public void edit2(String ID, String table, String frstValue, String secondValue, String thirdValue){
        new Connector().callStoredProcedure("execEdit", new Object[]{ID,table,frstValue,secondValue, thirdValue}, true, true);
    }

    public void add(String ID, String table, String frstValue, String secondValue){
        new Connector().callStoredProcedure("execAdd", new Object[]{ID,table,frstValue,secondValue}, true, true);
    }
    public String getAllWarehousesAdmin() {
        return new Connector().callStoredProcedure("getAllWarehousesAdmin", null, true, true);
    }
}
