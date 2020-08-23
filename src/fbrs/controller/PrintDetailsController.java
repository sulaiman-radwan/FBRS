package fbrs.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PrintDetailsController implements Initializable {
    public VBox printList;
    public BorderPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] array = {"رقم التاجر", "اسم التاجر", "جوال التاجر", "رصيد التاجر"};
        // بس عليك تبعت VBox هوة الي هتعرض عليه و المصفوفة فيها الي بدك تعرضو
        CreateCheckBoxTree(array, printList);
        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                ((Stage) rootPane.getScene().getWindow()).close();
        });
    }

    private void CreateCheckBoxTree(String[] array, VBox vBox) {
        CheckBoxTreeItem<String> rootItem = createCheckBoxTreeItem("الكل");
        rootItem.setExpanded(true);
        for (String s : array) {
            CheckBoxTreeItem<String> item = createCheckBoxTreeItem(s);
            rootItem.getChildren().add(item);
        }

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());

        vBox.getChildren().addAll(treeView);
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

    public void onPrint() {
    }
}
