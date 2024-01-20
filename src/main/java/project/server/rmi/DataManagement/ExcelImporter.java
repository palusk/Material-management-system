package project.server.rmi.DataManagement;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImporter {

    public List<String> csvReader(String csvFile, int validationType) {
        List<String> dataList = new ArrayList<>();
        List<String> errorList = new ArrayList<>(); // Error list for collecting error messages

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] header = reader.readNext(); // Read header to get column names

            if (validationType == 1 && validateHeaderForType1(header)) {
                validateDataForType1(reader, dataList, errorList);
            } else if (validationType == 2 && validateHeaderForType2(header)) {
                validateDataForType2(reader, dataList, errorList);
            }

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace(); // Handle exception appropriately
            // Add an error message to the error list
            errorList.add("Error");
        }

        if (!errorList.isEmpty()) {
            // If there are errors, return the error list
            System.out.println(errorList);
            return errorList;
        }

        System.out.println(dataList);
        return dataList;
    }

    private boolean validateHeaderForType1(String[] header) {
        return header != null
                && header[0].equalsIgnoreCase("product_id;quantity;warehouse_id;expiration_date");
    }

    private void validateDataForType1(CSVReader reader, List<String> dataList, List<String> errorList)
            throws IOException, CsvValidationException {
        String[] line;
        while ((line = reader.readNext()) != null ) {
            if( !line[0].equals(";;;")){
            line = line[0].split(";");

            if (line.length >= 4 && isInteger(line[0]) && isInteger(line[1])
                    && isInteger(line[2]) && isDate(line[3])) {
                String joinedLine = String.join(";", line);
                dataList.add(joinedLine);
            } else {
                // Add an error message to the error list
                errorList.add("Error");
            }}
        }
    }

    private boolean validateHeaderForType2(String[] header) {
        return header != null && header[0].equalsIgnoreCase("firstname;lastname;email;position;warehouse_id;reports_to");
    }

    private void validateDataForType2(CSVReader reader, List<String> dataList, List<String> errorList)
            throws IOException, CsvValidationException {
        String[] line;
        while ((line = reader.readNext()) != null) {

            if( !line[0].equals(";;;;;")){
                line = line[0].split(";");
            if (line.length >= 6
                    && isNonEmptyString(line[0])
                    && isNonEmptyString(line[1])
                    && isNonEmptyString(line[2])
                    && isNonEmptyString(line[3])
                    && isInteger(line[4])
                    && isInteger(line[5])) {
                String joinedLine = String.join(";", line);
                dataList.add(joinedLine);
            } else {
                // Add an error message to the error list
                errorList.add("Error");
            }
            }
        }
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDate(String value) {
        // Check if the date has exactly 10 characters
        return value.length() == 10;
    }

    private boolean isNonEmptyString(String value) {
        return !value.isEmpty();
    }
}