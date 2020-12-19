package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.EntryType;
import fbrs.model.StorageEntry;
import fbrs.utils.NavigationUtil;
import fbrs.utils.UIUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

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
    public Label FishermenBalance;
    public Label SellersBalance;
    public Label numberManufactured;
    public Label sumOfBukas;
    public Button TypeStorageEntriesBtn;

    private FilteredList<StorageEntry> storageEntries;
    private DatabaseModel model;
    private Popup popup;
    private Map<String, Boolean> checkStorageEntries;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        checkStorageEntries = new HashMap<>();
        popup = new Popup();

        List<EntryType> entryTypeList = model.getEntryTypesByCategory(3);
        for (EntryType entryType : entryTypeList) {
            checkStorageEntries.put(entryType.getName(), true);
        }
        popup.getContent().add(CreateCheckBoxTree(entryTypeList));

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

        SortedList<StorageEntry> sortedList = new SortedList<>(storageEntries);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);

        FishermenBalance.setText(String.valueOf(model.getFishermenBalance()));
        SellersBalance.setText(String.valueOf(model.getSellersBalance()));
        numberManufactured.setText(String.valueOf(model.getNumberManufactured()));
        storageBalance.setText(String.valueOf(model.getStorageBalance()));
        brokenCount.setText(String.valueOf(model.calculateBroken() * -1));
        LostCount.setText(String.valueOf(model.calculateLost() * -1));
        sumOfBukas.setText(String.valueOf(getSumOfBukas()));
        table.refresh();
    }

    public void back() {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML);
        popup.hide();
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
            model.addStorageEntry(-1, 15, Integer.parseInt(addToStorageTextField.getText()), "بكس جديدة من قبل الدرش");
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
            model.fetchData();
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

    public void onStorageEntryType() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        if (!popup.isShowing())
            popup.show(stage);
        else
            popup.hide();
    }

    private TreeView<String> CreateCheckBoxTree(List<EntryType> array) {
        CheckBoxTreeItem<String> rootItem = createCheckBoxTreeItem("جميع القيود");
        rootItem.setExpanded(true);
        for (EntryType entry : array) {
            CheckBoxTreeItem<String> item = createCheckBoxTreeItem(entry.getName());
            rootItem.getChildren().add(item);
        }

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
        return treeView;
    }

    private CheckBoxTreeItem<String> createCheckBoxTreeItem(String value) {
        CheckBoxTreeItem<String> checkBoxTreeItem = new CheckBoxTreeItem<>(value);
        checkBoxTreeItem.setSelected(true);
        if (!value.matches("جميع القيود")) {
            checkBoxTreeItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
                setSelectCheckBox(checkBoxTreeItem.getValue(), newValue);
                storageEntries.setPredicate(entry -> getSelectCheckBox(model.getEntryTypeName(entry.getType())));
                sumOfBukas.setText(String.valueOf(getSumOfBukas()));
            });
        }
        return checkBoxTreeItem;
    }

    public void setSelectCheckBox(String value, boolean isSelected) {
        checkStorageEntries.put(value, isSelected);
    }

    public boolean getSelectCheckBox(String value) {
        return checkStorageEntries.get(value);
    }

    private int getSumOfBukas() {
        int sum = 0;
        for (StorageEntry storageEntry :
                storageEntries) {
            sum += storageEntry.getQuantity();
        }
        return sum;
    }
}
