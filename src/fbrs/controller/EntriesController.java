package fbrs.controller;

import fbrs.model.*;
import fbrs.utils.NavigationUtil;
import fbrs.utils.UIUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

public class EntriesController implements Initializable {
    //UI
    public TableView<Entry> table;
    public TableColumn<Entry, Boolean> selectColumn;
    public TableColumn<Entry, String> fromColumn;
    public TableColumn<Entry, Date> dateCreatedColumn;
    public TableColumn<Entry, Date> dateUpdatedColumn;
    public TableColumn<Entry, String> toColumn;
    public TableColumn<Entry, String> typeColumn;
    public TableColumn<Entry, Number> quantityColumn;
    public TableColumn<Entry, Number> priceColumn;
    public TableColumn<Entry, String> comment;
    public HBox HBoxHeader;
    public Button backBtn;
    public BorderPane rootPane;
    public Button onDeleteBtn;
    public Button TypeEntriesBtn;
    public Label accountLabel;
    public Label balance;
    public Label buksaLabel;
    public Text title;
    public Button SpecialCasesBtn;
    public Button EditUserProfileBtn;
    public DatePicker FromDateCreated;
    public DatePicker ToDateCreated;
    public DatePicker FromDateUpdated;
    public DatePicker ToDateUpdated;
    public Button AddFromStorageBtn;
    public Button onDailyReportBtn;

    private Popup popup;
    private User user;
    private FilteredList<Entry> entries;
    private DatabaseModel model;
    private Map<String, Boolean> checkEntries;

    public void back() {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML);
        popup.hide();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        checkEntries = new HashMap<>();
        popup = new Popup();

        List<EntryType> entryTypeList = model.getEntryTypesByCategory(1);
        entryTypeList.addAll(model.getEntryTypesByCategory(2));
        for (EntryType entryType : entryTypeList) {
            checkEntries.put(entryType.getName(), true);
        }
        popup.getContent().add(CreateCheckBoxTree(entryTypeList));

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
        fromColumn.setCellValueFactory(param -> new SimpleStringProperty(model.getUserById(param.getValue().getGiverId()).getName()));
        toColumn.setCellValueFactory(param -> new SimpleStringProperty(model.getUserById(param.getValue().getTakerId()).getName()));
        dateCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        dateUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("dateUpdated"));
        typeColumn.setCellValueFactory((param -> new SimpleStringProperty(model.getEntryTypeName(param.getValue().getType()))));

        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        quantityColumn.setOnEditCommit(editEvent -> {
            if (editEvent.getNewValue().toString().equals("0")) {
                Toolkit.getDefaultToolkit().beep();
                table.refresh();
            } else {
                editEvent.getRowValue().setQuantity(Integer.parseInt(editEvent.getNewValue().toString()));
                model.updateEntryQuantity(editEvent.getRowValue().getId(), editEvent.getNewValue().intValue());
                refreshTable();
            }
        });

        priceColumn.setCellValueFactory((param -> {
            if (param.getValue().getPrice() == 0) {
                return null;
            } else {
                return new SimpleIntegerProperty(param.getValue().getPrice());
            }
        }));
        comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        CheckBox selectAll = new CheckBox();
        selectColumn.setGraphic(selectAll);
        selectAll.setOnAction(this::selectAllBoxes);
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        table.setEditable(true);

    }

    public void refreshTable() {
        ObservableList<Entry> observableList = FXCollections.observableArrayList();
        entries = new FilteredList<>(observableList);
        observableList.addAll(model.getAllEntries(UIUtil.datePickerToDate(FromDateCreated), UIUtil.datePickerToDate(ToDateCreated),
                UIUtil.datePickerToDate(FromDateUpdated), UIUtil.datePickerToDate(ToDateUpdated), user == null ? -1 : user.getId()));

        table.setItems(entries);

        SortedList<Entry> sortedList = new SortedList<>(entries);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);

        entries.setPredicate(entry -> getSelectCheckBox(model.getEntryTypeName(entry.getType())));
        table.refresh();

        user = model.getUserById(user.getId());
        balance.setText(String.valueOf(user.getBalance()));
    }

    private void selectAllBoxes(ActionEvent e) {
        for (Entry entry : entries) {
            entry.setSelected(((CheckBox) e.getSource()).isSelected());
        }
    }

    public void onDelete() {
        Optional<ButtonType> result =
                UIUtil.showConfirmDialog("هل أنت متأكد من الحذف النهائي؟",
                        "سيتم حذف القيود المحددة بشكل نهائي");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            model.deleteEntries(getSelectedEntries());
            setViewType(user == null ? null : model.getUserById(user.getId()));
        }
        model.fetchData();
    }

    public void setViewType(User user) {
        this.user = user;
        if (user instanceof Seller) {
            title.setText("قيود التاجر : " + user.getName());
            balance.setText(String.valueOf(user.getBalance()));
            AddFromStorageBtn.setVisible(false);
        } else if (user instanceof Fisherman) {
            title.setText("قيود الصياد : " + user.getName());
            balance.setText(String.valueOf(user.getBalance()));
            AddFromStorageBtn.setVisible(true);
        }
        if (user != null) {
            buksaLabel.setVisible(true);
            accountLabel.setVisible(true);
            balance.setVisible(true);
            EditUserProfileBtn.setVisible(true);
            SpecialCasesBtn.setVisible(true);
            rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE)
                    ((Stage) rootPane.getScene().getWindow()).close();
            });
        } else {
            title.setText("جميع القيود");
            buksaLabel.setVisible(false);
            accountLabel.setVisible(false);
            balance.setVisible(false);
            EditUserProfileBtn.setVisible(false);
            SpecialCasesBtn.setVisible(false);
            AddFromStorageBtn.setVisible(false);
            rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE)
                    back();
            });
        }
        refreshTable();
        FromDateCreated.requestFocus();
    }

    public void hideBackButton(boolean visible) {
        backBtn.setVisible(visible);
    }

    public void onEntryType() {
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
                entries.setPredicate(entry -> getSelectCheckBox(model.getEntryTypeName(entry.getType())));
            });
        }
        return checkBoxTreeItem;
    }

    public void setSelectCheckBox(String value, boolean isSelected) {
        checkEntries.put(value, isSelected);
    }

    public boolean getSelectCheckBox(String value) {
        return checkEntries.get(value);
    }

    private List<Entry> getSelectedEntries() {
        List<Entry> selectedEntries = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry.isSelected()) {
                selectedEntries.add(entry);
            }
        }
        return selectedEntries;
    }

    public void onEditUserProfile() throws IOException {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(NavigationUtil.USERS_PROFILE_FXML));
        stage.setTitle(user instanceof Seller ? "تعديل تفاصيل التاجر" : "تعديل تفاصيل الصايد");
        stage.getIcons().add(new Image(user instanceof Seller ? "/fbrs/photos/seller.png" : "/fbrs/photos/Fisherman.png"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.centerOnScreen();

        UserProfileController controller = loader.getController();
        controller.viewUser(user);
    }

    public void onSpecialCases() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(NavigationUtil.SPECIAL_CASES_FXML));

        Parent root = loader.load();
        Stage primaryStage = new Stage();
        primaryStage.setTitle("حالات خاصة للبُكس");
        primaryStage.getIcons().add(new Image("/fbrs/photos/App_icon.png"));

        primaryStage.setResizable(false);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        SpecialCasesController controller = loader.getController();
        controller.setUser(user);

        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        primaryStage.setOnCloseRequest(event -> refreshTable());

        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.showAndWait();
        balance.setText(String.valueOf(user.getBalance()));
    }

    public void onAddFromStorage() {
        UIUtil.addFromStorage(user);
        refreshTable();
    }

    public void onDailyReport() throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(NavigationUtil.DAILY_USER_REPORT_FXML));
        Parent root = loader.load();
        NavigationUtil.showPrimaryStage("التقرير اليومي", "/fbrs/photos/App_icon.png", root);

        DailyUserReport controller = loader.getController();
        controller.showReport(entries);
    }
}
