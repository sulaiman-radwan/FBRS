package fbrs.controller;

import fbrs.model.DatabaseModel;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BackupController implements Initializable {
    public TextField ExportFile;
    public TextField ImportFile;

    private DatabaseModel model = DatabaseModel.getModel();
    private File file;
    private File selectedDirectory;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onExport() throws IOException, InterruptedException {
        if (ExportFile.getText().isEmpty()) {
            onBrowseExport();
        } else {
            if (selectedDirectory.isDirectory()) {
                model.backup(selectedDirectory.getAbsolutePath());
            } else {
                onBrowseExport();
            }
        }
    }

    public void onImport() throws IOException, InterruptedException {
        if (ImportFile.getText().isEmpty()) {
            onBrowseImport();
        } else {
            if (file.isFile()) {
                model.restore(file.getAbsolutePath());
            } else {
                onBrowseExport();
            }
        }
    }

    public void onBrowseExport() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("إختر مجلد النسخة الاحتياطية");
        //File defaultDirectory = new File("c:/dev/javafx");
        //directoryChooser.setInitialDirectory(defaultDirectory);
        selectedDirectory = directoryChooser.showDialog(null);
        ExportFile.setText(selectedDirectory.getAbsolutePath());
    }

    public void onBrowseImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup File", "*.sql"));
        file = fileChooser.showOpenDialog(null);
        ImportFile.setText(file.getAbsolutePath());
    }
}
