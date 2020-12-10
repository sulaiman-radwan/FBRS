package fbrs.utils;

import fbrs.model.DatabaseModel;
import fbrs.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.awt.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class UIUtil {
    public static void setNumbersOnly(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
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

    // convert DatePicker to Date
    public static Date datePickerToDate(DatePicker datePicker) {
        LocalDate localDate = datePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        return Date.from(instant);
    }

    // convert LocalDate to Date
    public static Date localDateToDate(LocalDate localDate) {
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        return Date.from(instant);
    }

    public static Timestamp dateTimestamp(LocalDate localDate) {
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);
        return new Timestamp(date.getTime());
    }

    public static void formatDatePicker(DatePicker datePicker) {
        datePicker.setOnShowing(event -> Locale.setDefault(Locale.Category.FORMAT, new Locale("ar")));
        datePicker.setValue(LocalDate.now());
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        // Get the Stage.
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        stage.getIcons().add(new Image("/fbrs/photos/App_icon.png"));

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(stage.getScene());

        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.getDialogPane().getScene().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
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

    public static void addFromStorage(User user) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("إضافة بكس لرصيد الصياد");
        dialog.setHeaderText("إضافة بكس لرصيد الصياد : " + user.getName());
        dialog.setContentText("أدخل عدد البُكس المرادة : ");
        UIUtil.formatDialog(dialog);

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(number -> {
            boolean error = false;
            number = number.trim();
            if (number.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                UIUtil.showAlert("خطأ", "عدد البسكس فارغ",
                        "الرجاء التأكد من إدخال عدد البُكس قبل الضغط على إضافة",
                        Alert.AlertType.ERROR);
                error = true;
            } else if (!number.matches("\\d*")) {
                Toolkit.getDefaultToolkit().beep();
                UIUtil.showAlert("خطأ", "الرقم المدخل غير صالح : " + number,
                        "الرجاء التأكد من إدخال أرقام فقط", Alert.AlertType.ERROR);
                error = true;
            }
            if (error) {
                addFromStorage(user);
            } else {
                DatabaseModel.getModel().addEntry(1, 0, user.getId(), Integer.parseInt(number), 0, null);
                UIUtil.showAlert("تمت العملية بنجاح", "تم إضافة البُكس لرصيد الصياد : " + user.getName(),
                        "عدد البٌكس المضافة = " + number, Alert.AlertType.CONFIRMATION);
            }
        });
        DatabaseModel.getModel().fetchData();
    }

    public static <T extends User> void setUsersAsUnselected(List<T> users) {
        for (T t : users) {
            User user = t;
            user.setSelected(false);
        }
    }
}
