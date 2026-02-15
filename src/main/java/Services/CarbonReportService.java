package Services;

import dao.CarbonReportDAOImpl;
import dao.ICarbonReportDAO;
import Models.CarbonReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des rapports d'émissions carbone
 * Permet à l'Expert Carbone d'évaluer et valider les projets
 */
public class CarbonReportService {

    private final ICarbonReportDAO reportDAO = new CarbonReportDAOImpl();

    // Créer un nouveau rapport (porteur de projet soumet une demande d'évaluation)
    public CarbonReport createReport(Long projectId, String projectName, Long porteurProjetId, String porteurProjetName) {
        CarbonReport report = new CarbonReport(projectId, projectName, porteurProjetId, porteurProjetName);
        return reportDAO.save(report);
    }

    // L'expert carbone évalue et valide le rapport
    public CarbonReport validateReport(Long reportId, Long expertCarboneId, Double emissionsEstimate,
                                      String evaluationDetails, String commentaires) {
        Optional<CarbonReport> reportOpt = reportDAO.findById(reportId);
        if (reportOpt.isEmpty()) {
            System.err.println("[CLEAN] Rapport carbone non trouvé");
            return null;
        }

        CarbonReport report = reportOpt.get();
        report.setExpertCarboneId(expertCarboneId);
        report.setEmissionsEstimate(emissionsEstimate);
        report.setEvaluationDetails(evaluationDetails);
        report.setCommentairesExpert(commentaires);
        report.setStatut(CarbonReport.StatutRapport.VALIDE);
        report.setDateValidation(LocalDateTime.now());

        CarbonReport updated = reportDAO.update(report);
        System.out.println("[CLEAN] Rapport carbone validé: " + report.getProjectName());
        return updated;
    }

    // L'expert carbone rejette le rapport (demande modifications)
    public CarbonReport rejectReport(Long reportId, Long expertCarboneId, String motifRejet) {
        Optional<CarbonReport> reportOpt = reportDAO.findById(reportId);
        if (reportOpt.isEmpty()) {
            System.err.println("[CLEAN] Rapport carbone non trouvé");
            return null;
        }

        CarbonReport report = reportOpt.get();
        report.setExpertCarboneId(expertCarboneId);
        report.setCommentairesExpert(motifRejet);
        report.setStatut(CarbonReport.StatutRapport.REJECTE);
        report.setDateValidation(LocalDateTime.now());

        CarbonReport updated = reportDAO.update(report);
        System.out.println("[CLEAN] Rapport carbone rejeté: " + report.getProjectName());
        return updated;
    }

    // Récupérer tous les rapports
    public List<CarbonReport> getAllReports() {
        return reportDAO.findAll();
    }

    // Récupérer les rapports en attente d'évaluation
    public List<CarbonReport> getPendingReports() {
        return reportDAO.findPendingReports();
    }

    // Récupérer les rapports validés
    public List<CarbonReport> getValidatedReports() {
        return reportDAO.findByStatut(CarbonReport.StatutRapport.VALIDE);
    }

    // Récupérer les rapports rejetés
    public List<CarbonReport> getRejectedReports() {
        return reportDAO.findByStatut(CarbonReport.StatutRapport.REJECTE);
    }

    // Récupérer rapport par ID
    public Optional<CarbonReport> getReportById(Long id) {
        return reportDAO.findById(id);
    }

    // Récupérer rapports d'un projet
    public List<CarbonReport> getReportsByProject(Long projectId) {
        return reportDAO.findByProjectId(projectId);
    }

    // Récupérer rapports d'un porteur de projet
    public List<CarbonReport> getReportsByPorteurProjet(Long porteurProjetId) {
        return reportDAO.findByPorteurProjetId(porteurProjetId);
    }

    // Statistiques
    public long getPendingCount() {
        return reportDAO.countByStatut(CarbonReport.StatutRapport.EN_ATTENTE);
    }

    public long getValidatedCount() {
        return reportDAO.countByStatut(CarbonReport.StatutRapport.VALIDE);
    }

    public long getRejectedCount() {
        return reportDAO.countByStatut(CarbonReport.StatutRapport.REJECTE);
    }

    public long getTotalCount() {
        return reportDAO.count();
    }

    // Moyennes d'émissions (neutre, basée sur rapports validés)
    public Double getAverageEmissions() {
        List<CarbonReport> validated = getValidatedReports();
        if (validated.isEmpty()) return 0.0;

        double total = validated.stream()
                .mapToDouble(r -> r.getEmissionsEstimate() != null ? r.getEmissionsEstimate() : 0)
                .sum();
        return total / validated.size();
    }
}

