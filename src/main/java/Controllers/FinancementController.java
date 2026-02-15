package Controllers;

import Models.FinancementOffre;
import Services.FinancementOffreService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FinancementController {

    @FXML private TableView<FinancementOffre> tableFusion;
    @FXML private TableColumn<FinancementOffre, Integer> colFinId;
    @FXML private TableColumn<FinancementOffre, Integer> colFinProjetId;
    @FXML private TableColumn<FinancementOffre, Integer> colFinBanqueId;
    @FXML private TableColumn<FinancementOffre, Double> colFinMontant;
    @FXML private TableColumn<FinancementOffre, String> colFinDate;

    @FXML private TableColumn<FinancementOffre, Integer> colOffreId;
    @FXML private TableColumn<FinancementOffre, String> colOffreType;
    @FXML private TableColumn<FinancementOffre, Double> colOffreTaux;
    @FXML private TableColumn<FinancementOffre, Integer> colOffreDuree;

    @FXML private Button btnRefresh;
    @FXML private Button btnAddFinancement;
    @FXML private Button btnEditFinancement;
    @FXML private Button btnDeleteFinancement;

    @FXML private TextField txtProjetId;
    @FXML private TextField txtBanqueId;
    @FXML private TextField txtMontant;
    @FXML private TextField txtDateFinancement;

    @FXML private TextField txtTypeOffre;
    @FXML private TextField txtTaux;
    @FXML private TextField txtDuree;

    private final FinancementOffreService fusionService = new FinancementOffreService();

    @FXML
    private void initialize() {
        colFinId.setCellValueFactory(cd -> new SimpleIntegerProperty(nullSafeInt(cd.getValue().getFinancementId())).asObject());
        colFinProjetId.setCellValueFactory(cd -> new SimpleIntegerProperty(nullSafeInt(cd.getValue().getProjetId())).asObject());
        colFinBanqueId.setCellValueFactory(cd -> new SimpleIntegerProperty(nullSafeInt(cd.getValue().getBanqueId())).asObject());
        colFinMontant.setCellValueFactory(cd -> new SimpleDoubleProperty(nullSafeDouble(cd.getValue().getMontant())).asObject());
        colFinDate.setCellValueFactory(cd -> new SimpleStringProperty(nullSafeString(cd.getValue().getDateFinancement())));

        colOffreId.setCellValueFactory(cd -> new SimpleIntegerProperty(nullSafeInt(cd.getValue().getOffreId())).asObject());
        colOffreType.setCellValueFactory(cd -> new SimpleStringProperty(nullSafeString(cd.getValue().getTypeOffre())));
        colOffreTaux.setCellValueFactory(cd -> new SimpleDoubleProperty(nullSafeDouble(cd.getValue().getTaux())).asObject());
        colOffreDuree.setCellValueFactory(cd -> new SimpleIntegerProperty(nullSafeInt(cd.getValue().getDuree())).asObject());

        loadAll();

        tableFusion.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                txtProjetId.setText(String.valueOf(nullSafeInt(n.getProjetId())));
                txtBanqueId.setText(String.valueOf(nullSafeInt(n.getBanqueId())));
                txtMontant.setText(String.valueOf(nullSafeDouble(n.getMontant())));
                txtDateFinancement.setText(nullSafeString(n.getDateFinancement()));

                txtTypeOffre.setText(nullSafeString(n.getTypeOffre()));
                txtTaux.setText(String.valueOf(nullSafeDouble(n.getTaux())));
                txtDuree.setText(String.valueOf(nullSafeInt(n.getDuree())));
            }
        });
    }

    @FXML
    private void ajouterFinancement() {
        try {
            FinancementOffre data = buildFromForm(null);
            fusionService.add(data);
            loadAll();
            clearFields();
        } catch (NumberFormatException ex) {
            showError("Valeurs invalides", "Veuillez verifier les champs numeriques.");
        }
    }

    @FXML
    private void modifierFinancement() {
        FinancementOffre selected = tableFusion.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection requise", "Choisissez une ligne a modifier.");
            return;
        }
        try {
            FinancementOffre data = buildFromForm(selected);
            fusionService.update(data);
            loadAll();
        } catch (NumberFormatException ex) {
            showError("Valeurs invalides", "Veuillez verifier les champs numeriques.");
        }
    }

    @FXML
    private void supprimerFinancement() {
        FinancementOffre selected = tableFusion.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection requise", "Choisissez une ligne a supprimer.");
            return;
        }
        fusionService.delete(selected);
        loadAll();
        clearFields();
    }

    @FXML
    private void refreshAll() {
        loadAll();
    }

    private FinancementOffre buildFromForm(FinancementOffre selected) {
        FinancementOffre data = new FinancementOffre();
        if (selected != null) {
            data.setFinancementId(selected.getFinancementId());
            data.setOffreId(selected.getOffreId());
        }
        data.setProjetId(Integer.parseInt(txtProjetId.getText()));
        data.setBanqueId(Integer.parseInt(txtBanqueId.getText()));
        data.setMontant(Double.parseDouble(txtMontant.getText()));
        data.setDateFinancement(txtDateFinancement.getText());

        data.setTypeOffre(txtTypeOffre.getText());
        data.setTaux(Double.parseDouble(txtTaux.getText()));
        data.setDuree(Integer.parseInt(txtDuree.getText()));
        return data;
    }

    private void loadAll() {
        tableFusion.setItems(FXCollections.observableArrayList(fusionService.getAll()));
    }

    private void clearFields() {
        txtProjetId.clear();
        txtBanqueId.clear();
        txtMontant.clear();
        txtDateFinancement.clear();
        txtTypeOffre.clear();
        txtTaux.clear();
        txtDuree.clear();
    }

    private int nullSafeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private double nullSafeDouble(Double value) {
        return value == null ? 0.0 : value;
    }

    private String nullSafeString(String value) {
        return value == null ? "" : value;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
