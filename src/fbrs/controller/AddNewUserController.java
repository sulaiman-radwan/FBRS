package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.Market;
import fbrs.model.User;
import fbrs.utils.UIUtil;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddNewUserController implements Initializable {
    private static final String PHONE_REGEX = "(^$|^05[0-9]{8}$)";

    //UI
    public TextField nameTextField;
    public TextField phoneTextField;
    public ComboBox<String> userTypeComboBox;
    public ComboBox<Market> marketComboBox;
    public Label marketLabel;
    public BorderPane rootPane;

    private DatabaseModel model;
    private ValidationSupport support;

    public void onClickAdd() throws SQLException {
        support.setErrorDecorationEnabled(true); // validate and show errors now!

        int id = -1;
        Market market = marketComboBox.getValue();
        String name = nameTextField.getText().trim();
        String phone = phoneTextField.getText().trim();
        String userType = userTypeComboBox.getValue();

        String title = "";
        String message;
        String header;

        if (support.isInvalid()) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            if (userType.equals("تاجر")) {
                id = model.addUser(market.getId(), name, phone, User.getUserTypeID(userType));
                title = "تمت إضافة التاجر لسوق " + market + " بنجاح";

            } else {
                id = model.addUser(-1, name, phone, User.getUserTypeID(userType));
                title = "تمت إضافة " + userType + " بنجاح";
            }
        }
        if (id == -1) {
            title = "خطأ";
            header = "فشلت علمية الإضافة";
            message = "يرجى التأكد من إدخال البيانات بشكل صحيح وعدم إستخدام إسم مستخدم موجود بالفعل";
            UIUtil.showAlert(title, header, message, Alert.AlertType.ERROR);
        } else {
            header = "الاسم :" + name;
            message = "الرقم التعريفي = " + id;
            UIUtil.showAlert(title, header, message, Alert.AlertType.INFORMATION);
            support.setErrorDecorationEnabled(false); // we don't want errors to bother us for now.
            reset();
            ((Stage) rootPane.getScene().getWindow()).close();

        }
    }

    private void reset() {
        nameTextField.clear();
        phoneTextField.clear();
        marketComboBox.setValue(null);
        userTypeComboBox.setValue(userTypeComboBox.getPromptText());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        support = new ValidationSupport();

        List<Market> markets = model.getAllMarkets();
        for (Market market : markets) marketComboBox.getItems().add(market);

        userTypeComboBox.getItems().addAll("تاجر", "صياد لنش", "صياد حسكة");
        support.setErrorDecorationEnabled(false); // we don't want errors to bother us for now.
        support.registerValidator(nameTextField, Validator.createEmptyValidator("يجب ادخال اسم المستخدم", Severity.ERROR));
        support.registerValidator(userTypeComboBox, Validator.createEmptyValidator("يجب إختيار نوع المستخدم", Severity.ERROR));
        support.registerValidator(phoneTextField, Validator.createRegexValidator("تأكد من إدخال رقم جوال صالح", PHONE_REGEX, Severity.ERROR));


        Platform.runLater(() -> {
            rootPane.getScene().getWindow().setOnCloseRequest(event -> {
                if (!nameTextField.getText().isEmpty() || !phoneTextField.getText().isEmpty()) {
                    Optional<ButtonType> result =
                            UIUtil.showConfirmDialog("هل أنت متأكد من رغبتك في عدم حفظ الببانات المدخلة؟",
                                    "سيتم فقد البيانات المدخلة في حال عدم حفظها");
                    if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                        event.consume();
                    }
                }
            });
            rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE)
                    ((Stage) rootPane.getScene().getWindow()).close();
            });
        });
    }

    public void onSelectUserType() {
        if (userTypeComboBox.getValue().equals("صياد لنش") || userTypeComboBox.getValue().equals("صياد حسكة")) {
            marketComboBox.setDisable(true);
            marketLabel.setDisable(true);
            Validator<String> validator = (Control control, String value) ->
                    ValidationResult.fromMessageIf(control, null, Severity.ERROR, false);
            support.registerValidator(marketComboBox, validator);
        } else {
            support.registerValidator(marketComboBox, Validator.createEmptyValidator("يجب إختيار السوق", Severity.ERROR));
            marketComboBox.setDisable(false);
            marketLabel.setDisable(false);
        }
    }

    public void newSpecificUser(Market market, int viewType) {
        if (viewType == 1) {
            userTypeComboBox.setValue("تاجر");
            if (marketComboBox != null)
                marketComboBox.setValue(market);

        }
    }
}
