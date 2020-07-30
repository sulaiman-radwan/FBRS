package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.Market;
import fbrs.model.Seller;
import fbrs.model.User;
import fbrs.utils.NavigationUtil;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
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

    private DatabaseModel model;
    private Market market;
    private FilteredList<Seller> users;

    public void setMarket(Market market) {
        this.market = market;
        title.setText("تقرير سوق " + market);

        users = new FilteredList<>(model.getSellersByMarket(market.getId()));
        table.setItems(users);
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

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.MARKETS_FXML, actionEvent);
    }

    public void printBriefReport(ActionEvent actionEvent) {
    }

    public void printDetailedReport(ActionEvent actionEvent) {
    }

    public void newSeller(ActionEvent actionEvent) throws IOException {
        NavigationUtil.AddSpecificUser("إضافة تاجر جديد إلى سوق " + market.getName(), market, 1);
    }

    public void resetAccounts() {
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
    }
}
