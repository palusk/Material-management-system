package project;

import java.util.ArrayList;

public class ImporterExcela {

    private static String excel_File;

    public ImporterExcela(String excel_File){
        this.excel_File = excel_File;
    }

    public String loadCSV() {return new String();}    // załadowanie CSV z komputera

    public ArrayList formatCSV(String raw_CSV){}      // sformatowanie CSV, czyli z Stringa na Liste

    public boolean insertToStaging() {return true;}   //wyslanie do tabeli staging oraz uruchomienie procedury walidacji

    public int triggerValidation() {return 0;}               //strigerowanie procedury walidacji w bazie danych (0 jesli dobrze, numer wiersza jesli zle)

    public void promptResult(){System.out.println("");}
}
