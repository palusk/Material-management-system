package project;

import project.database.Connector;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilesManager {

    private static Connector database = new Connector();

    public static String updateProfiles() {
        String storedProcedureCall = "{call updateProfiles()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(storedProcedureCall)) {
            callableStatement.execute();
            return "Profiles refreshed";
        } catch (SQLException e) {
            return "Refresh failed";
        }
    }

    public String getProfiles() {
        return new Connector().call("getProfiles", null, true);
    }
}
