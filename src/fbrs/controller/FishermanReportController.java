package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FishermanReportController implements Initializable {
    public BorderPane rootPane;
    public TableColumn fromColumn;
    public TableColumn toColumn;
    public TableColumn dateCreatedColumn;
    public TableColumn dateUpdatedColumn;
    public TableColumn typeColumn;
    public TableColumn quantityColumn1;
    public TableColumn note;
    public TableView table;
    public Text name;
    public Text account;
    public ComboBox All;
    public ComboBox date;
    public Button specialCasesBtn;
    public Button editFishermanBtn;

    public void back(ActionEvent actionEvent) {
    }

    public void specialCases(ActionEvent actionEvent) throws IOException {
        NavigationUtil.createNewPrimaryStage("../view/specialCases.fxml",
                "حالات خاصة للبُكس", "fbrs/photos/App_icon.png");
    }

    public void editFisherman(ActionEvent actionEvent) throws IOException {
        NavigationUtil.createNewPrimaryStage("../view/FishermanProfile.fxml",
                "تفاصيل الصياد", "fbrs/photos/Fisherman.png");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
