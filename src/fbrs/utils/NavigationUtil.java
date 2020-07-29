package fbrs.utils;

import fbrs.controller.UserProfileController;
import fbrs.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

public class NavigationUtil {
    public final static String HOME_FXML = "/fbrs/view/home.fxml";
    public final static String ADD_NEW_USER_FXML = "/fbrs/view/addNewUser.fxml";
    public final static String ENTRIES_FXML = "/fbrs/view/Entries.fxml";
    public final static String FISHERMAN_INPUT_REPORT_FXML = "/fbrs/view/FishermanInputReport.fxml";
    public final static String IN_AND_OUT_RECORD_FXML = "/fbrs/view/InAndOutRecord.fxml";
    public final static String MARKET_REPORT_FXML = "/fbrs/view/MarketReport.fxml";
    public final static String MARKETS_FXML = "/fbrs/view/Markets.fxml";
    public final static String PRINT_DETAILS_FXML = "/fbrs/view/printDetails.fxml";
    public final static String RECYCLE_BIN_FXML = "/fbrs/view/RecycleBin.fxml";
    public final static String USERS_PROFILE_FXML = "/fbrs/view/UserProfile.fxml";
    public final static String SPECIAL_CASES_FXML = "/fbrs/view/SpecialCases.fxml";
    public final static String STORAGE_FXML = "/fbrs/view/storage.fxml";
    public final static String USERS_FXML = "/fbrs/view/users.fxml";
    public static final String BACKUP_FXML = "/fbrs/view/backup.fxml";


    public static void navigateTo(Parent rootPane, String path, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(NavigationUtil.class.getResource(path));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createNewPrimaryStage(String path, String nameStage, String photoPath) throws IOException {
        Parent root = FXMLLoader.load(NavigationUtil.class.getResource(path));
        showPrimaryStage(nameStage, photoPath, root);
    }

    public static void ViewUserProfile(String path, String nameStage, String photoPath, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(path));
        Parent root = loader.load();
        showPrimaryStage(nameStage, photoPath, root);
        UserProfileController controller = loader.getController();
        controller.viewUser(user);
    }

    private static void showPrimaryStage(String nameStage, String photoPath, Parent root) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle(nameStage);
        primaryStage.getIcons().add(new Image(photoPath));
        primaryStage.setResizable(false);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.show();
    }
}
