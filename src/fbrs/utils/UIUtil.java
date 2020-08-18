package fbrs.utils;

import fbrs.controller.EntriesController;
import fbrs.model.Fisherman;
import fbrs.model.Seller;
import fbrs.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class UIUtil {
    public static void setNumbersOnly(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    // convert DatePicker to Timestamp
    public static Timestamp dateTimestamp(DatePicker datePicker) {
        LocalDate localDate = datePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);
        return new Timestamp(date.getTime());
    }

    public static void formatDatePicker(DatePicker datePicker) {
        datePicker.setOnShowing(event -> Locale.setDefault(Locale.Category.FORMAT, new Locale("ar")));
        datePicker.setValue(LocalDate.now());
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty())
                    return null;
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        });
    }

    public static void formatDialog(Dialog dialog) {
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.getDialogPane().getScene().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        // Get the Stage.
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        stage.getIcons().add(new Image("/fbrs/photos/App_icon.png"));

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(stage.getScene());
    }

    public static Optional<ButtonType> showConfirmDialog(String headerText, String contentText) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("تأكيد");
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);

        ButtonType buttonTypeOk = new ButtonType("نعم", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("لا", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        UIUtil.formatDialog(dialog);

        return dialog.showAndWait();
    }

    public static void showAlert(String title, String header, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(message);

        UIUtil.formatDialog(alert);

        alert.showAndWait();
    }

    public static void ErrorInput(TextField textField) {
        textField.requestFocus();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    textField.setStyle("-fx-background-color: red");
                }),
                new KeyFrame(Duration.seconds(1), event -> {
                    textField.setStyle(null);
                })
        );
        timeline.play();
    }
}
