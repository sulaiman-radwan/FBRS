package fbrs.utils;

import fbrs.controller.AddNewUserController;
import fbrs.controller.EntriesController;
import fbrs.controller.UserProfileController;
import fbrs.model.Market;
import fbrs.model.Seller;
import fbrs.model.User;
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
    public final static String RECYCLE_BIN_FXML = "/fbrs/view/RecycleBin.fxml";
    public final static String USERS_PROFILE_FXML = "/fbrs/view/UserProfile.fxml";
    public final static String SPECIAL_CASES_FXML = "/fbrs/view/SpecialCases.fxml";
    public final static String STORAGE_FXML = "/fbrs/view/storage.fxml";
    public final static String USERS_FXML = "/fbrs/view/users.fxml";
    public static final String BACKUP_FXML = "/fbrs/view/backup.fxml";
    public static final String FAQ_FXML = "/fbrs/view/FAQ.fxml";
    public static final String DAILY_USER_REPORT_FXML = "/fbrs/view/DailyUserReport.fxml";


    public static void navigateTo(Parent rootPane, String path) {
        try {
            Parent root = FXMLLoader.load(NavigationUtil.class.getResource(path));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createNewPrimaryStage(String path, String stageName, String photoPath) throws IOException {
        Parent root = FXMLLoader.load(NavigationUtil.class.getResource(path));
        showPrimaryStage(stageName, photoPath, root);
    }

    public static Stage viewUserProfile(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(NavigationUtil.USERS_PROFILE_FXML));
        Parent root = loader.load();
        Stage stage = showPrimaryStage((user instanceof Seller ? "تعديل تفاصيل التاجر" : "تعديل تفاصيل الصايد"),
                (user instanceof Seller ? "/fbrs/photos/seller.png" : "/fbrs/photos/Fisherman.png"), root);
        UserProfileController controller = loader.getController();
        controller.viewUser(user);
        return stage;
    }

    public static Stage showPrimaryStage(String stageName, String photoPath, Parent root) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle(stageName);
        primaryStage.getIcons().add(new Image(photoPath));
        primaryStage.setResizable(false);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.show();
        return primaryStage;
    }

    public static Stage AddSpecificUser(String stageName, Market market, int viewType) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(NavigationUtil.ADD_NEW_USER_FXML));
        Parent root = loader.load();
        Stage stage = showPrimaryStage(stageName, "/fbrs/photos/App_icon.png", root);
        AddNewUserController controller = loader.getController();
        controller.newSpecificUser(market, viewType);
        return stage;
    }

    public static Stage viewUserEntries(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(NavigationUtil.ENTRIES_FXML));
        Parent root = loader.load();
        Stage stage = showPrimaryStage(((user instanceof Seller ? "قيود التاجر : " : "قيود الصياد : ") + user.getName()),
                (user instanceof Seller ? "/fbrs/photos/seller.png" : "/fbrs/photos/Fisherman.png"), root);

        EntriesController controller = loader.getController();
        controller.hideBackButton(false);
        controller.setViewType(user);
        return stage;
    }
}
