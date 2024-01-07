package project.client;

import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelGenerator {

    public void generateExcelFile(String sheetName, String[] headers) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // Dodaj nagłówki
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        Object[][] data;

        if (sheetName.equals("Employee Data")) {
            data = new Object[][]{
                    {"Jan", "Kowalski", "jan.kowalski@example.com", "Manager", 101},
                    {"Adam", "Nowak", "adam.nowak@example.com", "Senior Worker", 102}
            };
        } else {
            data = new Object[][]{
                    {"100", "10", "1", "31.12.2025"},
                    {"200", "100", "2", "31.12.2026"}
            };
        }

        int rowNum = 1;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.length; i++) {
                Cell cell = row.createCell(i);
                if (rowData[i] instanceof String) {
                    cell.setCellValue((String) rowData[i]);
                } else if (rowData[i] instanceof Integer) {
                    cell.setCellValue((Integer) rowData[i]);
                }
            }
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(sheetName.toLowerCase().replace(" ", "_") + ".csv");
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                    System.out.println("Excel file generated successfully!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}