package project.server.rmi.database;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

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

    public String callStoredProcedure(String procedureName, Object[] input, boolean outputFlag) {
            try {
                String result = executeCall(getCon(), procedureName, input, outputFlag);
                if(outputFlag == true)
                return result;
                else return "No output";
            } catch (SQLException e) {
                return "An error has occurred please contact your administrator";
            }
        }

    public String callStoredProcedure(String procedureName, Object[] input, boolean outputFlag, boolean withNames) {
        try {
            String result = executeCall(getCon(), procedureName, input, outputFlag);
            if(outputFlag == true) {
                if (!withNames){
                    String[] lines = result.split("\n");
                    result = String.join("\n", Arrays.copyOfRange(lines, 1, lines.length));
                }
                return result;
            }
            else return "No output";
        } catch (SQLException e) {
            return "An error has occurred please contact your administrator";
        }
    }

    public static String executeCall(Connection connection, String procedureName, Object[] input, boolean outputFlag) throws SQLException {
        String result = "No output";

        if (input == null) {
            input = new Object[0];
        }

        try (connection) {
            String call = buildCallString(procedureName, input.length);

            try (CallableStatement callableStatement = connection.prepareCall(call)) {
                setParameters(callableStatement, input);

                callableStatement.execute();

                if (outputFlag) {
                    result = resultSetToString(callableStatement.getResultSet());
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
            return "An error has occurred. Please contact your administrator";
        }

        return result;
    }

    private static String buildCallString(String procedureName, int inputLength) {
        StringBuilder call = new StringBuilder("{call " + procedureName + "(");

        for (int i = 0; i < inputLength; i++) {
            call.append("?");
            if (i < inputLength - 1) {
                call.append(",");
            }
        }

        return call.append(")}").toString();
    }

    private static void setParameters(CallableStatement callableStatement, Object[] input) throws SQLException {
        for (int i = 0; i < input.length; i++) {
            callableStatement.setObject(i + 1, input[i]);
        }
    }

    private static String resultSetToString(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= columnCount; i++) {
            result.append(metaData.getColumnName(i));
            if (i < columnCount) {
                result.append(";");
            }
        }
        result.append("\n");

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                result.append(resultSet.getString(i));
                if (i < columnCount) {
                    result.append(";");
                }
            }
            result.append("\n");
        }

        return result.toString();
    }

    public String insertDataIntoStaging(List<String> inputString, String procedureName, int columnsNumber) {
        System.out.println(inputString);

        StringBuilder failedRows = new StringBuilder();
        Integer rowNumber = 0;

        for (String line : inputString) {
            if (!line.trim().isEmpty()) {
                String[] columns = line.split(";");
                System.out.println(columnsNumber);
                System.out.println(columns.length);
                if (columns.length == columnsNumber) {
                    try {
                        callStoredProcedure(procedureName, columns, false);
                    } catch (Exception e) {
                        failedRows.append("Row ").append(rowNumber).append(": ").append(e.getMessage()).append(", ");
                    }
                } else {
                    failedRows.append("Row ").append(rowNumber).append(": Incorrect number of columns, ");
                }
            } else {
                failedRows.append("Row ").append(rowNumber).append(": Empty row, ");
            }
            rowNumber++;
        }

        return failedRows.toString();
    }
}
