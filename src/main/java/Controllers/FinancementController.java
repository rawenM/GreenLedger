package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
public class FinancementController {

    @FXML
    private TableView<?> tableFinancement;
    @FXML
    private TableColumn<?, ?> colFinId;
    @FXML
    private TableColumn<?, ?> colFinProjetId;
    @FXML
    private TableColumn<?, ?> colFinBanqueId;
    @FXML
    private TableColumn<?, ?> colFinMontant;
    @FXML
    private TableColumn<?, ?> colFinDate;

    @FXML
    private TableView<?> tableOffres;
    @FXML
    private TableColumn<?, ?> colOffreId;
    @FXML
    private TableColumn<?, ?> colOffreType;
    @FXML
    private TableColumn<?, ?> colOffreTaux;
    @FXML
    private TableColumn<?, ?> colOffreDuree;
    @FXML
    private TableColumn<?, ?> colOffreFinancementId;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnFinancement;
    @FXML
    private Button btnOffres;
    @FXML
    private Button btnNewFinancement;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnAddFinancement;
    @FXML
    private Button btnEditFinancement;
    @FXML
    private Button btnDeleteFinancement;
    @FXML
    private Button btnExportFinancement;
    @FXML
    private ComboBox<?> cmbProjetSelector;

    @FXML
    private void initialize() {
        // Init bindings and data loading here }
    }
}
