package fbrs.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class BackupController implements Initializable {
    public TextField ExportFile;
    public TextField BrowseFile;
    File file = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onExport() {
    }

    public void onImport() {
    }

    public void onBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup File", "*.*"));
        file = fileChooser.showOpenDialog(null);
        System.out.println(file.getAbsolutePath());
    }
}
