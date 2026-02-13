package Controllers;

import Models.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import org.GreenLedger.MainFX;

import java.io.IOException;

public class MainController {
    @FXML
    private Button btnSettings;

    public static void navigateToEvaluation(TableView<Projet> tableProjets) {
    }

    @FXML
    private void showSettings() {
        try {
            MainFX.setRoot("settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
