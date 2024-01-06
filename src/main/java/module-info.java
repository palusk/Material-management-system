module project.materialmanagementsystemjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires com.opencsv;
    requires poi;
    requires poi.ooxml;
    requires java.naming;

    opens project.materialmanagementsystemjavafx to javafx.fxml;
    exports project.materialmanagementsystemjavafx;
}