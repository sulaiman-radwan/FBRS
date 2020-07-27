package fbrs.controller;

import fbrs.utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class RecycleBinController implements Initializable {
    public TableView table;
    public TableColumn selectColumn;
    public TableColumn idColumn;
    public TableColumn nameColumn;
    public TableColumn phoneColumn;
    public TableColumn balanceColumn;
    public TextField searchField;
    public Button restoreBtn;
    public Button FinalDeletionBtn;
    public Button EmptyRecycleBinBtn;
    public BorderPane rootPane;

    public void restore(ActionEvent actionEvent) {
    }

    public void FinalDeletion(ActionEvent actionEvent) {
    }

    public void EmptyRecycleBin(ActionEvent actionEvent) {
    }

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
