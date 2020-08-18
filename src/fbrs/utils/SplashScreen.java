package fbrs.utils;

import fbrs.model.DatabaseManager;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

import static javafx.application.Platform.exit;

public class SplashScreen<T> {

    private Task<T> task;

    private Stage stage ;
    private ProgressIndicator progressIndicator;

    public SplashScreen(String message) {
        stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("برنامج إدارة بُكس السمك : الدرش");
        stage.getIcons().add(new Image("/fbrs/photos/App_icon.png"));

        Label label = new Label();
        label.setText(message);

        Image logo = new Image("/fbrs/photos/App_icon.png", 128, 128, true, true);
        ImageView imageView = new ImageView(logo);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        VBox vBox = new VBox();
        vBox.setSpacing(25);
        vBox.setPadding(new Insets(30));
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(imageView, label, progressIndicator);

        Scene scene = new Scene(vBox, 480, 320);
        scene.getStylesheets().add("/fbrs/styles/splash.css");
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            Optional<ButtonType> result =
                    UIUtil.showConfirmDialog("سيتم إغلاق البرنامج",
                            "هل أنت متأكد من إغلاق البرنامج");
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                DatabaseManager.getInstance().exit();
                exit();
            } else {
                event.consume();
            }
        });
    }

    public void activateProgressBar(Task<T> task) {
        this.task = task;
        progressIndicator.progressProperty().bind(this.task.progressProperty());
        stage.show();

        this.task.setOnCancelled(event1 -> {
            stage.close();
        });

    }

    public Stage getStage() {
        return stage;
    }
}