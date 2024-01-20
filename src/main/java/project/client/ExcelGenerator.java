package project.client;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelGenerator {

    public void generateExcelFile(String sheetName, String[] headers, Stage primaryStage) {

        if (sheetName.equals("Employee Data")) {
            List<Object[]> employeeData = new ArrayList<>();
            employeeData.add(new Object[]{"Jan", "Kowalski", "jan.kowalski@example.com", "Manager", 101, 1});
            generateCsvFile(sheetName, headers, employeeData, primaryStage);
        } else {
            List<Object[]> employeeData = new ArrayList<>();
            employeeData.add(new Object[]{"1", "100", "1", "2025-01-01"});
            generateCsvFile(sheetName, headers, employeeData, primaryStage);
        }
    }

    public void generateCsvFile(String fileName, String[] headers, List<Object[]> data, Stage primaryStage) {
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(fileName.toLowerCase().replace(" ", "_") + ".csv");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                saveCsvFile(file, headers, data);
                System.out.println("CSV file generated successfully!");
            }
        });
    }

    private void saveCsvFile(File file, String[] headers, List<Object[]> data) {
        try (FileWriter writer = new FileWriter(file)) {
            // Dodaj nagłówki do pliku CSV
            for (int i = 0; i < headers.length; i++) {
                writer.append(headers[i]);
                if (i < headers.length - 1) {
                    writer.append(";");
                }
            }
            writer.append("\n");

            // Dodaj dane do pliku CSV
            for (Object[] rowData : data) {
                for (int i = 0; i < rowData.length; i++) {
                    writer.append(String.valueOf(rowData[i]));
                    if (i < rowData.length - 1) {
                        writer.append(";");
                    }
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}