package project.server.rmi.DataManagement;

import project.client.user_interface.tabs.database.Connector;
import java.util.Arrays;
import java.util.List;

public class ProfilesManager {

    public static String updateProfiles() {
        String output;
        String dataForLDAP = getDataForLDAP();
        System.out.println(dataForLDAP);
        AuthenticationLDAP ldap = new AuthenticationLDAP();
        output = new Connector().callStoredProcedure("updateProfiles", null, false);
        String[] rows = dataForLDAP.split("\n");
        String cn, sn, mail, employeeType;
        // Iterujemy po każdym wierszu
        for (String row : rows) {
            // Dzielimy wiersz na pola używając znaku średnika jako separatora
            String[] fields = row.split(";");

                cn = fields[0];
                sn = fields[1];
                mail = fields[2];
                employeeType = fields[3];
            System.out.println(cn);
            System.out.println(sn);
            System.out.println(mail);
            System.out.println(employeeType);
                ldap.addUser(cn,sn,mail,employeeType);
            }

        return output;
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

    public static String getDataForLDAP(){
        return new Connector().callStoredProcedure("getDataForLDAP", null, true, false);
    }
}
