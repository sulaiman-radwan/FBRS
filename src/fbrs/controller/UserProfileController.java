package fbrs.controller;

import fbrs.model.*;
import fbrs.utils.NavigationUtil;
import fbrs.utils.UIUtil;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    private static final String NUMBER_REGEX = "\\d*";

    //UI
    public BorderPane rootPane;
    public GridPane gridPane;
    public CustomTextField id;
    public TextField name;
    public TextField phone;
    public TextField buksa;
    public CheckBox isEditable;
    public Label title;
    public Label TypeLabel;
    public Button onClickSaveBtn;
    public Button AddFromStorageBtn;
    public Button userEntryBtn;

    private String[] shipType;
    private DatabaseModel model;
    private List<Market> markets;
    private ValidationSupport support;
    private ComboBox<Market> marketComboBox;
    private ComboBox<String> FishermanTypeComboBox;
    private User user;

    public void onClickSave() {
        String updatedFields = "";
        boolean isUpdated = false;
        final Validator<String> validator = (final Control control, final String value) -> {
            final boolean condition = value == null || !(value.matches(NUMBER_REGEX));
            return ValidationResult.fromMessageIf(control, "يجب إدخال أرقام فقط للرقم التعريفي", Severity.ERROR, condition);
        };
        support.registerValidator(id, true, validator);

        int newID = Integer.parseInt(id.getText());

        if (user.getDarshKey() != newID) {
            if (!support.isInvalid()) {
                try {
                    model.updateDarshKey(user.getId(), newID);
                    updatedFields += "تم تغيير الرقم التعريفي للرقم : " + newID;
                    isUpdated = true;
                    user.setDarshKey(newID);
                } catch (SQLException exception) {
                    UIUtil.showAlert("لم يتم تغيير الرقم التعريفي",
                            "الرقم التعريفي مستخدم بالفعل من قبل مستخدم آخر الرجاء إختيار رقم آخر",
                            null,
                            Alert.AlertType.ERROR);
                    return;
                }
            }
        }

        if (!user.getName().equals(name.getText())) {
            if (model.updateUserName(user.getId(), name.getText())) {
                updatedFields += "\n تم تغيير الاسم ل : " + name.getText();
                user.setName(name.getText());
                isUpdated = true;
            }
        }

        if (!phone.getText().isEmpty()) {
            if (user.getPhone() == null || !user.getPhone().equals(phone.getText())) {
                if (model.updateUserPhone(user.getId(), phone.getText())) {
                    updatedFields += "\n تم تغيير رقم الجوال ل : " + phone.getText();
                    user.setPhone(phone.getText());
                    isUpdated = true;
                }
            }
        }

        if (user instanceof Seller) {
            Seller seller = (Seller) user;
            int marketId = marketComboBox.getValue().getId();

            if (seller.getMarket() != marketId) {
                if (model.updateUserMarket(user.getId(), marketId)) {
                    updatedFields += "\n تم تغيير سوق التاجر لسوق : " + markets.get(marketId - 1).getName();
                    seller.setMarket(marketId);
                    isUpdated = true;
                }
            }
        } else if (user instanceof Fisherman) {
            Fisherman fisherman = (Fisherman) user;
            int shipType = FishermanTypeComboBox.getValue().equals("لنش") ? 5 : 6;

            if (fisherman.getShipType() != shipType) {
                int DarshKey = model.updateDarshKeyByUserType(fisherman.getId(), shipType);
                this.id.setText(Integer.toString(DarshKey));
                user.setDarshKey(DarshKey);
                updatedFields += "تم تغيير الرقم التعريفي للرقم : " + DarshKey;
                if (model.updateFishermanType(fisherman.getId(), shipType)) {
                    updatedFields += "\n تم تغيير الصياد ل : " + (shipType == 5 ? this.shipType[0] : this.shipType[1]);
                    fisherman.setShipType((shipType));
                    isUpdated = true;
                }
            }
        }
        if (isUpdated) {
            UIUtil.showAlert("تمت العملية بنجاح",
                    "تم تغيير البيانات بنجاح",
                    updatedFields,
                    Alert.AlertType.INFORMATION);
        } else {
            UIUtil.showAlert("لم يتم تغيير التفاصيل",
                    "لا يوجد أي تغيرات",
                    null,
                    Alert.AlertType.INFORMATION);
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shipType = new String[]{"صياد لنش", "صياد حسكة"};
        model = DatabaseModel.getModel();
        markets = model.getAllMarkets();
        support = new ValidationSupport();
        marketComboBox = new ComboBox<>();
        FishermanTypeComboBox = new ComboBox<>();

        TextFieldSetEditable(false);

        marketComboBox.setPrefWidth(208.0);
        marketComboBox.setPrefHeight(25.0);
        marketComboBox.setPromptText("اختر سوق الجديد");

        FishermanTypeComboBox.setPromptText("إختر نوع المركبة");
        FishermanTypeComboBox.getItems().addAll("لنش", "حسكة");
        FishermanTypeComboBox.setPrefWidth(208.0);
        FishermanTypeComboBox.setPrefHeight(25.0);
    }

    public void onCheck() {
        TextFieldSetEditable(isEditable.isSelected());
    }

    public void TextFieldSetEditable(boolean isCheck) {
        id.setDisable(!isCheck);
        name.setDisable(!isCheck);
        phone.setDisable(!isCheck);
        buksa.setDisable(!isCheck);
        marketComboBox.setDisable(!isCheck);
        FishermanTypeComboBox.setDisable(!isCheck);
    }

    public void viewUser(User user) {
        this.user = user;
        if (user instanceof Seller) {
            title.setText("تعديل تفاصيل التاجر");
            AddFromStorageBtn.setVisible(false);
            userEntryBtn.setText("قيود التاجر");
            id.setText(Integer.toString(user.getDarshKey()));
            name.setText(user.getName());
            phone.setText(user.getPhone());
            buksa.setText(Integer.toString(user.getBalance()));
            for (Market market : markets) marketComboBox.getItems().add(market);
            gridPane.add(marketComboBox, 1, 4);
            marketComboBox.setValue(model.getMarketByID(((Seller) user).getMarket()));

        } else if (user instanceof Fisherman) {
            title.setText("تعديل تفاصيل الصياد");
            TypeLabel.setText("نوع المركبة");
            userEntryBtn.setText("قيود الصياد");
            id.setText(Integer.toString(user.getDarshKey()));
            name.setText(user.getName());
            phone.setText(user.getPhone());
            buksa.setText(Integer.toString(user.getBalance()));
            FishermanTypeComboBox.setValue(((Fisherman) user).getShipType() == 5 ? "لنش" : "حسكة");
            gridPane.add(FishermanTypeComboBox, 1, 4);
        } else {
            System.out.println("Error: A valid user was not sent");
        }
    }

    public void onAddFromStorage() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("إضافة بكس لرصيد الصياد");
        dialog.setHeaderText("إضافة بكس لرصيد الصياد : " + user.getName());
        dialog.setContentText("أدخل عدد البُكس المرادة : ");
        UIUtil.formatDialog(dialog);

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(number -> {
            if (number.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                UIUtil.showAlert("خطأ", "عدد البسكس فارغ",
                        "الرجاء التأكد من إدخال عدد البُكس قبل الضغط على إضافة", Alert.AlertType.ERROR);
                onAddFromStorage();
            } else {
                if (number.matches(NUMBER_REGEX)) {
                    model.addEntry(1, 0, user.getId(), Integer.parseInt(number), 0, null);
                    UIUtil.showAlert("تم العملية بنجاح", "تم إضافة البُكس لرصيد الصياد : " + user.getName(),
                            "عدد البٌكس المضافة = " + number, Alert.AlertType.CONFIRMATION);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    UIUtil.showAlert("خطأ", "الرقم المدخل غير صالح : " + number,
                            "الرجاء التأكد من إدخال أرقام فقط", Alert.AlertType.ERROR);
                    onAddFromStorage();
                }
            }
        });
    }

    public void onUserEntry() throws IOException {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(NavigationUtil.ENTRIES_FXML));
        Parent root = loader.load();

        if (user instanceof Seller) {
            stage.setTitle("قيود التاجر : " + user.getName());
            stage.getIcons().add(new Image("/fbrs/photos/seller.png"));
        } else if (user instanceof Fisherman) {
            stage.setTitle("قيود الصياد : " + user.getName());
            stage.getIcons().add(new Image("/fbrs/photos/Fisherman.png"));
        }
        Scene scene = new Scene(root, 1250, 600);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.centerOnScreen();

        EntriesController controller = loader.getController();
        controller.hideBackButton(false);
        controller.setViewType(user);
    }
}
