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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
            protected Void call() throws SQLException {
                DatabaseManager.getInstance().getConnection();
                DatabaseModel.getModel().fetchData();
                return null;
            }
        };

        loadDataTask.setOnFailed(event -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("فشل الاتصال بقاعدة البيانات");
            alert.setTitle("خطأ غير متوقع");
            alert.setContentText("اتصل بالمهندس");

            UIUtil.formatDialog(alert);

            alert.setOnCloseRequest(event1 -> exit());

            alert.showAndWait();

            event.getSource().getException().printStackTrace();
        });

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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("سيتم إغلاق البرنامج");
            alert.setHeaderText("هل تريد عمل نسخة احتياطية على سطح المكتب قبل إغلاق البرنامج؟");
            alert.setContentText(null);

            ButtonType save = new ButtonType("حفظ", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("إلغاء", ButtonBar.ButtonData.FINISH);
            ButtonType do_not_save = new ButtonType("عدم الحفظ", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(save, cancel, do_not_save);

            UIUtil.formatDialog(alert);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == save) {
                try {
                    File path = new File(System.getProperty("user.home"), "Desktop\\backup.sql");
                    if (DatabaseModel.getModel().backup(path.getAbsolutePath())) {
                        UIUtil.showAlert("تمت العملية بنجاح",
                                "تمت علمية النسخ الاحتياطي بنجاح",
                                "مكان النسخة الاحتياطية :\n" + path.getAbsolutePath(),
                                Alert.AlertType.INFORMATION);
                    } else {
                        UIUtil.showAlert("خطأ",
                                "لم يتم عمل نسخة احتياطية",
                                "تأكد من إختيار مجلد صالح :\n" + path.getAbsolutePath(),
                                Alert.AlertType.ERROR);
                    }
                    DatabaseManager.getInstance().exit();
                    exit();

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            } else if (result.get() == cancel) {
                event.consume();
            } else {
                DatabaseManager.getInstance().exit();
                exit();
            }

        });
    }
}
