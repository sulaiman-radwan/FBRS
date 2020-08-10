package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.Market;
import fbrs.utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MarketsController implements Initializable {

    //UI
    public Button backBtn;
    public Button AddNewMarketBtn;
    public Button printAllMarketsBtn;
    public BorderPane rootPane;
    public GridPane gridPane;

    private DatabaseModel model;

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
    }

    public void AddNewMarket() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("إضافة سوق جديد");
        dialog.setHeaderText("إضافة سوق جديد");
        dialog.setContentText("أدخل إسم السوق الجديد");
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        Optional<String> result = dialog.showAndWait();

        // The Java 8 way to get the response value (with lambda expression).
        result.ifPresent(model::addMarket);
    }

    public void printAllMarkets() throws IOException {
        NavigationUtil.createNewPrimaryStage(NavigationUtil.PRINT_DETAILS_FXML,
                "تفاصيل الطباعة", "/fbrs/photos/print.png");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        List<Market> markets = model.getAllMarkets();

        // Create a pane and set its properties
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(8, 0, 0, 0));
        gridPane.setVgap(64);
        gridPane.setHgap(64);

        int columnCount = 3;
        for (int i = 0; i < markets.size(); i++) {
            gridPane.add(createMarketButton(markets.get(i)), i % columnCount, i / columnCount);
        }
    }

    private Button createMarketButton(Market market) {
        Button button = new Button(market.getName());
        button.setCursor(Cursor.HAND);
        button.setId(market.getId() + "");
        button.setPrefWidth(150);
        button.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(NavigationUtil.MARKET_REPORT_FXML));

            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            rootPane.getScene().setRoot(root);

            MarketReportController controller = loader.getController();
            controller.setMarket(market);
        });
        return button;
    }
}
