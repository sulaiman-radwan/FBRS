package fbrs.controller;

import fbrs.model.Fisherman;
import fbrs.model.Seller;
import fbrs.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {
    private User user;
    private String[] shipType = {"صياد لنش", "صياد حسكة"};

    //UI
    public TextField id;
    public TextField name;
    public TextField phone;
    public TextField buksa;
    public CheckBox isEditable;
    public Label title;
    public Label marketLabel;
    public ComboBox marketComboBox;
    public Button onClickSaveBtn;
    public Button AddFromStorageBtn;
    public Button userEntryBtn;

    public void onClickSave(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TextFieldSetEditable(false);
    }

    public void onCheck(ActionEvent actionEvent) {
        TextFieldSetEditable(isEditable.isSelected());
    }
    public void TextFieldSetEditable(boolean isCheck){
        id.setEditable(isCheck);
        name.setEditable(isCheck);
        phone.setEditable(isCheck);
        buksa.setEditable(isCheck);
    }

    public void viewUser(User user) {
        this.user = user;
        if (user instanceof Seller) {
            title.setText("تعديل تفاصيل التاجر");
            AddFromStorageBtn.setVisible(false);
            userEntryBtn.setText("قيود التاجر");
            id.setText(Integer.toString(user.getId()));
            name.setText(user.getName());
            phone.setText(user.getPhone());
            buksa.setText(Integer.toString(user.getBalance()));
            marketComboBox.getItems().addAll("الشيخ رضوان", "المعسكر");

        } else if (user instanceof Fisherman) {
            title.setText("تعديل تفاصيل الصياد");
            marketLabel.setText("نوع المركبة");
            marketComboBox.setPromptText("إختر نوع المركبة");
            marketComboBox.getItems().addAll("صياد لنش", "صياد حسكة");
            userEntryBtn.setText("قيود الصياد");
            id.setText(Integer.toString(user.getId()));
            name.setText(user.getName());
            phone.setText(user.getPhone());
            buksa.setText(Integer.toString(user.getBalance()));
            marketComboBox.setValue(this.shipType[((Fisherman) user).getShipType() + 1]);
        } else {
            System.out.println("Error: A valid user was not sent");
        }
    }

    public void onAddFromStorage(ActionEvent actionEvent) {
    }

    public void onUserEntry(ActionEvent actionEvent) {
    }
}
