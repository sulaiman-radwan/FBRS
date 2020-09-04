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
        viewUsers(UsersController.SELLER_TYPE);
    }

    @FXML
    public void viewFishermen() throws IOException {
        viewUsers(UsersController.FISHERMAN_TYPE);
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

    public void onStorage(ActionEvent actionEvent) throws IOException {
        NavigationUtil.navigateTo(homeVBox, NavigationUtil.STORAGE_FXML);
    }

    public void onRecycleBin(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(homeVBox, NavigationUtil.RECYCLE_BIN_FXML);
    }

    public void onAllEntries() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(NavigationUtil.ENTRIES_FXML));

        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        homeVBox.getScene().setRoot(root);

        EntriesController controller = loader.getController();
        controller.setViewType(null);
    }

    public void onFishermanInputReport(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(homeVBox, NavigationUtil.FISHERMAN_INPUT_REPORT_FXML);
    }

    public void onMarkets(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(homeVBox, NavigationUtil.MARKETS_FXML);
    }

    public void onBackup() throws IOException {
        NavigationUtil.createNewPrimaryStage(NavigationUtil.BACKUP_FXML,
                "النسخ الاحتياطي", "/fbrs/photos/restore.png");
    }

    public void onHelp() {
        //Todo;
        NavigationUtil.navigateTo(homeVBox, NavigationUtil.FAQ_FXML);
    }

    public void onInAndOutRecordSeller() throws IOException {
        viewOnInAndOutRecord(InAndOutRecordController.TYPE_SELLER);
    }

    public void onInAndOutRecordFisherman() throws IOException {
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