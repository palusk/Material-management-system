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

}
