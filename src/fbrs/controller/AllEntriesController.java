package fbrs.controller;

import fbrs.utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AllEntriesController implements Initializable {
    public TableView table;
    public TableColumn fromColumn;
    public TableColumn dateCreatedColumn;
    public TableColumn dateUpdatedColumn;
    public TableColumn toColumn;
    public TableColumn typeColumn;
    public TableColumn quantityColumn1;
    public TableColumn note;
    public Button backBtn;
    public BorderPane rootPane;

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
    }

    public void fromComboBox(ActionEvent actionEvent) {
    }

    public void toComboBox(ActionEvent actionEvent) {
    }

    public void dateUptateComboBox(ActionEvent actionEvent) {
    }

    public void typeComboBox(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
