package fbrs.controller;

import fbrs.model.*;
import fbrs.utils.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

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

    private FBRSPrintUtil printUtil;
    private DatabaseModel model;
    private Market market;
    private FilteredList<Seller> sellers;

    private File homeDirectory;
    private File reportsDirectory;
    private File briefReportsDirectory;
    private File detailedReportsDirectory;
    private File balanceReportDirectory;

    public void setMarket(Market market) {
        this.market = market;
        title.setText("تقرير سوق " + market);

        sellers = new FilteredList<>(model.getSellersByMarket(market.getId()));
        table.setItems(sellers);

        SortedList<Seller> sortedList = new SortedList<>(sellers);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);

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
        printReport(false);
    }

    public void printDetailedReport() {
        printReport(true);
    }

    public void printReport(boolean isDetailed) {
        List<FBRSPrintableUserEntry> printableUserEntries = new ArrayList<>();
        Date selectedDate = UIUtil.datePickerToDate(datePicker);
        Date selectedDateMinusOneDay = UIUtil.localDateToDate(datePicker.getValue().minusDays(1));
        Date today = UIUtil.localDateToDate(LocalDate.now());

        LoadingDialog<Void> loadingDialog = new LoadingDialog<>("جارِ اعداد التقرير...");

        Task<Void> prepareDataTask = new Task<Void>() {
            @Override
            protected Void call() {
                int progress = 0;
                List<Seller> sellersList = sellers.filtered(seller -> seller.getBalance() > 0);
                updateProgress(progress, sellersList.size());
                FBRSPrintableUserEntry printableUserEntry;
                ObservableList<Entry> userEntries;
                for (Seller user : sellersList) {
                    userEntries = model.getAllEntries(selectedDate, today, selectedDate, today, user.getId());

                    printableUserEntry = new FBRSPrintableUserEntry(user,
                            userEntries.filtered(entry -> entry.getType() == 2),
                            model.calculateUserBalanceToDateInc(user.getId(), selectedDateMinusOneDay),
                            userEntries.filtered(entry -> entry.getType() == 3 ||
                                    (entry.getType() == 2 && entry.getGiverId() == user.getId())).stream()
                                    .mapToInt(Entry::getQuantity).sum());

                    printableUserEntries.add(printableUserEntry);

                    updateProgress(++progress, sellersList.size());
                }
                return null;
            }
        };

        prepareDataTask.setOnSucceeded(event -> {
            boolean isSingleDay = UIUtil.localDateToDate(LocalDate.now()).equals(selectedDate);
            if (printableUserEntries.isEmpty()) {
                UIUtil.showAlert("لم يتم تنفيذ العملية",
                        "جميع أرصدة التجار تساوي صفر",
                        market.getName(),
                        Alert.AlertType.INFORMATION);
            } else {
                String fileName = isDetailed ? "التقرير المفصل" : "التقرير المختصر";

                fileName += isSingleDay ? String.format("_%s_%s.docx", market.getName(), LocalDate.now())
                        : String.format("_%s_من_%s_إلى_%s.docx", market.getName(), datePicker.getValue(), LocalDate.now());

                File file = new File(isDetailed ? detailedReportsDirectory : briefReportsDirectory, fileName);

                printUtil.printMarketReport(file.getAbsolutePath(), printableUserEntries,
                        isDetailed, isSingleDay, (isSingleDay ? null : selectedDate));
            }
            loadingDialog.getDialogStage().close();
        });

        loadingDialog.activateProgress(prepareDataTask);
        new Thread(prepareDataTask).start();
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
        String contentText = "عدد البُكس التي سوف يتم إرجاعها = " + buksaCount + "    ,عدد التجار = " + numberOfUsers;
        Optional<ButtonType> result
                = UIUtil.showConfirmDialog("هل أنت متأكد من تصفير جميع حسابات التجار في هذا السوق؟", contentText);
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            for (Seller seller : sellers) {
                if (seller.getBalance() > 0) {
                    model.addEntry(3, seller.getId(), 0, seller.getBalance(), 0,
                            "تم تصفير الرصيد = " + seller.getBalance());
                }
            }
            model.fetchData();
            refreshTable();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        printUtil = FBRSPrintUtil.getInstance();

        homeDirectory = new File(System.getProperty("user.home"), "Desktop");
        reportsDirectory = new File(homeDirectory, "التقارير");
        balanceReportDirectory = new File(reportsDirectory, "تقارير الأرصدة");
        briefReportsDirectory = new File(reportsDirectory, "التقارير المختصرة");
        detailedReportsDirectory = new File(reportsDirectory, "التقارير المفصلة");

        // Create Directory if not Exists
        reportsDirectory.mkdir();
        balanceReportDirectory.mkdir();
        briefReportsDirectory.mkdir();
        detailedReportsDirectory.mkdir();

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

    public void printSellerWithBalance() {
        List<Seller> balancesReport = sellers.filtered(seller -> seller.getBalance() > 0);

        if (balancesReport.isEmpty()) {
            UIUtil.showAlert("لم يتم تنفيذ العملية",
                    "جميع أرصدة التجار تساوي صفر",
                    market.getName(),
                    Alert.AlertType.INFORMATION);
        } else {
            File file = new File(balanceReportDirectory,
                    String.format("%s_%s_%s.docx", "تقرير_الأرصدة", market.getName(), LocalDate.now()));
            printUtil.printMarketBalanceReport(file.getAbsolutePath(), balancesReport);
        }
    }
}
