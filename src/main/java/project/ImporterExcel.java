package project;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import project.database.Connector;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImporterExcel {

    public ImporterExcel() {}

    Connector database = new Connector();

    // załadowanie CSV z komputera oraz sformatowanie CSV na arrayliste, zwraca tę listę
    public List<String[]> csvReader(String csvFile) {
        List<String[]> dataList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] line;

            while ((line = reader.readNext()) != null) {
                dataList.add(line);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return dataList;
        }

    //wyslanie do tabeli staging oraz uruchomienie procedury walidacji
    public void insertToStaging(String csvFile) {
        if(database.importCSV(csvFile)) {
            System.out.println("CSV file has been properly imported to the 'staging' table!");
            triggerValidation();
        }else System.out.println("CSV file import has failed!");
    }


    //strigerowanie procedury walidacji w bazie danych (0 jesli dobrze, numer wiersza jesli zle)
    protected int triggerValidation() {
        database.validation();
        return 0;
    }

    public void promptResult(){System.out.println("");}
}
