package project;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {


        ProductsLoader productsLoader = new ProductsLoader();
        String csvFile = "C:\\Users\\mateu\\IdeaProjects\\Material-management-system\\src\\main\\java\\project\\csvFile\\delivery.csv";

        System.out.println(productsLoader.loadProductsFromCSV(csvFile));
        System.out.println(productsLoader.getStagingErrors());
    }
}