package project.client.user_interface;

import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import project.client.RemoteManagerImpl;
import project.client.interfaces.DataProviderRemote;
import project.client.interfaces.RemoteManager;
import project.client.user_interface.tabs.OrderTabCreator;
import project.client.user_interface.tabs.PendingOrdersTabCreator;
import project.client.user_interface.tabs.ProductsTabCreator;
import project.client.user_interface.tabs.ProfileTabCreator;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserPage {

    private WindowManager windowManager;

    public UserPage(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public Scene getScene() {
        try {
            RemoteManager remoteManager = new RemoteManagerImpl();
            DataProviderRemote dataProvider = remoteManager.getDataProvider();

            BorderPane pane = new BorderPane();

            TabPane tabPane = new TabPane();
            tabPane.getTabs().addAll(
                    OrderTabCreator.create(remoteManager, dataProvider),
                    ProductsTabCreator.create(remoteManager, dataProvider),
                    ProfileTabCreator.create(windowManager),
                    PendingOrdersTabCreator.create(remoteManager, dataProvider)
            );

            pane.setCenter(tabPane);

            return new Scene(pane, 400, 300);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}