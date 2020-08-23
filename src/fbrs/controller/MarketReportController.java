package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.Market;
import fbrs.model.Seller;
import fbrs.model.User;
import fbrs.utils.NavigationUtil;
import fbrs.utils.UIUtil;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MarketReportController implements Initializable {

    //UI
    public TableColumn<Seller, Boolean> selectColumn;
    public TableColumn<Seller, Integer> idColumn;
    public TableColumn<Seller, String> nameColumn;
    public TableColumn<Seller, String> phoneColumn;
    public TableColumn<Seller, Integer> balanceColumn;
    public TableView<Seller> table;
    public TextField searchField;
    public Button printBriefReportBtn;
    public Button printDetailedReportBtn;
    public Button newSellerBtn;
    public Button resetAccountsBtn;
    public BorderPane rootPane;
    public Text title;
    public DatePicker datePicker;
    public Button printSellerWithBalance;

    private DatabaseModel model;
    private Market market;
    private FilteredList<Seller> sellers;

    public void setMarket(Market market) {
        this.market = market;
        title.setText("تقرير سوق " + market);

        sellers = new FilteredList<>(model.getSellersByMarket(market.getId()));
        table.setItems(sellers);
        searchField.requestFocus();
    }

    private void refreshTable() {
        setMarket(market);
        search();
        table.refresh();
    }

    private void selectAllBoxes(ActionEvent e) {
        for (User user : sellers)
            user.setSelected(((CheckBox) e.getSource()).isSelected());
    }

    private void search() {
        String regex = ".*" + searchField.getText().replaceAll("\\s+", ".*").replaceAll("أ", "ا") + ".*";
        sellers.setPredicate(seller -> seller.toString().matches(regex));
    }

    public void back() {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.MARKETS_FXML);
    }

    public void printBriefReport() {
        //todo:
    }

    public void printDetailedReport() {
        //todo:
    }

    public void newSeller() throws IOException {
        NavigationUtil.AddSpecificUser("إضافة تاجر جديد إلى سوق " + market.getName(), market, 1);
        refreshTable();
    }

    public void zeroBalances() {
        int buksaCount = 0;
        int numberOfUsers = 0;
        for (Seller seller : sellers) {
            if (seller.getBalance() > 0) {
                buksaCount += seller.getBalance();
                numberOfUsers += 1;
            }
        }
        String contentText = "عدد البُكس التي سوف يتم إرجها = " + buksaCount + "    ,عدد التجار = " + numberOfUsers;
        Optional<ButtonType> result
                = UIUtil.showConfirmDialog("هل أنت متأكد من تصفير جميع حسابات التجار في هذا السوق؟", contentText);
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            for (Seller seller : sellers) {
                if (seller.getBalance() > 0) {
                    model.addEntry(3, seller.getId(), 0, seller.getBalance(), 0,
                            "تم تصفير الرصيد = " + seller.getBalance());
                }
            }
            refreshTable();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("darshKey"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));

        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> search());

        CheckBox selectAll = new CheckBox();
        selectColumn.setGraphic(selectAll);
        selectAll.setOnAction(this::selectAllBoxes);

        table.setEditable(true);
        UIUtil.formatDatePicker(datePicker);

        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                back();
        });
    }

    public void onClick(MouseEvent mouseEvent) throws IOException {
        User user = table.getSelectionModel().getSelectedItem();
        if (mouseEvent.getButton() == MouseButton.PRIMARY
                && mouseEvent.getClickCount() == 2
                && (user != null)) {
            Stage stage = NavigationUtil.viewUserEntries(user);
            stage.setOnCloseRequest(we -> {
                refreshTable();
            });
        }
    }

    public void onChangeDate() {
        //todo:
    }

    public void printSellerWithBalance(ActionEvent actionEvent) {
    }
}
