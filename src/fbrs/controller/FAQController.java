package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.FAQ;
import fbrs.utils.NavigationUtil;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FAQController implements Initializable {
    public Accordion accordion;
    public BorderPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<FAQ> faqs = DatabaseModel.getModel().getAllFAQ();

        for (FAQ faq : faqs) {
            // Create First TitledPane.
            TitledPane titledPane = new TitledPane();
            titledPane.setText(faq.getQuestion());
            titledPane.setStyle("-fx-font-size: 18");
            titledPane.setMaxHeight(100);

            TextArea answer = new TextArea(faq.getAnswer());
            answer.setWrapText(true);
            answer.setEditable(false);

            titledPane.setContent(answer);
            accordion.getPanes().add(titledPane);
        }

        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                back();
        });
    }

    public void back() {
        NavigationUtil.navigateTo(rootPane, NavigationUtil.HOME_FXML);
    }
}
