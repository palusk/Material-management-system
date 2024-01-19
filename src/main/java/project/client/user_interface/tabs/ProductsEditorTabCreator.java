package project.client.user_interface.tabs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Tab;
import project.client.RemoteManagerImpl;
import project.client.TableManager;
import project.client.interfaces.DataProviderRemote;
import project.client.interfaces.RemoteManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ProductsEditorTabCreator{


        public Tab create() {
            BorderPane root = new BorderPane();

            RemoteManager remoteManager = null;
            DataProviderRemote dataProvider = null;
            TableView<ObservableList<String>> tableView = new TableView<>();
            try {
                remoteManager = new RemoteManagerImpl();
                dataProvider = remoteManager.getDataProvider();

                TableManager tableManager = new TableManager();
                tableManager.printTable(dataProvider.getAllProducts(), tableView);
            }catch (Exception e){};
            // Utwórz TableView


          // // Utwórz kolumny
          // TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
          // firstNameCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());

          // TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
          // lastNameCol.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

          // TableColumn<Person, Number> ageCol = new TableColumn<>("Age");
          // ageCol.setCellValueFactory(cellData -> cellData.getValue().ageProperty());

            // Utwórz pola tekstowe do wprowadzania danych
            TextField firstNameField = new TextField();
            firstNameField.setPromptText("Product ID");
            TextField lastNameField = new TextField();
            lastNameField.setPromptText("Product Name");
            TextField ageField = new TextField();
            ageField.setPromptText("Nothing");

            // Utwórz przyciski do dodawania, edycji i usuwania
            Button addButton = new Button("Add");
       //     addButton.setOnAction(e -> addPerson());

            Button editButton = new Button("Edit");
        //    editButton.setOnAction(e -> editPerson());

            Button deleteButton = new Button("Delete");
        //    deleteButton.setOnAction(e -> deletePerson());

            // Dodaj komponenty do interfejsu
            root.setCenter(tableView);

            BorderPane formPane = new BorderPane();
            formPane.setPadding(new Insets(10));
            formPane.setLeft(new VBox(5, new Label("Product ID:"), new Label("Product Name:"), new Label("Nothing:")));
            formPane.setCenter(new VBox(5, firstNameField, lastNameField, ageField));
            formPane.setRight(new VBox(5, addButton, editButton, deleteButton));

            root.setBottom(formPane);

            Tab databaseTab = new Tab("Database");
            databaseTab.setContent(root);

            return databaseTab;
        }

    //    private void addPerson() {
    //        String firstName = firstNameField.getText();
    //        String lastName = lastNameField.getText();
    //        int age = Integer.parseInt(ageField.getText());
//
    //        Person newPerson = new Person(firstName, lastName, age);
    //        data.add(newPerson);
//
    //        clearFields();
    //    }
//
    //    private void editPerson() {
    //        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
//
    //        if (selectedPerson != null) {
    //            selectedPerson.setFirstName(firstNameField.getText());
    //            selectedPerson.setLastName(lastNameField.getText());
    //            selectedPerson.setAge(Integer.parseInt(ageField.getText()));
    //            tableView.refresh();
//
    //            clearFields();
    //        }
    //    }
//
    //    private void deletePerson() {
    //        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
//
    //        if (selectedPerson != null) {
    //            data.remove(selectedPerson);
    //            clearFields();
    //        }
    //    }
//
    //    private void clearFields() {
    //        firstNameField.clear();
    //        lastNameField.clear();
    //        ageField.clear();
    //    }

        public static class Person {
            private final javafx.beans.property.SimpleStringProperty firstName;
            private final javafx.beans.property.SimpleStringProperty lastName;
            private final javafx.beans.property.SimpleIntegerProperty age;

            public Person(String firstName, String lastName, int age) {
                this.firstName = new javafx.beans.property.SimpleStringProperty(firstName);
                this.lastName = new javafx.beans.property.SimpleStringProperty(lastName);
                this.age = new javafx.beans.property.SimpleIntegerProperty(age);
            }

            public String getFirstName() {
                return firstName.get();
            }

            public javafx.beans.property.SimpleStringProperty firstNameProperty() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName.set(firstName);
            }

            public String getLastName() {
                return lastName.get();
            }

            public javafx.beans.property.SimpleStringProperty lastNameProperty() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName.set(lastName);
            }

            public int getAge() {
                return age.get();
            }

            public javafx.beans.property.SimpleIntegerProperty ageProperty() {
                return age;
            }

            public void setAge(int age) {
                this.age.set(age);
            }
        }
}
