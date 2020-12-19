package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.User;
import fbrs.utils.NavigationUtil;
import fbrs.utils.UIUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
        List<User> selectedUsers = getSelectedUsers();
        if (!selectedUsers.isEmpty()) {
            confirmDelete(selectedUsers);
        }
    }

    public void EmptyRecycleBin() {
        List<User> AllUsers = new ArrayList<>(users);
        if (!AllUsers.isEmpty()) {
            confirmDelete(AllUsers);
        }
    }

    private void confirmDelete(List<User> users) {
        Optional<ButtonType> result =
                UIUtil.showConfirmDialog("هل أنت متأكد من الحذف النهائي؟",
                        "سيتم حذف القيود المحددة بشكل نهائي");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            model.deactivateUsers(users);
            model.deleteUsers(users);
            refreshTable();
        }
    }

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML);
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


        Platform.runLater(() -> searchField.requestFocus());
        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                back(null);
        });
    }

    private void refreshTable() {
        ObservableList<User> observableList = FXCollections.observableArrayList();

        users = new FilteredList<>(observableList);
        observableList.addAll(model.getDeletedUsers());

        table.setItems(users);

        SortedList<User> sortedList = new SortedList<>(users);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
    }

    private List<User> getSelectedUsers() {
        return users.stream().filter(User::isSelected).collect(Collectors.toList());
    }

    private void selectAllBoxes(ActionEvent e) {
        for (User user : users) {
            user.setSelected(((CheckBox) e.getSource()).isSelected());
        }
    }

    private void search() {
        String regex = ".*" + searchField.getText().replaceAll("\\s+", ".*").replaceAll("أ", "ا") + ".*";
        users.setPredicate(user -> user.toString().matches(regex));
    }
}
