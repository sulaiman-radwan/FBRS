package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.Entry;
import fbrs.model.User;
import fbrs.utils.NavigationUtil;
import fbrs.utils.UIUtil;
import javafx.application.Platform;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class FishermanInputReportController implements Initializable {
    //IU
    public TableView<Entry> table;
    public TableColumn<Entry, Boolean> selectColumn;
    public TableColumn<Entry, String> IDColumn;
    public TableColumn<Entry, Number> quantityColumn;
    public TableColumn<Entry, Number> priceColumn;
    public TableColumn<Entry, String> sellerColumn;
    public TableColumn<Entry, String> commentColumn;
    public Button backBtn;
    public TextField fishermanTextField;
    public Label fishermanName;
    public Label numberOfBuksa;
    public TextField quantityTextField;
    public TextField sellerTextField;
    public Button addBtn;
    public TextField priceTextField;
    public BorderPane rootPane;
    public Button AdmitBtn;
    public TextField commentTextField;
    public Button deleteBtn;
    public Label fishermanBalance;

    private DatabaseModel model;
    private User currentFisherman;
    private User currentSeller;
    private ObservableList<Entry> entries;
    private boolean isAdmit = true;
    private int buksaCount = 0;
    private int numberOfEntries = 0;
    private Map<Entry, Integer> overflow;

    public void back() {
        if (isAdmit) {
            NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML);
        } else {
            Optional<ButtonType> result =
                    UIUtil.showConfirmDialog("هل أنت متأكد من رغبتك في عدم حفظ البيانات المدخلة؟",
                            "سيتم فقد البيانات المدخلة في حال عدم إعتمادها");

            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML);
            }
        }
    }

    public void onAdd() {
        boolean error = false;

        String quantity = quantityTextField.getText().trim();
        String price = priceTextField.getText().trim();
        String comment = commentTextField.getText().trim();

        if (currentSeller == null || currentFisherman == currentSeller) {
            error = true;
            sellerTextField.clear();
            UIUtil.ErrorInput(sellerTextField);
        }
        if (price.isEmpty()) {
            error = true;
            UIUtil.ErrorInput(priceTextField);
        }
        if (quantity.isEmpty() || Integer.parseInt(quantity) == 0) {
            error = true;
            UIUtil.ErrorInput(quantityTextField);
        }
        if (currentFisherman == null) {
            error = true;
            UIUtil.ErrorInput(fishermanTextField);
        }

        if (error) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            Entry entry;
            int remainingBalance = calculateRemainingBalance(currentFisherman.getId());
            if (Integer.parseInt(quantity) > remainingBalance) {
                entry = new Entry(numberOfEntries, 2, currentFisherman.getId(), currentSeller.getId(),
                        Integer.parseInt(quantity), Integer.parseInt(price), null, null,
                        comment + " ,بيع زيادة = " + (Integer.parseInt(quantity) - remainingBalance));
                overflow.put(entry, Integer.parseInt(quantity) - remainingBalance);
            } else {
                entry = new Entry(numberOfEntries, 2, currentFisherman.getId(), currentSeller.getId(),
                        Integer.parseInt(quantity), Integer.parseInt(price),
                        null, null, comment);
            }

            entries.add(entry);


            table.refresh();
            resetTextFields();
            isAdmit = false;
            numberOfEntries = entries.size();
            currentSeller = null;
            updateBuksaCount();
            fishermanBalance.setText(String.valueOf(calculateRemainingBalance(currentFisherman.getId())));
            table.scrollTo(entries.size() - 1);
        }
    }

    private void updateBuksaCount() {
        int count = 0;
        for (Entry entry : entries)
            count += entry.getQuantity();
        buksaCount = count;
        numberOfBuksa.setText(Integer.toString(buksaCount));
    }

    private int calculateRemainingBalance(int userID) {
        int remainingBalance = model.getUserById(userID).getBalance();
        for (Entry entry : entries) {
            if (entry.getGiverId() == userID) {
                remainingBalance -= entry.getQuantity();
            }
        }
        return remainingBalance;
    }

    private void reset() {
        resetTextFields();
        fishermanName.setText(null);
        fishermanTextField.setText(null);
        fishermanBalance.setText("0");
        currentSeller = null;
        currentFisherman = null;
        fishermanTextField.requestFocus();
    }

    private void resetTextFields() {
        quantityTextField.clear();
        priceTextField.setText("0");
        sellerTextField.setText(null);
        commentTextField.clear();
        quantityTextField.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        entries = FXCollections.observableArrayList();
        overflow = new HashMap<>();
        UIUtil.setNumbersOnly(quantityTextField);
        UIUtil.setNumbersOnly(priceTextField);

        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        IDColumn.setCellValueFactory(param ->
                new SimpleStringProperty(String.valueOf(entries.indexOf(param.getValue()) + 1)));

        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        quantityColumn.setOnEditCommit(editEvent -> {
            if (editEvent.getNewValue().toString().equals("0")) {
                Toolkit.getDefaultToolkit().beep();
            } else {
                editEvent.getRowValue().setQuantity(Integer.parseInt(editEvent.getNewValue().toString()));
                updateBuksaCount();
                fishermanBalance.setText(String.valueOf(calculateRemainingBalance(currentFisherman.getId())));
            }
            table.refresh();
        });

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        priceColumn.setOnEditCommit(editEvent -> {
            if (editEvent.getNewValue().toString().equals("0")) {
                Toolkit.getDefaultToolkit().beep();
            } else {
                editEvent.getRowValue().setPrice(Integer.parseInt(editEvent.getNewValue().toString()));
            }
            table.refresh();
        });


        sellerColumn.setCellValueFactory(param ->
                new SimpleStringProperty(model.getUserById(param.getValue().getTakerId()).getName()));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        List<User> FishermenNames = new ArrayList<>(model.getAllFishermen());
        FishermenNames.addAll(model.getAllSellers());
        TextFields.bindAutoCompletion(fishermanTextField, t -> FishermenNames.stream().filter(user -> {
            String regex = ".*".concat(t.getUserText().replaceAll("\\s+", ".*")).concat(".*");
            return user.toString().matches(regex);
        }).collect(Collectors.toList())).setOnAutoCompleted(event -> {
            currentFisherman = event.getCompletion();
            fishermanName.setText(currentFisherman.toString());
            fishermanBalance.setText(String.valueOf(calculateRemainingBalance(currentFisherman.getId())));
            quantityTextField.requestFocus();
        });

        List<User> userList = new ArrayList<>(model.getAllSellers());
        userList.addAll(model.getAllFishermen());
        TextFields.bindAutoCompletion(sellerTextField, t -> userList.stream().filter(user -> {
            String regex = ".*".concat(t.getUserText().replaceAll("\\s+", ".*")).concat(".*");
            return user.toString().matches(regex);
        }).collect(Collectors.toList())).setOnAutoCompleted(event -> currentSeller = event.getCompletion());

        CheckBox selectAll = new CheckBox();
        selectColumn.setGraphic(selectAll);
        selectAll.setOnAction(this::selectAllBoxes);
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        table.setEditable(true);
        table.setItems(entries);

        Platform.runLater(() -> fishermanTextField.requestFocus());
        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                back();
        });
    }

    public void onAdmit() {
        String contentText = "عدد البُكس المضافة = " + buksaCount + "    ,عدد القيود = " + numberOfEntries;

        Optional<ButtonType> result = UIUtil.showConfirmDialog("هل تريد إعتماد البيانات المدخلة؟", contentText);
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            for (Entry entry : entries) {
                int id = model.addEntry(entry.getType(), entry.getGiverId(), entry.getTakerId(), entry.getQuantity(),
                        entry.getPrice(), entry.getComment());
                if (entry.getComment().contains("بيع زيادة")) {
                    model.addEntry(16, 0, entry.getGiverId(), overflow.get(entry), 0,
                            "بيع الصياد بُكس زيادة عن ما أخد");
                }

            }
            isAdmit = true;
            entries = FXCollections.observableArrayList();
            table.setItems(entries);
            table.refresh();
            numberOfEntries = 0;
            updateBuksaCount();
            fishermanBalance.setText("0");
            reset();
            model.fetchData();
        }
    }

    private void selectAllBoxes(ActionEvent e) {
        for (Entry entry : entries) {
            entry.setSelected(((CheckBox) e.getSource()).isSelected());
        }
    }

    public void onQuantity() {
        if (quantityTextField.getText().isEmpty()) {
            UIUtil.ErrorInput(quantityTextField);
            Toolkit.getDefaultToolkit().beep();
        } else
            priceTextField.requestFocus();
    }

    public void onPrice() {
        if (priceTextField.getText().isEmpty()) {
            UIUtil.ErrorInput(priceTextField);
            Toolkit.getDefaultToolkit().beep();
        } else
            sellerTextField.requestFocus();
    }

    public void onDelete() {
        Optional<ButtonType> result =
                UIUtil.showConfirmDialog("هل أنت متأكد من الحذف النهائي؟",
                        "سيتم حذف القيود المحددة بشكل نهائي");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            entries.removeIf(Entry::isSelected);
        }
        table.refresh();
        updateBuksaCount();
        fishermanBalance.setText(String.valueOf(calculateRemainingBalance(currentFisherman.getId())));
        numberOfEntries = entries.size();
        isAdmit = entries.size() == 0;
    }
}
