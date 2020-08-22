package fbrs;

import fbrs.model.DatabaseManager;
import fbrs.model.DatabaseModel;
import fbrs.utils.NavigationUtil;
import fbrs.utils.SplashScreen;
import fbrs.utils.UIUtil;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.util.Optional;

import static javafx.application.Platform.exit;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        SplashScreen<Void> splashScreen = new SplashScreen<>("جارِ التحميل");
        primaryStage.getIcons().add(new Image("/fbrs/photos/App_icon.png"));

        Task<Void> loadDataTask = new Task<Void>() {
            @Override
            protected Void call() {
                DatabaseManager.getInstance().getConnection();
                DatabaseModel.getModel().fetchData();
                return null;
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            splashScreen.getStage().close();
            primaryStage.setTitle("برنامج الحافظ لإدارة بُكس السمك");

            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource(NavigationUtil.HOME_FXML));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);

            primaryStage.setResizable(false);
            primaryStage.setMaximized(true);

            primaryStage.show();
        });

        splashScreen.activateProgressBar(loadDataTask);
        new Thread(loadDataTask).start();

        primaryStage.setOnCloseRequest(event -> {
            Optional<ButtonType> result =
                    UIUtil.showConfirmDialog("هل أنت متأكد من إغلاق البرنامج؟", "سيتم إغلاق البرنامج");
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                DatabaseManager.getInstance().exit();
                exit();
            } else {
                event.consume();
            }
        });
    }
}
