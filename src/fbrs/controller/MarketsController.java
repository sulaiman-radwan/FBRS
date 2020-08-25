package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.Market;
import fbrs.utils.NavigationUtil;
import fbrs.utils.UIUtil;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MarketsController implements Initializable {

    //UI
    public Button backBtn;
    public Button AddNewMarketBtn;
    public BorderPane rootPane;
    public GridPane gridPane;

    private DatabaseModel model;

    public void back() {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML);
    }

    public void AddNewMarket() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("إضافة سوق جديد");
        dialog.setHeaderText("إضافة سوق جديد");
        dialog.setContentText("أدخل إسم السوق الجديد");
        UIUtil.formatDialog(dialog);
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newMarketName -> {
            newMarketName = newMarketName.trim();
            if (newMarketName.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                UIUtil.showAlert("خطأ", "إسم السوق فارغ",
                        "الرجاء التأكد من إدخال اسم السوق قبل الضغط على إضافة", Alert.AlertType.ERROR);
                AddNewMarket();
            } else {
                if (model.isValidMarketName(newMarketName)) {
                    model.addMarket(newMarketName);
                    UIUtil.showAlert("تمت العملية بنجاح", "تم إضافة السوق بنجاح",
                            "اسم السوق الجديد : " + newMarketName, Alert.AlertType.CONFIRMATION);
                    viewMarkets();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    UIUtil.showAlert("خطأ", "السوق المضاف موجود بالفعل : " + newMarketName,
                            "يجب إضافة إسم سوق غير موجود بالفعل", Alert.AlertType.ERROR);
                    AddNewMarket();
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        viewMarkets();

        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                back();
        });
    }

    private void viewMarkets() {
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
        button.setId(String.valueOf(market.getId()));
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
