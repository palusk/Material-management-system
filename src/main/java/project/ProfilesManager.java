package project;

import project.database.Connector;

public class ProfilesManager {

    private static Connector database = new Connector();

    public static String updateProfiles() {
        return new Connector().callStoredProcedure("updateProfiles", null, false);
    }

    public String getProfiles() {
        return new Connector().callStoredProcedure("getProfiles", null, true);
    }
}
