package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class InAndOutRecordController implements Initializable {
    public TableView table;
    public TableColumn numberColumn;
    public TableColumn idColumn;
    public TableColumn nameColumn;
    public TableColumn noteColumn;
    public Button backBtn;
    public TextField number;
    public TextField user;
    public TextField note;
    public Button addBtn;
    public BorderPane rootPane;

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
    }

    public void onAdd(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
