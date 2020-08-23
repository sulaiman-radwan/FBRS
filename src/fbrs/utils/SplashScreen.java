package fbrs.utils;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreen<T> {

    private Task<T> task;

    private Stage stage;
    private ProgressIndicator progressIndicator;

    public SplashScreen(String message) {
        stage = new Stage();
        stage.setResizable(false);
        //stage.setMaximized(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("برنامج الحافظ لإدارة بُكس السمك");
        stage.getIcons().add(new Image("/fbrs/photos/App_icon.png"));

        Label label = new Label();
        label.setText(message);

        Image logo = new Image("/fbrs/photos/FBRS.png", 514, 320, true, true);
        ImageView imageView = new ImageView(logo);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        BorderPane borderPane = new BorderPane();

        VBox vBox = new VBox();
        vBox.setSpacing(16);
        //vBox.setPadding(new Insets(8));
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(imageView, label, progressIndicator);

        Label copyright = new Label("©2020 OSMMU");
        copyright.setFont(Font.font(10));

        borderPane.setCenter(vBox);
        borderPane.setBottom(copyright);
        BorderPane.setAlignment(copyright, Pos.CENTER_RIGHT);
        BorderPane.setMargin(copyright, new Insets(16));
        //BorderPane.setMargin(vBox, new Insets(32,0,0,0));


        Scene scene = new Scene(borderPane, 650, 500);
        scene.getStylesheets().add("/fbrs/styles/splash.css");
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        stage.setScene(scene);
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