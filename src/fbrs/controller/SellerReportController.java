package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class SellerReportController {
    public TableView table;
    public TableColumn fromColumn;
    public TableColumn toColumn;
    public TableColumn dateCreateColumn;
    public TableColumn dateUpdateColumn;
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
}
