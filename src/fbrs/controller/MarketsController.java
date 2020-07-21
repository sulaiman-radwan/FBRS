package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MarketsController implements Initializable {
    public Button backBtn;
    public Button AddNewMarketBtn;
    public Button printAllMarketsBtn;
    public BorderPane rootPane;

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
    }

    public void AddNewMarket() {
    }

    public void printAllMarkets() throws IOException {
        NavigationUtil.createNewPrimaryStage("../view/printDetails.fxml",
                "تفاصيل الطباعة", "fbrs/photos/print.png");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
