package fbrs;

import fbrs.model.DatabaseManager;
import fbrs.utils.NavigationUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(NavigationUtil.HOME_FXML));
        primaryStage.setTitle("برنامج إدارة بُكس السمك");
        primaryStage.getIcons().add(new Image("/fbrs/photos/App_icon.png"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        primaryStage.show();
        DatabaseManager.getInstance().getConnection();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DatabaseManager.getInstance().exit();
    }
}
