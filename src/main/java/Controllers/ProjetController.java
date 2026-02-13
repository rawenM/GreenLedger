package Controllers;

import Models.Projet;
import Services.ProjetService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProjetController {

    // ✅ Test sans login
    private static final int TEST_ENTREPRISE_ID = 1;

    private final ProjetService service = new ProjetService();
    private final ObservableList<Projet> data = FXCollections.observableArrayList();
    private Projet selected;

    @FXML private TableView<Projet> table;
    @FXML private TableColumn<Projet, Number> colId;
    @FXML private TableColumn<Projet, String> colTitre;
    @FXML private TableColumn<Projet, String> colStatut;
    @FXML private TableColumn<Projet, Number> colBudget;

    @FXML private TextField tfTitre;
    @FXML private TextArea taDescription;
    @FXML private TextField tfBudget;
    @FXML private ComboBox<String> cbStatut;
    @FXML private TextField tfScoreEsg;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(v -> new SimpleIntegerProperty(v.getValue().getId()));
        colTitre.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getTitre()));
        colStatut.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getStatut()));
        colBudget.setCellValueFactory(v -> new SimpleDoubleProperty(v.getValue().getBudget()));

        cbStatut.setItems(FXCollections.observableArrayList(
                "DRAFT","SUBMITTED","IN_PROGRESS","COMPLETED","CANCELLED"
        ));
        cbStatut.setValue("DRAFT");

        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            selected = p;
            if (p != null) fillForm(p);
        });

        refresh();
        clearForm();
    }

    @FXML private void onNew() {
        selected = null;
        table.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML private void onSave() {
        String titre = tfTitre.getText() == null ? "" : tfTitre.getText().trim();
        if (titre.length() < 3) { error("Titre: min 3 caractères."); return; }

        double budget;
        try {
            budget = Double.parseDouble(tfBudget.getText().trim());
            if (budget <= 0) throw new Exception();
        } catch (Exception e) { error("Budget invalide (>0)."); return; }

        int score;
        try {
            score = Integer.parseInt(tfScoreEsg.getText().trim());
            if (score < 0 || score > 100) throw new Exception();
        } catch (Exception e) { error("Score ESG invalide (0..100)."); return; }

        String statut = cbStatut.getValue() == null ? "DRAFT" : cbStatut.getValue();

        try {
            if (selected == null) {
                Projet p = new Projet();
                p.setEntrepriseId(TEST_ENTREPRISE_ID);
                p.setTitre(titre);
                p.setDescription(taDescription.getText());
                p.setBudget(budget);
                p.setScoreEsg(score);
                p.setStatut(statut);
                service.insert(p);
            } else {
                selected.setTitre(titre);
                selected.setDescription(taDescription.getText());
                selected.setBudget(budget);
                selected.setScoreEsg(score);
                selected.setStatut(statut);
                service.update(selected);
            }
            refresh();
            onNew();
        } catch (Exception ex) {
            error("DB: " + ex.getMessage());
        }
    }

    @FXML private void onDelete() {
        Projet p = table.getSelectionModel().getSelectedItem();
        if (p == null) { error("Sélectionne un projet."); return; }
        if (!confirm("Supprimer projet #" + p.getId() + " ?")) return;

        try {
            service.delete(p.getId());
            refresh();
            onNew();
        } catch (Exception ex) {
            error("DB: " + ex.getMessage());
        }
    }

    @FXML private void onRefresh() { refresh(); }

    private void refresh() {
        try {
            data.setAll(service.getByEntreprise(TEST_ENTREPRISE_ID));
        } catch (Exception ex) {
            error("DB: " + ex.getMessage());
        }
    }

    private void fillForm(Projet p) {
        tfTitre.setText(p.getTitre());
        taDescription.setText(p.getDescription());
        tfBudget.setText(String.valueOf(p.getBudget()));
        tfScoreEsg.setText(String.valueOf(p.getScoreEsg()));
        cbStatut.setValue(p.getStatut());
    }

    private void clearForm() {
        tfTitre.clear();
        taDescription.clear();
        tfBudget.setText("0");
        tfScoreEsg.setText("0");
        cbStatut.setValue("DRAFT");
    }

    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
