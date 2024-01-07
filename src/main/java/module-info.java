module project.materialmanagementsystemjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires com.opencsv;
    requires poi;
    requires poi.ooxml;
    requires java.naming;
    requires java.rmi;

    opens project.materialmanagementsystemjavafx to javafx.fxml;
    exports project.client;
    opens project.client to javafx.fxml;
    exports project.client.interfaces;
    opens project.client.interfaces to javafx.fxml;
    exports project.server.rmi.DataManagement;
    opens project.server.rmi.DataManagement to javafx.fxml;
}