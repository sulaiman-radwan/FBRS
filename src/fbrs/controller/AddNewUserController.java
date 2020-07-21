package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddNewUserController implements Initializable {
    public TextField name;
    public TextField phone;
    public ComboBox type_user;
    public ComboBox market;

    public void onClickAdd(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
