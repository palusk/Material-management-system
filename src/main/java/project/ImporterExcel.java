package project;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImporterExcel {

    public ImporterExcel() {}

    public List<String[]> csvReader(String csvFile){
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





    public String loadCSV() {return new String();}    // załadowanie CSV z komputera

   // public ArrayList formatCSV(String raw_CSV){}      // sformatowanie CSV, czyli z Stringa na Liste

    public boolean insertToStaging() {return true;}   //wyslanie do tabeli staging oraz uruchomienie procedury walidacji

    public int triggerValidation() {return 0;}               //strigerowanie procedury walidacji w bazie danych (0 jesli dobrze, numer wiersza jesli zle)

    public void promptResult(){System.out.println("");}
}
