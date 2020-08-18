package fbrs.controller;

import fbrs.model.*;
import fbrs.utils.NavigationUtil;
import fbrs.utils.UIUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InAndOutRecordController implements Initializable {
    public static final int TYPE_SELLER = 1;
    public static final int TYPE_FISHERMAN = 2;

    public TableView<Entry> table;
    public TableColumn<Entry, Boolean> selectColumn;
    public TableColumn<Entry, Number> quantityColumn;
    public TableColumn<Entry, String> idColumn;
    public TableColumn<Entry, String> nameColumn;
    public TableColumn<Entry, String> commentColumn;
    public Button backBtn;
    public TextField quantityTextField;
    public TextField userTextField;
    public TextField commentTextField;
    public Button addBtn;
    public BorderPane rootPane;
    public Text Title;
    public Label SellerOrFisherman;
    public Button AdmitBtn;
    public Label numberOfBuksa;
    public Button deleteBtn;

    private int buksaCount = 0;
    private int numberOfUsers = 0;
    private int viewType;
    private User currentUser;
    private DatabaseModel model;
    private ObservableList<Entry> entries;
    private boolean isAdmit = true;

    public void back(ActionEvent actionEvent) {
        if (isAdmit) {
            NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
        } else {
            Optional<ButtonType> result =
                    UIUtil.showConfirmDialog("سيتم فقد البيانات المدخلة في حال عدم إعتمادها",
                            "هل أنت متأكد من رغبتك في عدم حفظ البانات المدخلة؟");

            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
            }
        }
    }

    public void onAdd() {
        boolean error = false;

        String quantity = quantityTextField.getText().trim();
        String comment = commentTextField.getText().trim();

        if (currentUser == null) {
            error = true;
            UIUtil.ErrorInput(userTextField);
        }
        if (quantity.isEmpty() || Integer.parseInt(quantity) == 0) {
            error = true;
            UIUtil.ErrorInput(quantityTextField);
        }

        if (error) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            switch (viewType) {
                case TYPE_SELLER:
                    entries.add(new Entry(currentUser.getId(), 3, currentUser.getId(), 0,
                            Integer.parseInt(quantity), 0, null,
                            null, comment));
                    break;
                case TYPE_FISHERMAN:
                    entries.add(new Entry(currentUser.getId(), 1, 0, currentUser.getId(),
                            Integer.parseInt(quantity), 0, null,
                            null, comment));
            }
            table.refresh();
            resetTextFields();
            isAdmit = false;
            numberOfUsers = entries.size();
            updateBuksaCount();
            table.scrollTo(entries.size() - 1);
        }
    }

    private void resetTextFields() {
        quantityTextField.clear();
        userTextField.setText(null);
        commentTextField.clear();
        currentUser = null;
        quantityTextField.requestFocus();
    }

    public void updateBuksaCount() {
        int count = 0;
        for (Entry entry : entries)
            count += entry.getQuantity();
        buksaCount = count;
        numberOfBuksa.setText(Integer.toString(buksaCount));
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
        switch (viewType) {
            case TYPE_SELLER:
                Title.setText("كشف إستلام من التجار");
                SellerOrFisherman.setText("تاجر");
                rootPane.setStyle("-fx-background-color: #ADE498;");

                ObservableList<Seller> SellersName = model.getAllSellers();
                TextFields.bindAutoCompletion(userTextField, t -> SellersName.stream().filter(user -> {
                    String regex = ".*".concat(t.getUserText().replaceAll("\\s+", ".*")).concat(".*");
                    return user.toString().matches(regex);
                }).collect(Collectors.toList())).setOnAutoCompleted(event -> currentUser = event.getCompletion());
                break;
            case TYPE_FISHERMAN:
                Title.setText("كشف تسليم للصيادين");
                SellerOrFisherman.setText("صياد");
                rootPane.setStyle("-fx-background-color: #FEBF63;");

                ObservableList<Fisherman> FishermenName = model.getAllFishermen();
                TextFields.bindAutoCompletion(userTextField, t -> FishermenName.stream().filter(user -> {
                    String regex = ".*".concat(t.getUserText().replaceAll("\\s+", ".*")).concat(".*");
                    return user.toString().matches(regex);
                }).collect(Collectors.toList())).setOnAutoCompleted(event -> currentUser = event.getCompletion());

        }
        quantityTextField.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        UIUtil.setNumbersOnly(quantityTextField);
        entries = FXCollections.observableArrayList();

        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        nameColumn.setCellValueFactory(param -> {
            int id = param.getValue().getGiverId();
            if (id == 0)
                id = param.getValue().getTakerId();
            return new SimpleStringProperty(model.getUserById(id).getName());
        });
        idColumn.setCellValueFactory(param -> {
            int id = param.getValue().getGiverId();
            if (id == 0)
                id = param.getValue().getTakerId();
            return new SimpleStringProperty(model.getUserById(id).getDarshKey() + "");
        });
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        quantityColumn.setOnEditCommit(editEvent -> {
            if (editEvent.getNewValue().toString().equals("0")) {
                Toolkit.getDefaultToolkit().beep();
            } else {
                editEvent.getRowValue().setQuantity(Integer.parseInt(editEvent.getNewValue().toString()));
                updateBuksaCount();
            }
            table.refresh();
        });

        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        CheckBox selectAll = new CheckBox();
        selectColumn.setGraphic(selectAll);
        selectAll.setOnAction(this::selectAllBoxes);
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        table.refresh();
        table.setEditable(true);
        table.setItems(entries);
        table.refresh();
    }

    public void onAdmit() {
        String header = "";
        switch (viewType) {
            case TYPE_SELLER:
                header = "عدد البُكس المضافة = " + buksaCount + "    ,عدد التجار = " + numberOfUsers;
                break;
            case TYPE_FISHERMAN:
                header = "عدد البُكس المضافة = " + buksaCount + "    ,عدد الصيادين = " + numberOfUsers;
        }

        Optional<ButtonType> result =
                UIUtil.showConfirmDialog(header,
                        "هل تريد إعتماد البيانات المدخلة");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            for (Entry entry : entries) {
                model.addEntry(entry.getType(), entry.getGiverId(), entry.getTakerId(), entry.getQuantity(),
                        entry.getPrice(), entry.getComment());
            }
            isAdmit = true;
            entries = FXCollections.observableArrayList();
            table.setItems(entries);
            table.refresh();
            numberOfUsers = 0;
            updateBuksaCount();
        }
    }

    private void selectAllBoxes(ActionEvent e) {
        for (Entry entry : entries) {
            entry.setSelected(((CheckBox) e.getSource()).isSelected());
        }
    }

    public void onDelete() {
        Optional<ButtonType> result =
                UIUtil.showConfirmDialog("سيتم حذف القيود المحددين بشكل نهائي",
                        "هل أنت متأكد من الحذف النهائي");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            entries.removeIf(Entry::isSelected);
        }
        table.refresh();
        updateBuksaCount();
        isAdmit = entries.size() == 0;
    }

    public void onQuantity() {
        if (quantityColumn.getText().isEmpty()) {
            UIUtil.ErrorInput(quantityTextField);
            Toolkit.getDefaultToolkit().beep();
        } else
        userTextField.requestFocus();
    }
}
