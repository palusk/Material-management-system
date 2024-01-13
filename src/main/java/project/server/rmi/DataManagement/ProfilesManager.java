package project.server.rmi.DataManagement;

import project.server.rmi.database.Connector;
import java.util.Arrays;
import java.util.List;

public class ProfilesManager {

    public static String updateProfiles() {
        return new Connector().callStoredProcedure("updateProfiles", null, false);
    }

    public String getProfiles() {
        return new Connector().callStoredProcedure("getProfiles", null, true);
    }

    public static String getUserWarehouse(int userID) {
        return new Connector().callStoredProcedure("getUserWarehouse", new Object[]{userID}, true,false);
    }

    public static String getUserProfile(int userID) {
        return new Connector().callStoredProcedure("getUserProfile", new Object[]{userID}, true, false);
    }

    public String getAllWarehouses() {
        return new Connector().callStoredProcedure("getAllWarehouses", null, true, false);
    }

    public List<String> getWarehouseDropdown(int userID){

        int profileID = Integer.parseInt(getUserProfile(userID));
        System.out.println(getUserProfile(userID));
        System.out.println(profileID);
            if(profileID == 0){
                String warehouseString = getAllWarehouses();
                List<String> warehouseList = Arrays.asList(warehouseString.split("\n"));
                System.out.println(warehouseList);
                return warehouseList;
            }else {
                String warehouseString = getUserWarehouse(userID);
                List<String> warehouseList = Arrays.asList(warehouseString.split("\n"));
                System.out.println(warehouseList);
                return warehouseList;
            }
    }
}
