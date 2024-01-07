package project.server.rmi.DataManagement;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImporter {

    public ExcelImporter() {}

    public List<String> csvReader(String csvFile) {
        List<String> dataList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                String joinedLine = String.join(";", line);
                dataList.add(joinedLine);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return dataList;
    }

}
