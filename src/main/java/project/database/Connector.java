package project.database;

import java.sql.*;

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

    public String call(String procedureName, Object[] input, boolean outputFlag) {
            String result;
            try {
                result = executeCall(getCon(), procedureName, input, outputFlag);
                return result;
            } catch (SQLException e) {
                System.out.println(e);
                return "ERROR";
            }
        }


    public static String executeCall(Connection connection, String procedureName,
                                          Object[] input, boolean outputFlag) throws SQLException {
        String result = "No output";

        if(input == null){
            input = new Object[0];
        }

        try (connection) {
            // Przygotowanie wywołania procedury
            StringBuilder call = new StringBuilder("{call " + procedureName + "(");

            for (int i = 0; i < input.length; i++) {
                call.append("?");
                if (i < input.length - 1) {
                    call.append(",");
                }
            }

            call.append(")}");

            CallableStatement callableStatement = connection.prepareCall(call.toString());

            // Ustawienie parametrów wejściowych
            for (int i = 0; i < input.length; i++) {
                callableStatement.setObject(i + 1, input[i]);
            }

            // Wywołanie procedury
            callableStatement.execute();

            // Pobranie wartości parametrów wyjściowych
            try{
            if (outputFlag) {
                result = resultSetToString(callableStatement.getResultSet());
            }}catch(Exception e){
                System.out.println(e);
            }

        }

        return result;
    }

    private static String resultSetToString(ResultSet resultSet) throws SQLException {
        StringBuilder result = new StringBuilder();

        // Pobieranie metadanych kolumn
        int columnCount = resultSet.getMetaData().getColumnCount();

        // Dodawanie nagłówków kolumn do wyniku
        for (int i = 1; i <= columnCount; i++) {
            result.append(resultSet.getMetaData().getColumnName(i));
            if (i < columnCount) {
                result.append("\t");
            }
        }
        result.append("\n");

        // Dodawanie danych do wyniku
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                result.append(resultSet.getString(i));
                if (i < columnCount) {
                    result.append("\t");
                }
            }
            result.append("\n");
        }

        return result.toString();
    }

}
