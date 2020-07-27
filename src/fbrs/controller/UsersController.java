package fbrs.controller;

import fbrs.model.User;
import fbrs.utils.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    public static final int TYPE_SELLER = 1;
    public static final int TYPE_FISHERMAN = 2;

    //UI
    public TableColumn<User, Boolean> selectColumn;
    public TableColumn<User, Integer> idColumn;
    public TableColumn<User, String> nameColumn;
    public TableColumn<User, String> phoneColumn;
    public TableColumn<User, Integer> balanceColumn;
    public TableColumn<User, Boolean> marketColumn;
    public TableColumn<User, Boolean> shipTypeColumn;
    public TextField searchField;
    public TableView<User> table;
    public Button newUserBtn;
    public Button printUserBtn;
    public Button deleteSelectedBtn;
    public BorderPane rootPane;
    public Text Title;

    private FilteredList<User> users;

    @FXML
    public void back(ActionEvent event) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, event);
    }

    private void search() {
        String regex = ".*" + searchField.getText().replaceAll("/s+", ".*") + ".*";
        users.setPredicate(p -> p.getName().matches(regex) || String.valueOf(p.getId()).matches(regex));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<User> observableList = FXCollections.observableArrayList();
        users = new FilteredList<>(observableList);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        marketColumn.setCellValueFactory(new PropertyValueFactory<>("market"));
        shipTypeColumn.setCellValueFactory(new PropertyValueFactory<>("shipType"));

        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> search());

        CheckBox selectAll = new CheckBox();
        selectColumn.setGraphic(selectAll);
        selectAll.setOnAction(this::selectAllBoxes);

        table.setItems(users);
        table.setEditable(true);
    }

    public void setViewType(int viewType) {
        //todo:update Title based on type;
        switch (viewType) {
            case TYPE_SELLER:
                Title.setText("التجار");
                //Todo:get sellers from database;
                table.getColumns().remove(shipTypeColumn);
                break;
            case TYPE_FISHERMAN:
                Title.setText("الصيادين");
                //Todo:get fishermen from database;
                table.getColumns().remove(marketColumn);
        }
    }

    private void selectAllBoxes(ActionEvent e) {
        for (User use : users) {
            use.setSelected(((CheckBox) e.getSource()).isSelected());
        }
    }

    public void newUser(ActionEvent event) throws IOException {
        NavigationUtil.createNewPrimaryStage(NavigationUtil.ADD_NEW_USER_FXML,
                "إضافة مستخدم جديد", "/fbrs/photos/App_icon.png");
    }

    public void printUser(ActionEvent event) {
        //Todo
    }

    public void deleteSelected(ActionEvent event) {
        //Todo
    }
}