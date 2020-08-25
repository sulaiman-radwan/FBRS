package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.EntryType;
import fbrs.model.User;
import fbrs.utils.UIUtil;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SpecialCasesController implements Initializable {
    //UI
    public BorderPane rootPane;
    public ComboBox<EntryType> specialCasesComboBox;
    public TextField buksaCountTextField;
    public TextField commentTextField;

    private DatabaseModel model;
    private User user;

    public void onClickSave() {
        String buksaCount = buksaCountTextField.getText().trim();
        EntryType specialCase = specialCasesComboBox.getValue();
        String comment = commentTextField.getText().trim();
        if (specialCase == null || buksaCount.isEmpty() || Integer.parseInt(buksaCount) == 0) {
            Toolkit.getDefaultToolkit().beep();
            UIUtil.ErrorInput(buksaCountTextField);
        } else {
            switch (specialCase.getId()) {
                case 4:
                    //قيد تصحيحي
                case 5:
                    //غير معروف المصير
                case 8:
                    //استيلاء الاحتلال

                    //User balance adjustment
                    int id = model.addEntry(specialCase.getId(), user.getId(), 0,
                            Integer.parseInt(buksaCount), 0, comment);
                    //Storage adjustment
                    model.addStorageEntry(id, specialCase.getId(),
                            -1 * Integer.parseInt(buksaCount), comment);
                    break;
                case 6:
                    //توبة تاجر
                case 9:
                    //ارجاع زيادة
                    model.addStorageEntry(-1, specialCase.getId(),
                            Integer.parseInt(buksaCount), comment);
                    break;
            }
            UIUtil.showAlert("تمت العملية بنجاح", "تم إضافة حالة خاصة بنجاح",
                    specialCase.getName() + " = " + buksaCountTextField.getText(), Alert.AlertType.INFORMATION);
            ((Stage) rootPane.getScene().getWindow()).close();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        UIUtil.setNumbersOnly(buksaCountTextField);

        List<EntryType> entryTypeList = model.getEntryTypesSpecialCases();
        for (EntryType entryType : entryTypeList) specialCasesComboBox.getItems().add(entryType);

        specialCasesComboBox.setCellFactory(param -> new ListCell<EntryType>() {
            @Override
            public void updateItem(EntryType item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    setText(item.getName());

                    // Add the Tooltip with the Short Desc of the current item
                    Tooltip tooltip = new Tooltip();
                    tooltip.setText(item.getShortDesc());
                    setTooltip(tooltip);

                } else {
                    setText(null);
                    setTooltip(null);
                }
            }
        });
        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                ((Stage) rootPane.getScene().getWindow()).close();
        });
    }

    public void setUser(User user) {
        this.user = user;
    }
}
