package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.GreenLedger.MainFX;

import java.io.IOException;

public class GreenWalletController extends BaseController {

    // Sidebar Buttons
    @FXML
    private Button btnWalletOverview;
    
    @FXML
    private Button btnOperations;
    
    @FXML
    private Button btnFinancing;
    
    @FXML
    private Button btnAddOperation;

    @FXML
    private Button btnSettings;

    // Stat Cards
    @FXML
    private Label lblTotalBalance;
    
    @FXML
    private Label lblTotalOperations;
    
    @FXML
    private Label lblPendingAmount;
    
    @FXML
    private Label lblValidatedAmount;

    // Search & Filter
    @FXML
    private TextField txtSearch;
    
    @FXML
    private ComboBox<String> filterStatus;
    
    @FXML
    private ComboBox<String> filterType;

    // Table
    @FXML
    private TableView<String> tableOperations;
    
    @FXML
    private TableColumn<String, String> colId;
    
    @FXML
    private TableColumn<String, String> colProjet;
    
    @FXML
    private TableColumn<String, String> colType;
    
    @FXML
    private TableColumn<String, String> colAmount;
    
    @FXML
    private TableColumn<String, String> colDate;
    
    @FXML
    private TableColumn<String, String> colStatut;
    
    @FXML
    private TableColumn<String, String> colAction;

    // Action Buttons
    @FXML
    private Button btnNewOperation;
    
    @FXML
    private Button btnExport;
    
    @FXML
    private Button btnRefresh;
    
    @FXML
    private Button btnExportData;

    // Content Pane
    @FXML
    private VBox contentPane;

    @FXML
    public void initialize() {
        super.initialize(); // Initialize theme selector from BaseController
        
        // TODO: Initialize wallet data
        setupListeners();
    }

    private void setupListeners() {
        btnWalletOverview.setOnAction(e -> showWalletOverview());
        btnOperations.setOnAction(e -> showOperations());
        btnFinancing.setOnAction(e -> showFinancing());
        btnAddOperation.setOnAction(e -> addNewOperation());
        btnExportData.setOnAction(e -> exportData());
        btnNewOperation.setOnAction(e -> addNewOperation());
        btnExport.setOnAction(e -> exportData());
        btnRefresh.setOnAction(e -> refreshData());
        if (btnSettings != null) {
            btnSettings.setOnAction(e -> showSettings());
        }
        
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> searchOperations(newVal));
        filterStatus.valueProperty().addListener((obs, oldVal, newVal) -> filterOperations());
        filterType.valueProperty().addListener((obs, oldVal, newVal) -> filterOperations());
    }

    private void showWalletOverview() {
        // TODO: Display wallet overview
    }

    private void showOperations() {
        // TODO: Display operations list
    }

    private void showFinancing() {
        // TODO: Display financing options
    }

    private void showSettings() {
        try {
            MainFX.setRoot("settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewOperation() {
        // TODO: Open dialog to add new operation
    }

    private void exportData() {
        // TODO: Export wallet data
    }

    private void refreshData() {
        // TODO: Refresh data from database
    }

    private void searchOperations(String query) {
        // TODO: Implement search functionality
    }

    private void filterOperations() {
        // TODO: Apply filters to table
    }
}
