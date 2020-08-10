package fbrs.controller;

import fbrs.model.Entry;
import fbrs.model.Fisherman;
import fbrs.model.Seller;
import fbrs.model.User;
import fbrs.utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class EntriesController implements Initializable {
    //UI
    public TableView<Entry> table;
    public TableColumn<Entry, Boolean> selectColumn;
    public TableColumn<Entry, String> fromColumn;
    public TableColumn<Entry, Date> dateCreatedColumn;
    public TableColumn<Entry, Date> dateUpdatedColumn;
    public TableColumn<Entry, String> toColumn;
    public TableColumn<Entry, String> typeColumn;
    public TableColumn<Entry, Integer> quantityColumn;
    public TableColumn<Entry, String> note;
    public HBox HBox;
    public Button backBtn;
    public BorderPane rootPane;
    public Button onDeleteBtn;
    public Button TypeEntriesBtn;
    public Label accountLabel;
    public Label count;
    public Label buksaLabel;
    public Text title;
    public Button SpecialCasesBtn;
    public Button EditUserProfileBtn;

    private Popup popup;
    private User user;

    public void back(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
        popup.hide();
    }

    public void fromComboBox() {
    }

    public void toComboBox() {
    }

    public void dateUpdatedComboBox() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        popup = new Popup();
        String[] array = {"استيلاء الاحتلال", "إرجاع زيادة", "خلاف بين تاجر وصياد"
                , "ضياع بُكس", "توبة تاجر", "قيد تسليم", "قيد بيع"};
        popup.getContent().add(CreateCheckBoxTree(array));
        if (user == null) {
            title.setText("جميع القيود");
            buksaLabel.setVisible(false);
            accountLabel.setVisible(false);
            count.setVisible(false);
            EditUserProfileBtn.setVisible(false);
            SpecialCasesBtn.setVisible(false);
        }
    }

    public void onDelete() {

    }

    public void setViewType(User user) {
        this.user = user;
        if (user instanceof Seller) {
            title.setText("قيود التاجر : " + user.getName());
        } else if (user instanceof Fisherman) {
            title.setText("قيود الصياد : " + user.getName());
        } else {
            System.out.println("Error: A valid user was not sent");
        }
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

    private TreeView<String> CreateCheckBoxTree(String[] array) {
        CheckBoxTreeItem<String> rootItem = createCheckBoxTreeItem("إظهار جميع القيود");
        rootItem.setExpanded(true);
        for (String s : array) {
            CheckBoxTreeItem<String> item = createCheckBoxTreeItem(s);
            rootItem.getChildren().add(item);
        }

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
        return treeView;
    }

    private CheckBoxTreeItem<String> createCheckBoxTreeItem(String value) {
        CheckBoxTreeItem<String> checkBoxTreeItem = new CheckBoxTreeItem<>(value);
        checkBoxTreeItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // هاي الميثود عشان لما يعمل صح بترسل إشارة، هان بتعمل الشفل بعد ما يعمل صح على الفلتر المطلوب
            // getValue()  العنصر الي صار عليه تغيير
            // newValue عبارة عن صح أو خطأ حسب الحالة الجديدة
            System.out.println(checkBoxTreeItem.getValue() + " - selected: " + newValue);

        });
        return checkBoxTreeItem;
    }

    public void onEditUserProfile() throws IOException {
        NavigationUtil.ViewUserProfile(user);

    }

    public void onSpecialCases() throws IOException {
        NavigationUtil.createNewPrimaryStage(NavigationUtil.SPECIAL_CASES_FXML,
                "حالات خاصة للبُكس", "/fbrs/photos/App_icon.png");
    }
}
