package project;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        String csvFile = "D:/Program Files/IdeaProjects/Material-management-system/src/main/java/project/csvFile/delivery.csv";

        ImporterExcel object = new ImporterExcel();
        List<String[]> dataList = object.csvReader(csvFile);

        for (String[] row : dataList) {
            for (String value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }

    }
}