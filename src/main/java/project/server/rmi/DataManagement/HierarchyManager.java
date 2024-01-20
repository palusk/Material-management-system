package project.server.rmi.DataManagement;

import project.client.user_interface.tabs.database.Connector;

import java.util.Arrays;
import java.util.List;

public class HierarchyManager {
    private static Connector database = new Connector();
    static ExcelImporter importer = new ExcelImporter();

    public static String loadEmployeesFromCSV(String csvFile) {
        try {
            List<String> dataList = importer.csvReader(csvFile);
            String error = HierarchyManager.insertDataIntoStaging(dataList);
            if (error.isEmpty()) {
                error = HierarchyManager.triggerValidation();
            } else {
                error = "Load for following rows failed: " + error;
            }
            return error;
        } catch (Exception e) {
            // Log the exception using a logging framework
            return "CSV read failed";
        }
    }



    private static String insertDataIntoStaging(List<String> inputString) {
        return new Connector().insertDataIntoStaging(inputString,"InsertStagingEmployees",6);
    }

    public static String triggerValidation() {
        return new Connector().callStoredProcedure("ProcessPendingRowsInEmployees", null, false);
    }

    public String getStagingErrors() {
        return new Connector().callStoredProcedure("GetEmployeesStagingErrors", null, true);
    }

    public static String clearStaging() {
        return new Connector().callStoredProcedure("DeleteAllEmployeesStagingRows", null, false);
    }

    public String getStagingTable() {
        return new Connector().callStoredProcedure("GetEmployeesStagingTable", null, true);
    }

    public static String refreshHierarchy() {
        return new Connector().callStoredProcedure("CalculateEmployeeHierarchy", null, false);
    }

    public String getHierarchy() {
        return new Connector().callStoredProcedure("getHierarchy", null, true);
    }


    public static String getAllEmployees() {
        return new Connector().callStoredProcedure("getAllEmployees", null, true);
    }

    public static List<String> getAllEmployeesList(){
        String employeesString = getAllEmployees();
        List<String> emails = Arrays.asList(employeesString.split("\n"));
        System.out.println(emails);
        return emails;
    }


    public static void compareLdapAndDatabase(AuthenticationLDAP ldap){
        List<String> ldapEmployees = ldap.getAllUsers();
        List<String> databaseEmployees = getAllEmployeesList();
        boolean check = false;
        for (String element : ldapEmployees) {
            if (databaseEmployees.contains(element)) {
                check = true;
            }else {
                ldap.deleteUser(element);
                //TODO dodac usunioecie z bazy duplikatow
            }
        }
        if(check) {System.out.println("New users have been added to the LDAP server successful!");}
    }


//    public static void main(String[] args) {
//
//        List<String> emails = getAllEmployeesList();
//
//        System.out.println("Emails:");
//        for (String email : emails) {
//            System.out.println(email);
//        }
//    }


}



