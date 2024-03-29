package project.client.user_interface;

import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import project.client.RemoteManagerImpl;
import project.client.user_interface.tabs.EmployeeManagementTabCreator;
import project.client.user_interface.tabs.ProductManagementTabCreator;
import project.client.user_interface.tabs.ProductsEditorTabCreator;
import project.client.user_interface.tabs.ProfileTabCreator;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class AdminPage {

    private WindowManager windowManager;
    private EmployeeManagementTabCreator employeeManagementTabCreator;
    private ProductManagementTabCreator productManagementTabCreator;
    private ProfileTabCreator profileTabCreator;
    private ProductsEditorTabCreator productsEditorTabCreator;

    public AdminPage(WindowManager windowManager) throws RemoteException, NotBoundException {
        this.windowManager = windowManager;

        this.profileTabCreator = new ProfileTabCreator();

        this.employeeManagementTabCreator = new EmployeeManagementTabCreator(
                new FileChooser(), windowManager, new RemoteManagerImpl(), new RemoteManagerImpl().getHierarchyManager(), new RemoteManagerImpl().getProfilesManager());
        this.productManagementTabCreator = new ProductManagementTabCreator(
                new FileChooser(), windowManager, new RemoteManagerImpl(), new RemoteManagerImpl().getProductsLoader());
        this.productsEditorTabCreator = new ProductsEditorTabCreator();
    }

    private BorderPane createAdminPane() {
        BorderPane pane = new BorderPane();
        return pane;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                employeeManagementTabCreator.create(),
                productManagementTabCreator.create(),
                profileTabCreator.create(windowManager),
                productsEditorTabCreator.create()
        );
        return tabPane;
    }

    public Scene getScene() {
        BorderPane pane = createAdminPane();
        pane.setCenter(createTabPane());
        return new Scene(pane, 400, 300);
    }
}