package project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Connector {

    private static final String url = "jdbc:mariadb://localhost:3306/material_management_system";
    private static final String user = "root";
    private static final String pwd = "password";
    Connection con;

    public Connector() {
        try {
            this.con = DriverManager.getConnection(url,user,pwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean importCSV(String csvFile){

        String sqlStatement = "LOAD DATA LOCAL INFILE ? INTO TABLE staging " +
                "FIELDS TERMINATED BY ';' " +
                "LINES TERMINATED BY '\r\n' " +
                "IGNORE 1 LINES " +
                "(order_id, status_id, order_date, warehouse_name, quantity, product_name)";

        try (PreparedStatement preparedStatement = con.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, csvFile);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    }


    public void validation(){}

}
