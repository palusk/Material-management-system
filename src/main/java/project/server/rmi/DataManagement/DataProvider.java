package project.server.rmi.DataManagement;

import project.server.rmi.database.Connector;

public class DataProvider {

    public String getWarehouseProducts(int warehouseID) {
        return new Connector().callStoredProcedure("GetProductInfoInWarehouse", new Object[]{warehouseID}, true, true);
    }
}
