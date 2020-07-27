package fbrs.controller;

import fbrs.utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class InAndOutRecordController implements Initializable {
    public static final int TYPE_SELLER = 1;
    public static final int TYPE_FISHERMAN = 2;

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
    public Text Title;
    public Label SellerOrFisherman;

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
    }

    public void onAdd(ActionEvent actionEvent) {
    }

    public void setViewType(int viewType) {
        //todo:update Title based on type;
        switch (viewType) {
            case TYPE_SELLER:
                Title.setText("كشف إستلام من التجار");
                SellerOrFisherman.setText("تاجر");
                rootPane.setStyle("-fx-background-color: darkseagreen;");
                //Todo:get sellers from database;
                break;
            case TYPE_FISHERMAN:
                Title.setText("كشف تسليم للصيادين");
                SellerOrFisherman.setText("صياد");
                rootPane.setStyle("-fx-background-color: LightPink;");
                //Todo:get fishermen from database;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
