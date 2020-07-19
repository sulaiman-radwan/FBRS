package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PrintDetailsController implements Initializable {
    public VBox printList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] array = {"رقم التاجر", "اسم التاجر", "جوال التاجر", "رصيد التاجر"};
        // بس عليك تبعت VBox هوة الي هتعرض عليه و المصفوفة فيها الي بدك تعرضو
        CreateCheckBoxTree(array, printList);
    }

    public static void CreateCheckBoxTree(String[] array, VBox vBox){
        CheckBoxTreeItem<String> rootItem = createCheckBoxTreeItem("الكل");
        rootItem.setExpanded(true);
        for(int i = 0; i < array.length; i++) {
            CheckBoxTreeItem<String> item = createCheckBoxTreeItem(array[i]);
            rootItem.getChildren().add(item);
        }

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

        vBox.getChildren().addAll(treeView);
    }

    public static CheckBoxTreeItem<String> createCheckBoxTreeItem(String value) {
        CheckBoxTreeItem<String> checkBoxTreeItem = new CheckBoxTreeItem<>(value);
        checkBoxTreeItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // هاي الميثود عشان لما يعمل صح بترسل إشارة، هان بتعمل الشفل بعد ما يعمل صح على الفلتر المطلوب
            // getValue()  العنصر الي صار عليه تغيير
            // newValue عبارة عن صح أو خطأ حسب الحالة الجديدة
            System.out.println(checkBoxTreeItem.getValue() + " - selected: " + newValue);

        });
        return checkBoxTreeItem;
    }

    public void onPrint(ActionEvent actionEvent) {
    }
}
