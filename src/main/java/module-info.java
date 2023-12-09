module project.materialmanagementsystemjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires com.opencsv;

    opens project.materialmanagementsystemjavafx to javafx.fxml;
    exports project.materialmanagementsystemjavafx;
}