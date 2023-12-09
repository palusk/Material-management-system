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

    public Connection getCon() {
        return con;
    }

    public Connector() {
        try {
            this.con = DriverManager.getConnection(url,user,pwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
