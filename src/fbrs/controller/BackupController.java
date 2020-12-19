package fbrs.controller;

import fbrs.model.DatabaseManager;
import fbrs.model.DatabaseModel;
import fbrs.utils.LoadingDialog;
import fbrs.utils.UIUtil;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BackupController implements Initializable {

    //IU
    public TextField ExportFileNameTextField;
    public TextField ImportPathTextField;
    public VBox rootPane;

    private DatabaseModel model;
    private File file;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                ((Stage) rootPane.getScene().getWindow()).close();
        });
    }

    public void onExport() {
        onSave();
        if (file != null) {
            LoadingDialog<Boolean> loadingDialog = new LoadingDialog<>("جارِ التصدير...");

            Task<Boolean> exportTask = new Task<Boolean>() {
                @Override
                protected Boolean call() {
                    try {
                        return model.backup(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };

            exportTask.setOnSucceeded(event -> {
                loadingDialog.getDialogStage().close();

                if (exportTask.getValue()) {
                    UIUtil.showAlert("تمت العملية بنجاح",
                            "تمت علمية النسخ الاحتياطي بنجاح",
                            "مكان النسخة الاحتياطية :\n" + file.getAbsolutePath(),
                            Alert.AlertType.INFORMATION);
                } else {
                    UIUtil.showAlert("خطأ",
                            "لم يتم عمل نسخة احتياطية",
                            "تأكد من إختيار مجلد صالح :\n" + file.getAbsolutePath(),
                            Alert.AlertType.ERROR);
                }
            });

            loadingDialog.activateProgress(exportTask);
            new Thread(exportTask).start();
        }
    }

    public void onImport() {
        String path = ImportPathTextField.getText().trim();
        if (!path.isEmpty()) {
            LoadingDialog<Boolean> loadingDialog = new LoadingDialog<>("جارِ الاستيراد...");

            Task<Boolean> importTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws SQLException {
                    try {
                        model.dropDataBaseTables();
                        model.closeConnection();
                        return model.restore(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    } finally {
                        DatabaseManager.getInstance().getConnection();
                        DatabaseModel.getModel().fetchData();
                    }
                }
            };

            importTask.setOnSucceeded(event -> {
                loadingDialog.getDialogStage().close();

                if (importTask.getValue()) {
                    UIUtil.showAlert("تمت العملية بنجاح",
                            "تمت إستيراد النسخة الاحتياطية بنجاح",
                            "مكان النسخة الاحتياطية :\n" + file.getAbsolutePath(),
                            Alert.AlertType.INFORMATION);
                } else {
                    UIUtil.showAlert("خطأ",
                            "لم يتم إسترجاع النسخة الاحتياطية",
                            "تأكد من إختيار ملف صالح :'\n" + file.getAbsolutePath(),
                            Alert.AlertType.ERROR);
                }
            });

            loadingDialog.activateProgress(importTask);
            new Thread(importTask).start();
        } else {
            onBrowse();
        }
    }

    private void onSave() {
        String fileName = ExportFileNameTextField.getText().trim();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("إختر مجلد النسخة الاحتياطية");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup File", "*.sql"));
        fileChooser.setInitialFileName(fileName.isEmpty() ? "fbrs" : fileName);
        file = fileChooser.showSaveDialog(null);
    }

    public void onBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup File", "*.sql"));
        file = fileChooser.showOpenDialog(null);
        if (file != null)
            ImportPathTextField.setText(file.getAbsolutePath());
    }
}
