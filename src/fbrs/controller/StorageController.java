package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.StorageEntry;
import fbrs.utils.NavigationUtil;
import fbrs.utils.UIUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class StorageController implements Initializable {

    //UI
    public BorderPane rootPane;
    public TableView<StorageEntry> table;
    public TableColumn<StorageEntry, Boolean> selectColumn;
    public TableColumn<StorageEntry, String> causedByColumn;
    public TableColumn<StorageEntry, String> typeColumn;
    public TableColumn<StorageEntry, Integer> quantityColumn;
    public TableColumn<StorageEntry, Date> dateCreatedColumn;
    public TableColumn<StorageEntry, Date> dateUpdatedColumn;
    public TableColumn<StorageEntry, String> commentColumn;
    public Button backBtn;
    public Text title;
    public Label storageBalance;
    public TextField addToStorageTextField;
    public Button onDeleteBtn;
    public DatePicker FromDateCreated;
    public DatePicker ToDateCreated;
    public DatePicker FromDateUpdated;
    public DatePicker ToDateUpdated;
    public TextField addBrokenTextField;
    public Label brokenCount;
    public Label LostCount;

    private FilteredList<StorageEntry> storageEntries;
    private DatabaseModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        UIUtil.setNumbersOnly(addToStorageTextField);
        UIUtil.setNumbersOnly(addBrokenTextField);

        UIUtil.formatDatePicker(FromDateCreated);
        UIUtil.formatDatePicker(FromDateUpdated);
        UIUtil.formatDatePicker(ToDateCreated);
        UIUtil.formatDatePicker(ToDateUpdated);

        Tooltip tooltipFrom = new Tooltip();
        tooltipFrom.setText("عرض القيود من تاريخ");
        FromDateCreated.setTooltip(tooltipFrom);
        FromDateUpdated.setTooltip(tooltipFrom);

        Tooltip tooltipTo = new Tooltip();
        tooltipTo.setText("عرض القيود إلى تاريخ");
        ToDateCreated.setTooltip(tooltipTo);
        ToDateUpdated.setTooltip(tooltipTo);

        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        causedByColumn.setCellValueFactory(param -> new SimpleStringProperty(model.getCausativeByID(param.getValue().getCausedBy())));
        dateCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        dateUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("dateUpdated"));
        typeColumn.setCellValueFactory((param -> new SimpleStringProperty(model.getEntryTypeName(param.getValue().getType()))));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        CheckBox selectAll = new CheckBox();
        selectColumn.setGraphic(selectAll);
        selectAll.setOnAction(this::selectAllBoxes);
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        table.setEditable(true);
        refreshTable();

        Platform.runLater(() -> FromDateCreated.requestFocus());
        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                back();
        });
    }

    public void refreshTable() {
        ObservableList<StorageEntry> observableList = FXCollections.observableArrayList();
        storageEntries = new FilteredList<>(observableList);
        observableList.addAll(model.getAllStorageEntries(UIUtil.datePickerToDate(FromDateCreated), UIUtil.datePickerToDate(ToDateCreated),
                UIUtil.datePickerToDate(FromDateUpdated), UIUtil.datePickerToDate(ToDateUpdated)));
        table.setItems(storageEntries);
        storageBalance.setText(String.valueOf(model.getStorageBalance()));
        brokenCount.setText(String.valueOf(model.calculateBroken() * -1));
        LostCount.setText(String.valueOf(model.calculateLost() * -1));
        table.refresh();
    }

    public void back() {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML);
    }

    private void selectAllBoxes(ActionEvent e) {
        for (StorageEntry storageEntry : storageEntries) {
            storageEntry.setSelected(((CheckBox) e.getSource()).isSelected());
        }
    }

    public void addToStorage() {
        if (addToStorageTextField.getText().isEmpty()) {
            UIUtil.ErrorInput(addToStorageTextField);
        } else {
            model.addStorageEntry(-1, 12, Integer.parseInt(addToStorageTextField.getText()), "بكس جديدة من قبل الدرش");
            UIUtil.showAlert("تمت العملية بنجاح", "تم إضافة بًكس جديدة إلى المخزن",
                    "عدد البكس = " + addToStorageTextField.getText(), Alert.AlertType.INFORMATION);
            addToStorageTextField.clear();
            refreshTable();
        }
    }

    public void onDelete() {
        Optional<ButtonType> result =
                UIUtil.showConfirmDialog("هل أنت متأكد من الحذف النهائي؟",
                        "سيتم حذف القيود المحددة بشكل نهائي");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            model.deleteStorageEntries(getSelectedStorageEntry());
            refreshTable();
        }
    }

    private List<StorageEntry> getSelectedStorageEntry() {
        List<StorageEntry> selectedEntries = new ArrayList<>();
        for (StorageEntry storageEntry : storageEntries) {
            if (storageEntry.isSelected()) {
                selectedEntries.add(storageEntry);
            }
        }
        return selectedEntries;
    }

    public void addBroken(ActionEvent actionEvent) {
        if (addBrokenTextField.getText().isEmpty()) {
            UIUtil.ErrorInput(addBrokenTextField);
        } else {
            model.addStorageEntry(-1, 11, -1 * Integer.parseInt(addBrokenTextField.getText()),
                    "تبليغ عن بًكس محطمة من قبل الدرش");
            UIUtil.showAlert("تمت العملية بنجاح", "تم التبليغ عن بًكس محطمة",
                    "عدد البكس = " + addBrokenTextField.getText(), Alert.AlertType.INFORMATION);
            addBrokenTextField.clear();
            refreshTable();
        }
    }

    public void onResetBroken() {
        Optional<ButtonType> result =
                UIUtil.showConfirmDialog("هل أنت متأكد من رغبتك في تفريم البكس المحطمة؟",
                        "لا يمكن التارجع عن هذه العملية");

        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            model.resetBroken();
            refreshTable();
        }
    }

    public void onResetLost() {
        Optional<ButtonType> result =
                UIUtil.showConfirmDialog("هل أنت متأكد من رغبتك في تصفير البُكس غير معروفة المصير؟",
                        "لا يمكن التارجع عن هذه العملية");

        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            model.resetLost();
            refreshTable();
        }
    }
}
