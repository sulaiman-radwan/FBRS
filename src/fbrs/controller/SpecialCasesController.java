package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.EntryType;
import fbrs.model.User;
import fbrs.utils.UIUtil;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SpecialCasesController implements Initializable {
    //UI
    public ComboBox<EntryType> specialCases;
    public TextField buksaCountTextField;

    private DatabaseModel model;
    private User user;

    public void onClickSave() {
        String buksaCount = buksaCountTextField.getText().trim();
        EntryType cases = specialCases.getValue();
        if (cases == null || buksaCount.isEmpty() || Integer.parseInt(buksaCount) == 0) {
            Toolkit.getDefaultToolkit().beep();
            UIUtil.ErrorInput(buksaCountTextField);
        } else {
            model.addEntry(cases.getId(), user.getId(), model.getUserById(0).getId(),
                    Integer.parseInt(buksaCount), 0, cases.getShortDesc());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        UIUtil.setNumbersOnly(buksaCountTextField);

        List<EntryType> entryTypeList = model.getEntryTypesSpecialCases();
        for (EntryType entryType : entryTypeList) specialCases.getItems().add(entryType);

        specialCases.setCellFactory(param -> new ListCell<EntryType>() {
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
    }

    public void setUser(User user) {
        this.user = user;
    }
}
