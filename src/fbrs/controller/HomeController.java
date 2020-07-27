package fbrs.controller;

import fbrs.utils.NavigationUtil;
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
        NavigationUtil.createNewPrimaryStage(NavigationUtil.STORAGE_FXML,
                "المخزن", "/fbrs/photos/Warehouse.png");
    }

    public void onRecycleBin(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(homeVBox, NavigationUtil.RECYCLE_BIN_FXML, actionEvent);
    }

    public void onAllEntries(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(homeVBox, NavigationUtil.ENTRIES_FXML, actionEvent);
    }

    public void onFishermanInputReport(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(homeVBox, NavigationUtil.FISHERMAN_INPUT_REPORT_FXML, actionEvent);
    }

    public void onMarkets(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(homeVBox, NavigationUtil.MARKETS_FXML, actionEvent);
    }

    public void onBackup(ActionEvent actionEvent) {
        //Todo;
    }

    public void onHelp(ActionEvent actionEvent) {
        //Todo;
    }

    public void onInAndOutRecordSeller(ActionEvent actionEvent) throws IOException {
        viewOnInAndOutRecord(InAndOutRecordController.TYPE_SELLER);
    }

    public void onInAndOutRecordFisherman(ActionEvent actionEvent) throws IOException {
        viewOnInAndOutRecord(InAndOutRecordController.TYPE_FISHERMAN);
    }

    private void viewOnInAndOutRecord(int viewType) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(NavigationUtil.IN_AND_OUT_RECORD_FXML));

        Parent root = loader.load();
        homeVBox.getScene().setRoot(root);

        InAndOutRecordController controller = loader.getController();
        controller.setViewType(viewType);
    }
}