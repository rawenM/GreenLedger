package Controllers;

import Models.Budget;
import Models.Projet;
import Models.TypeUtilisateur;
import Models.User;
import Services.ProjetService;
import Utils.NavigationContext;
import Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.GreenLedger.MainFX;

public class ProjetCreateController {

    private static final int TEST_ENTREPRISE_ID = 1;
    private final ProjetService service = new ProjetService();

    @FXML private TextField tfTitre;
    @FXML private TextField tfBudgetMontant;
    @FXML private ComboBox<String> cbBudgetDevise;
    @FXML private TextArea taBudgetRaison;
    @FXML private TextField tfCompanyAddress;
    @FXML private TextField tfCompanyEmail;
    @FXML private TextField tfCompanyPhone;
    @FXML private TextArea taDescription;

    @FXML
    public void initialize() {
        if (cbBudgetDevise != null) {
            cbBudgetDevise.getItems().setAll("TND", "EUR", "USD");
            cbBudgetDevise.setValue("TND");
        }
    }

    @FXML
    private void onBack() {
        goHome();
    }

    @FXML
    private void onSaveDraft() {
        createWithStatus("DRAFT");
    }

    @FXML
    private void onAdd() {
        createWithStatus("SUBMITTED");
    }

    private void createWithStatus(String statut) {
        String titre = safe(tfTitre.getText());
        if (titre.length() < 3) { error("Titre: min 3 caractères."); return; }

        double montant;
        try {
            montant = Double.parseDouble(safe(tfBudgetMontant.getText()));
            if (montant <= 0) throw new Exception();
        } catch (Exception e) {
            error("Budget invalide (>0).");
            return;
        }

        String raison = safe(taBudgetRaison != null ? taBudgetRaison.getText() : null);
        if (raison.length() < 3) { error("Raison budget: min 3 caractères."); return; }

        String devise = (cbBudgetDevise != null && cbBudgetDevise.getValue() != null)
                ? cbBudgetDevise.getValue()
                : "TND";

        Budget budget = new Budget();
        budget.setMontant(montant);
        budget.setRaison(raison);
        budget.setDevise(devise);

        Projet p = new Projet();
        p.setEntrepriseId(resolveEntrepriseId());
        p.setTitre(titre);
        p.setBudget(budget);
        p.setDescription(taDescription.getText());
        p.setStatut(statut);


        p.setScoreEsg(null);

        p.setCompanyAddress(emptyToNull(tfCompanyAddress.getText()));
        p.setCompanyEmail(emptyToNull(tfCompanyEmail.getText()));
        p.setCompanyPhone(emptyToNull(tfCompanyPhone.getText()));

        service.insert(p);
        goHome();
    }

    private void goHome() {
        try {
            MainFX.setRoot(resolveBackTarget());
        } catch (Exception ex) {
            error("Navigation impossible: " + ex.getMessage());
        }
    }

    private String resolveBackTarget() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null && user.getTypeUtilisateur() == TypeUtilisateur.PORTEUR_PROJET) {
            return "fxml/dashboard";
        }
        String previous = NavigationContext.getInstance().getPreviousPage();
        if (previous != null && !previous.isBlank()) return previous;
        return "GestionProjet";
    }

    private int resolveEntrepriseId() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null && user.getId() != null) return user.getId().intValue();
        return TEST_ENTREPRISE_ID;
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }

    private String emptyToNull(String s) {
        String v = safe(s);
        return v.isEmpty() ? null : v;
    }

    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}