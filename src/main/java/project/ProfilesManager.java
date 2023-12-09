package project;

import project.database.Connector;

public class ProfilesManager {

    private static Connector database = new Connector();

    public static String updateProfiles() {
        return new Connector().call("updateProfiles", null, true);
    }

    public String getProfiles() {
        return new Connector().call("getProfiles", null, true);
    }
}
