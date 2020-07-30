package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.Market;
import fbrs.model.User;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AddNewUserController implements Initializable {

    //UI
    public TextField nameTextField;
    public TextField phoneTextField;
    public ComboBox<String> userTypeComboBox;
    public ComboBox<Market> marketComboBox;
    public Label marketLabel;

    private DatabaseModel model;
    private ValidationSupport support;

    public void onClickAdd() {
        support.setErrorDecorationEnabled(true); // validate and show errors now!
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        int id = -1;
        Market market = marketComboBox.getValue();
        String name = nameTextField.getText();
        String phone = phoneTextField.getText();
        String userType = userTypeComboBox.getValue();

        String message = "";

        if (support.isInvalid()) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            if (userType.equals("تاجر")) {
                id = model.addUser(market.getId(), name, phone, User.getUserTypeID(userType));
                message = "تمت إضافة التاجر لسوق " + market + " بنجاح";

            } else {
                id = model.addUser(-1, name, phone, User.getUserTypeID(userType));
                message = "تمت إضافة " + userType + " بنجاح";
            }
        }
        if (id == -1) {
            message = "فشلت علمية الإضافة";
            alert.setHeaderText(message);
            alert.setContentText("يرجى التأكد من إدخال البيانات بشكل صحيح وعدم إستخدام إسم مستخدم موجود بالفعل");
            alert.showAndWait();
        } else {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setTitle(message);
            alert.setHeaderText("الاسم :" + name);
            alert.setContentText("الرقم التعريفي = " + id);
            alert.showAndWait();
            support.setErrorDecorationEnabled(false); // we don't want errors to bother us for now.
            reset();
        }
    }

    private void reset() {
        nameTextField.setText("");
        phoneTextField.setText("");
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

    }

    public void onSelectUserType() {
        if (userTypeComboBox.getValue().equals("صياد لنش") || userTypeComboBox.getValue().equals("صياد حسكة")) {
            marketComboBox.setVisible(false);
            marketLabel.setVisible(false);
            Validator<String> validator = (Control control, String value) ->
                    ValidationResult.fromMessageIf(control, null, Severity.ERROR, false);
            support.registerValidator(marketComboBox, validator);
        } else {
            support.registerValidator(marketComboBox, Validator.createEmptyValidator("يجب إختيار السوق", Severity.ERROR));
            marketComboBox.setVisible(true);
            marketLabel.setVisible(true);
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
