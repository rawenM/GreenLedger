package Services;

import DataBase.MyConnection;
import Models.Budget;
import Models.Projet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetService {

    private final Connection cnx = MyConnection.getConnection();

    private static final String SELECT_BASE =
            "SELECT p.id, p.entreprise_id, p.titre, p.description, p.statut, p.score_esg, " +
                    "       p.company_address, p.company_email, p.company_phone, " +
                    "       b.id_budget, b.montant, b.raison, b.devise " +
                    "FROM projet p " +
                    "LEFT JOIN budget b ON b.id_projet = p.id ";

    public List<Projet> afficher() {
        String sql = SELECT_BASE + "ORDER BY p.date_creation DESC";
        return fetchList(sql, null);
    }

    public List<Projet> getByEntreprise(int entrepriseId) {
        String sql = SELECT_BASE + "WHERE p.entreprise_id=? ORDER BY p.date_creation DESC";
        return fetchList(sql, ps -> ps.setInt(1, entrepriseId));
    }

    public Projet getById(int idProjet) {
        String sql = SELECT_BASE + "WHERE p.id=?";
        List<Projet> list = fetchList(sql, ps -> ps.setInt(1, idProjet));
        return list.isEmpty() ? null : list.get(0);
    }

    public void insert(Projet p) {
        if (p == null) return;

        Budget b = p.getBudgetObj();
        if (b == null) {
            b = new Budget();
            b.setMontant(p.getBudget());     // ✅ getBudget() = double
            b.setRaison("Budget initial");
            b.setDevise("TND");
        }
        b.setDevise(normalizeDevise(b.getDevise()));

        String sqlProjet =
                "INSERT INTO projet (entreprise_id, titre, description, statut, score_esg, company_address, company_email, company_phone) " +
                        "VALUES (?,?,?,?,?,?,?,?)";

        String sqlBudget =
                "INSERT INTO budget (montant, raison, devise, id_projet) VALUES (?,?,?,?)";

        try {
            cnx.setAutoCommit(false);

            int newId;
            try (PreparedStatement ps = cnx.prepareStatement(sqlProjet, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, p.getEntrepriseId());
                ps.setString(2, p.getTitre());
                ps.setString(3, p.getDescription());
                ps.setString(4, p.getStatut());

                // score ESG: toujours NULL côté entreprise
                ps.setNull(5, Types.INTEGER);

                ps.setString(6, p.getCompanyAddress());
                ps.setString(7, p.getCompanyEmail());
                ps.setString(8, p.getCompanyPhone());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Insertion projet: aucune clé générée");
                    newId = keys.getInt(1);
                }
            }

            try (PreparedStatement psB = cnx.prepareStatement(sqlBudget)) {
                psB.setDouble(1, b.getMontant());
                psB.setString(2, b.getRaison());
                psB.setString(3, b.getDevise());
                psB.setInt(4, newId);
                psB.executeUpdate();
            }

            cnx.commit();
        } catch (SQLException e) {
            rollbackQuietly();
            System.out.println("Erreur insert projet+budget: " + e.getMessage());
        } finally {
            setAutoCommitQuietly(true);
        }
    }

    public void update(Projet p) {
        if (p == null) return;

        String currentStatut = getStatutById(p.getId());
        boolean isDraft = "DRAFT".equalsIgnoreCase(currentStatut);

        if (!isDraft) {
            updateDescriptionOnly(
                    p.getId(),
                    p.getDescription(),
                    p.getCompanyAddress(),
                    p.getCompanyEmail(),
                    p.getCompanyPhone()
            );
            return;
        }

        Budget b = p.getBudgetObj();
        if (b == null) {
            b = new Budget();
            b.setMontant(p.getBudget());
            b.setRaison("Budget initial");
            b.setDevise("TND");
        }
        b.setDevise(normalizeDevise(b.getDevise()));

        String sqlProjet =
                "UPDATE projet SET titre=?, description=?, company_address=?, company_email=?, company_phone=? WHERE id=?";

        String sqlBudget =
                "UPDATE budget SET montant=?, raison=?, devise=? WHERE id_projet=?";

        try {
            cnx.setAutoCommit(false);

            try (PreparedStatement ps = cnx.prepareStatement(sqlProjet)) {
                ps.setString(1, p.getTitre());
                ps.setString(2, p.getDescription());
                ps.setString(3, p.getCompanyAddress());
                ps.setString(4, p.getCompanyEmail());
                ps.setString(5, p.getCompanyPhone());
                ps.setInt(6, p.getId());
                ps.executeUpdate();
            }

            int updated;
            try (PreparedStatement psB = cnx.prepareStatement(sqlBudget)) {
                psB.setDouble(1, b.getMontant());
                psB.setString(2, b.getRaison());
                psB.setString(3, b.getDevise());
                psB.setInt(4, p.getId());
                updated = psB.executeUpdate();
            }

            if (updated == 0) {
                String insertBudget = "INSERT INTO budget (montant, raison, devise, id_projet) VALUES (?,?,?,?)";
                try (PreparedStatement psIns = cnx.prepareStatement(insertBudget)) {
                    psIns.setDouble(1, b.getMontant());
                    psIns.setString(2, b.getRaison());
                    psIns.setString(3, b.getDevise());
                    psIns.setInt(4, p.getId());
                    psIns.executeUpdate();
                }
            }

            cnx.commit();
        } catch (SQLException e) {
            rollbackQuietly();
            System.out.println("Erreur update projet: " + e.getMessage());
        } finally {
            setAutoCommitQuietly(true);
        }
    }

    public void updateDescriptionOnly(int id, String description, String address, String email, String phone) {
        String sql = "UPDATE projet SET description=?, company_address=?, company_email=?, company_phone=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setString(2, address);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur updateDescriptionOnly: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM projet WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur delete projet: " + e.getMessage());
        }
    }

    public void cancel(int id) {
        String sql = "UPDATE projet SET statut='CANCELLED' WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur cancel projet: " + e.getMessage());
        }
    }

    public boolean updateStatut(int idProjet, String statut) {
        String current = getStatutById(idProjet);
        if (current != null && !"DRAFT".equalsIgnoreCase(current) && "DRAFT".equalsIgnoreCase(statut)) {
            return false;
        }

        String sql = "UPDATE projet SET statut=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, statut);
            ps.setInt(2, idProjet);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur updateStatut: " + e.getMessage());
            return false;
        }
    }

    public String getStatutById(int idProjet) {
        String sql = "SELECT statut FROM projet WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idProjet);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("statut");
            }
        } catch (SQLException e) {
            System.out.println("Erreur getStatutById: " + e.getMessage());
        }
        return null;
    }

    // -------------------------
    // Helpers
    // -------------------------
    private interface StatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Projet> fetchList(String sql, StatementBinder binder) {
        List<Projet> list = new ArrayList<>();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            if (binder != null) binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Projet p = new Projet(
                            rs.getInt("id"),
                            rs.getInt("entreprise_id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            (Integer) rs.getObject("score_esg"),
                            rs.getString("statut"),
                            rs.getString("company_address"),
                            rs.getString("company_email"),
                            rs.getString("company_phone")
                    );

                    Integer idBudget = (Integer) rs.getObject("id_budget");
                    if (idBudget != null) {
                        Budget b = new Budget();
                        b.setIdBudget(idBudget);
                        b.setMontant(rs.getDouble("montant"));
                        b.setRaison(rs.getString("raison"));
                        b.setDevise(rs.getString("devise"));
                        b.setIdProjet(p.getId());
                        p.setBudget(b); // ✅ setBudget(Budget)
                    }

                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur fetch projets: " + e.getMessage());
        }
        return list;
    }

    private String normalizeDevise(String devise) {
        if (devise == null) return "TND";
        String d = devise.trim().toUpperCase();
        return (d.equals("TND") || d.equals("EUR") || d.equals("USD")) ? d : "TND";
    }

    private void rollbackQuietly() {
        try { cnx.rollback(); } catch (Exception ignored) {}
    }

    private void setAutoCommitQuietly(boolean value) {
        try { cnx.setAutoCommit(value); } catch (Exception ignored) {}
    }
}