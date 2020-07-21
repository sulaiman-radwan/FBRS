package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MarketReportController implements Initializable {
    public TableColumn balanceColumn;
    public TableColumn selectColumn;
    public TableColumn idColumn;
    public TableColumn nameColumn;
    public TableColumn phoneColumn;
    public TableView table;
    public TextField searchField;
    public Button printBriefReportBtn;
    public Button printDetailedReportBtn;
    public Button newSellerBtn;
    public Button resetAccountsBtn;

    public void back(ActionEvent actionEvent) {
    }

    public void printBriefReport(ActionEvent actionEvent) {
    }

    public void printDetailedReport(ActionEvent actionEvent) {
    }

    public void newSeller(ActionEvent actionEvent) {
    }

    public void resetAccounts(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
