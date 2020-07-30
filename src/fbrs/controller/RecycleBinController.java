package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.User;
import fbrs.utils.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecycleBinController implements Initializable {
    //IU
    public TableView<User> table;
    public TableColumn<User, Boolean> selectColumn;
    public TableColumn<User, Integer> idColumn;
    public TableColumn<User, String> nameColumn;
    public TableColumn<User, String> phoneColumn;
    public TableColumn<User, Integer> balanceColumn;
    public TextField searchField;
    public Button restoreBtn;
    public Button FinalDeletionBtn;
    public Button EmptyRecycleBinBtn;
    public BorderPane rootPane;

    private DatabaseModel model;
    private FilteredList<User> users;

    public void restore() {
        model.reactivateUsers(getSelectedUsers());
        refreshTable();
    }

    public void deleteSelected() {
        ArrayList<User> selectedUsers = getSelectedUsers();
        if (!selectedUsers.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("تأكيد حذف النهائي");
            alert.setHeaderText("سيتم حذف المستخدمين المحددين بشكل نهائي");
            alert.setContentText("هل أنت متأكد من الحذف النهائي");
            confirmDelete(selectedUsers, alert);
        }
    }

    public void EmptyRecycleBin() {
        ArrayList<User> AllUsers = new ArrayList<>(users);
        if (!AllUsers.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("تأكيد إفراغ سلة المحذوفات");
            alert.setHeaderText("تأكيد حذف جميع المستخدمين بشكل نهائي");
            alert.setContentText("هل أنت متأكد من إفراغ سلة المحذوفات");
            confirmDelete(AllUsers, alert);
        }
    }

    private void confirmDelete(ArrayList<User> users, Alert alert) {
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            model.deactivateUsers(users);
            model.deleteUsers(users);
            refreshTable();
        } else {
            alert.close();
        }
    }

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> search());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("darshKey"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));

        CheckBox selectAll = new CheckBox();
        selectColumn.setGraphic(selectAll);
        selectAll.setOnAction(this::selectAllBoxes);
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        table.setEditable(true);

        refreshTable();
    }

    private void refreshTable() {
        ObservableList<User> observableList = FXCollections.observableArrayList();

        users = new FilteredList<>(observableList);
        observableList.addAll(model.getDeletedUsers());

        table.setItems(users);
    }

    private ArrayList<User> getSelectedUsers() {
        ArrayList<User> selectedUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isSelected()) {
                selectedUsers.add(user);
            }
        }
        return selectedUsers;
    }

    private void selectAllBoxes(ActionEvent e) {
        for (User use : users) {
            use.setSelected(((CheckBox) e.getSource()).isSelected());
        }
    }

    private void search() {
        String regex = ".*" + searchField.getText().replaceAll("/s+", ".*") + ".*";
        users.setPredicate(p -> p.getName().matches(regex) || String.valueOf(p.getId()).matches(regex));
    }
}
