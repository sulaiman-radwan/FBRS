package fbrs.utils;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class LoadingDialog<E> {

    private Task<E> mTask;

    private Stage dialogStage;
    private ProgressIndicator progressIndicator;

    public LoadingDialog() {
    }

    public LoadingDialog(String message) {
        dialogStage = new Stage();
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(message);

        Label label = new Label();
        label.setText(message);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Button button = new Button("إلغاء");
        button.setCancelButton(true);
        button.setOnAction(event -> mTask.cancel());

        VBox vBox = new VBox();
        vBox.setSpacing(15);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(label, progressIndicator, button);

        Scene scene = new Scene(vBox, 200, 150);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        dialogStage.setScene(scene);

        dialogStage.setOnCloseRequest(event -> mTask.cancel());
    }

    public void activateProgress(Task<E> task) {
        mTask = task;
        progressIndicator.progressProperty().bind(mTask.progressProperty());
        dialogStage.show();

        mTask.setOnCancelled(event1 -> {
            dialogStage.close();
        });

    }


    public Stage getDialogStage() {
        return dialogStage;
    }
}