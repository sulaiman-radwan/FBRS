package fbrs.controller;

import fbrs.model.Fisherman;
import fbrs.model.Seller;
import fbrs.model.User;
import fbrs.utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.layout.HBox;

public class EntriesController implements Initializable {
    private User user;

    //UI
    private Popup popup = new Popup();
    public TableView table;
    public TableColumn selectColumn;
    public TableColumn fromColumn;
    public TableColumn dateCreatedColumn;
    public TableColumn dateUpdatedColumn;
    public TableColumn toColumn;
    public TableColumn typeColumn;
    public TableColumn quantityColumn1;
    public TableColumn note;
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


    public void back(ActionEvent actionEvent) {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML, actionEvent);
        popup.hide();
    }

    public void fromComboBox(ActionEvent actionEvent) {
    }

    public void toComboBox(ActionEvent actionEvent) {
    }

    public void dateUpdatedComboBox(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] array = {"استيلاء الاحتلال" ,"إرجاع زيادة" ,"خلاف بين تاجر وصياد"
                ,"ضياع بُكس", "توبة تاجر", "قيد تسليم", "قيد بيع"};
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

    public void onDelete(ActionEvent actionEvent) {

    }

    public void setViewType(User user) {
        this.user = user;
        if (user instanceof Seller) {
            title.setText("قيود تاجر " + user.getName());
            HBox.setMargin(title,new Insets(16, 0, 0, 200));
        } else if (user instanceof Fisherman) {
            title.setText("قيود صياد " + user.getName());
            HBox.setMargin(title,new Insets(16, 0, 0, 200));
        } else {
            System.out.println("Error: A valid user was not sent");
        }
    }

    public void onEntryType(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        if (!popup.isShowing())
            popup.show(stage);
        else
            popup.hide();

    }
    private TreeView<String> CreateCheckBoxTree(String[] array){
        CheckBoxTreeItem<String> rootItem = createCheckBoxTreeItem("إظهار جميع القيود");
        rootItem.setExpanded(true);
        for(int i = 0; i < array.length; i++) {
            CheckBoxTreeItem<String> item = createCheckBoxTreeItem(array[i]);
            rootItem.getChildren().add(item);
        }

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
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

    public void onEditUserProfile(ActionEvent actionEvent) throws IOException {
        NavigationUtil.ViewUserProfile(NavigationUtil.USERS_PROFILE_FXML,
                "تعديل تفاصيل المستخدم", "/fbrs/photos/App_icon.png", user);

    }

    public void onSpecialCases(ActionEvent actionEvent) throws IOException {
        NavigationUtil.createNewPrimaryStage(NavigationUtil.SPECIAL_CASES_FXML,
                "حالات خاصة للبُكس", "/fbrs/photos/App_icon.png");
    }
}
