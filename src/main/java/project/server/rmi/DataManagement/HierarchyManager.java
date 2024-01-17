package project.server.rmi.DataManagement;

import project.client.user_interface.tabs.database.Connector;
import java.sql.SQLException;
import java.util.List;

public class HierarchyManager {
    private static Connector database = new Connector();
    static ExcelImporter importer = new ExcelImporter();

    public static String loadEmployeesFromCSV(String csvFile) throws SQLException {
        try {
            AuthenticationLDAP addUserLDAP = new AuthenticationLDAP();

            boolean checkIfUserAddedToLDAP = false;

            List<String> dataList = importer.csvReader(csvFile);
            String error = HierarchyManager.insertDataIntoStaging(dataList);

            if (error.isEmpty()) {
                    for (String line : dataList) {
                        String[] columns = line.split(";");

                        if (columns.length >= 3) {
                            String firstname = columns[0].trim();
                            String lastname = columns[1].trim();
                            String email = columns[2].trim();

                            //System.out.println("Firstname: " + firstname + ", Lastname: " + lastname + ", Email: " + email);
                            if(addUserLDAP.addUser(firstname, lastname, email)){
                                checkIfUserAddedToLDAP = true;
                            }

                        } else {
                            // Error jeśli linia nie ma wystarczającej liczby kolumn
                            System.err.println("Invalid CSV line: " + line);
                            checkIfUserAddedToLDAP = false;
                        }
                    }
                    if(checkIfUserAddedToLDAP){
                        error = HierarchyManager.triggerValidation();
                        System.out.println("Validation of new users have been run successful.");
                    }else System.err.println("Adding users to LDAP server has failed, validation has not been triggered.");
            } else {
                error = "Load for following rows failed: " + error;
            }

            // -- TODO METODA DO SPRAWDZENIA UŻYTKOWNIKOW Z SERWERA LDAP ORAZ TABELĄ EMPLOYEES
            return error;
        } catch (Exception e) {
            return "CSV read failed";
        }
    }




    private static String insertDataIntoStaging(List<String> inputString) {
        return new Connector().insertDataIntoStaging(inputString,"InsertStagingEmployees",5);
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
}
