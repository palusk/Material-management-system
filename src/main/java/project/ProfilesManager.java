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
        StringBuilder result = new StringBuilder();

        String callProcedure = "{CALL getProfiles()}";
        try (CallableStatement callableStatement = database.getCon().prepareCall(callProcedure)) {
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    int employee_id = resultSet.getInt("employee_id");
                    int profile_id = resultSet.getInt("profile_id");

                    result.append("Employee ID: ").append(employee_id)
                            .append(", Profile ID: ").append(profile_id)
                            .append("\n");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result.toString();
    }
}
