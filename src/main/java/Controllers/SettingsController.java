package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.GreenLedger.MainFX;
import Utils.NavigationContext;

import java.io.IOException;

public class SettingsController extends BaseController {

    @FXML
    private Button btnBack;

    @FXML
    public void initialize() {
        super.initialize();
    }

    @FXML
    private void showWallet() {
        try {
            String previousPage = NavigationContext.getInstance().getPreviousPage();
            MainFX.setRoot(previousPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
