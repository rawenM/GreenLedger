package Models;

public class Projet {
    private int id;
    private int entrepriseId;
    private String titre;
    private String description;
    private double budget;
    private int scoreEsg;
    private String statut; // correspond à la colonne "statut" en DB

    public Projet() {}

    public Projet(int id, int entrepriseId, String titre, String description, double budget, int scoreEsg, String statut) {
        this.id = id;
        this.entrepriseId = entrepriseId;
        this.titre = titre;
        this.description = description;
        this.budget = budget;
        this.scoreEsg = scoreEsg;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEntrepriseId() { return entrepriseId; }
    public void setEntrepriseId(int entrepriseId) { this.entrepriseId = entrepriseId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public int getScoreEsg() { return scoreEsg; }
    public void setScoreEsg(int scoreEsg) { this.scoreEsg = scoreEsg; }

    // --- champs DB ---
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    // --- ALIAS compatibilité (pour tes controllers existants) ---
    // Tes controllers appellent getStatutEvaluation() + PropertyValueFactory("statutEvaluation")
    public String getStatutEvaluation() { return statut; }
    public void setStatutEvaluation(String statutEvaluation) { this.statut = statutEvaluation; }
}
