package project.server.rmi.DataManagement;

import project.server.rmi.database.Connector;

public class ProductsManager {

    public static void insertStagingOrder(String orderDetails) {
        System.out.println(orderDetails);
        new Connector().callStoredProcedure("insertStagingOrder", new Object[]{orderDetails}, false);
    }

}
