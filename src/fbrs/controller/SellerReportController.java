package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class SellerReportController implements Initializable {
    public TableView table;
    public TableColumn fromColumn;
    public TableColumn toColumn;
    public TableColumn dateCreatedColumn;
    public TableColumn dateUpdatedColumn;
    public TableColumn typeColumn;
    public TableColumn quantityColumn1;
    public TableColumn note;
    public Text name;
    public Text account;
    public Button specialCasesBtn;
    public Button editFishermanBtn;
    public ComboBox All;
    public ComboBox date;

    public void back(ActionEvent actionEvent) {
    }

    public void editFisherman(ActionEvent actionEvent) {
    }

    public void specialCases(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
