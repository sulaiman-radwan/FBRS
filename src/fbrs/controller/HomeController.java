package fbrs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

import static fbrs.controller.UsersController.TYPE_FISHERMAN;
import static fbrs.controller.UsersController.TYPE_SELLER;

public class HomeController {

    @FXML
    public void viewSellers(ActionEvent event) throws IOException {
        viewUsers(TYPE_SELLER, event);
    }

    @FXML
    public void viewFishermen(ActionEvent event) throws IOException {
        viewUsers(TYPE_FISHERMAN, event);
    }

    private void viewUsers(int type, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/users.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        //access the controller and call a method
        UsersController controller = loader.getController();
        controller.setViewType(type);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        window.setScene(scene);
        window.show();
    }

}
