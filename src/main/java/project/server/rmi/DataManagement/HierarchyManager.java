package project.server.rmi.DataManagement;

import project.client.user_interface.tabs.database.Connector;

import java.util.Arrays;
import java.util.List;

public class HierarchyManager {
    private static Connector database = new Connector();
    static ExcelImporter importer = new ExcelImporter();

    public static String loadEmployeesFromCSV(String csvFile){
        try {
            AuthenticationLDAP objectLDAP = new AuthenticationLDAP();

            boolean checkIfUserAddedToLDAP = false;

            List<String> dataList = importer.csvReader(csvFile);
            String error = HierarchyManager.insertDataIntoStaging(dataList);

            if (error.isEmpty()) {
                    for (String line : dataList) {
                        String[] columns = line.split(";");

                        if (columns.length >= 4) {
                            String firstname = columns[0].trim();
                            String lastname = columns[1].trim();
                            String email = columns[2].trim();
                            String employeeType = columns[3].trim();

                            //System.out.println("Firstname: " + firstname + ", Lastname: " + lastname + ", Email: " + email);
                            try {
                                if (objectLDAP.addUser(firstname, lastname, email, employeeType)) {
                                    checkIfUserAddedToLDAP = true;
                                }
                            }catch (Exception e) {
                                    System.out.println(e.getMessage());
                                    checkIfUserAddedToLDAP = false;
                            }

                        } else {
                            // Error jeśli linia nie ma wystarczającej liczby kolumn
                            System.err.println("Invalid CSV line: " + line);
                            checkIfUserAddedToLDAP = false;
                        }
                    }
                    if(checkIfUserAddedToLDAP){
                        error = HierarchyManager.triggerValidation();
                        System.out.println("Validation of new users have been run.");
                        compareLdapAndDatabase(objectLDAP);
                    }else System.err.println("Adding users to LDAP server has failed, validation has not been triggered.");
            } else {
                error = "Load for following rows failed: " + error;
            }

            return error;
        } catch (Exception e) {
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



