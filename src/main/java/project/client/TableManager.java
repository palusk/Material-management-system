package project.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

public class TableManager {

    private ObservableList<ObservableList<String>> parseData(String tableData) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        String[] rows = tableData.split("\n");

        for (String row : rows) {
            String[] columns = row.split(";");
            ObservableList<String> rowData = FXCollections.observableArrayList(columns);
            data.add(rowData);
        }

        return data;
    }

    void printTable(String tableData, TableView<ObservableList<String>> tableView) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        ObservableList<ObservableList<String>> data = parseData(tableData);

        if (data.isEmpty() || data.get(0).isEmpty()) {
            addNoDataColumn(tableView);
        } else {
            for (int i = 0; i < data.get(0).size(); i++) {
                final int index = i;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(data.get(0).get(i));

                column.setCellValueFactory(cellDataFeatures -> {
                    ObservableList<String> rowValues = cellDataFeatures.getValue();
                    return new SimpleStringProperty(rowValues.get(index));
                });

                tableView.getColumns().add(column);
            }

            data.remove(0);

            tableView.setItems(data);
        }
    }


    private void addNoDataColumn(TableView<ObservableList<String>> tableView) {
        TableColumn<ObservableList<String>, String> column = new TableColumn<>("No Data");
        column.setCellValueFactory(cellDataFeatures -> new SimpleStringProperty("NO DATA"));
        tableView.getColumns().add(column);

        // Ustawienie pustego wiersza, aby tekst "NO DATA" został wyświetlony
        tableView.setItems(FXCollections.emptyObservableList());
    }


}
