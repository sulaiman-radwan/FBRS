package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    public Parent homeVBox;

    @FXML
    public void viewSellers() throws IOException {
        viewUsers(UsersController.TYPE_SELLER);
    }

    @FXML
    public void viewFishermen() throws IOException {
        viewUsers(UsersController.TYPE_FISHERMAN);
    }

    private void viewUsers(int viewType) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(NavigationUtil.USERS_FXML));

        Parent root = loader.load();
        homeVBox.getScene().setRoot(root);

        UsersController controller = loader.getController();
        controller.setViewType(viewType);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onStorage() throws IOException {
        NavigationUtil.createNewPrimaryStage("../view/storage.fxml",
                "المخزن", "fbrs/photos/Warehouse.png");
    }

    public void onRecycleBin(ActionEvent actionEvent) {
        NavigationUtil.navTo(homeVBox, NavigationUtil.RECYCLE_BIN_FXML, actionEvent);
    }

    public void onAllEntries(ActionEvent actionEvent) {
        NavigationUtil.navTo(homeVBox, NavigationUtil.VIEW_ALL_ENTRIES_FXML, actionEvent);
    }

    public void onInAndOutRecord(ActionEvent actionEvent) {
        NavigationUtil.navTo(homeVBox, NavigationUtil.IN_AND_OUT_RECORD_FXML, actionEvent);
    }

    public void onFishermanInputReport(ActionEvent actionEvent) {
        NavigationUtil.navTo(homeVBox, NavigationUtil.FISHERMAN_INPUT_REPORT_FXML, actionEvent);
    }

    public void onMarkets(ActionEvent actionEvent) {
        NavigationUtil.navTo(homeVBox, NavigationUtil.MARKETS_FXML, actionEvent);
    }
}