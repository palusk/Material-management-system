package project.client.user_interface.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import project.client.TableManager;
import project.client.interfaces.DataProviderRemote;
import project.client.interfaces.ProfilesManagerRemote;
import project.client.interfaces.RemoteManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class ProductsTabCreator {

    public static Tab create(RemoteManager remoteManager, DataProviderRemote dataProvider) throws NotBoundException, RemoteException {
        Tab tab = new Tab("Products in stock");
        TableManager tableManager = new TableManager();
        ProfilesManagerRemote profilesManager = remoteManager.getProfilesManager();
        TableView<ObservableList<String>> tableView = new TableView<>();

        ComboBox<String> dropdown = createDropdown(profilesManager.getWarehouseDropdown(2), dataProvider, tableView, tableManager);

        VBox vbox = new VBox(dropdown, tableView);
        vbox.setPadding(new Insets(10));

        // Ustawiamy zawartość zakładki
        tab.setContent(vbox);

        return tab;
    }

    private static ComboBox<String> createDropdown(List<String> warehouseDropdown, DataProviderRemote dataProvider, TableView<ObservableList<String>> tableView, TableManager tableManager) {
        ComboBox<String> dropdown = new ComboBox<>();

        dropdown.setItems(FXCollections.observableArrayList(warehouseDropdown));
        dropdown.setValue("Select an option");

        dropdown.setOnAction(event -> {
            try {
                String selectedValue = dropdown.getValue();
                int index = selectedValue.indexOf(" ");
                String warehouseID = selectedValue.substring(0, index);

                tableManager.printTable(dataProvider.getWarehouseProducts(Integer.parseInt(warehouseID)), tableView);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        return dropdown;
    }
}