package Utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * EmailService simple sans dépendance externe.
 * - Lit la configuration depuis les variables d'environnement ou config.properties.
 * - Si SMTP est configuré, la méthode sendResetEmail fait seulement la trace (fallback).
 * - Cette version garantit la compilation même si Jakarta Mail n'est pas disponible.
 */
public class EmailService {

    private final Properties smtpProps = new Properties();
    private boolean configured = false;

    public EmailService() {
        // Charger depuis variables d'environnement en priorité
        String envHost = System.getenv("SMTP_HOST");
        if (envHost != null && !envHost.isEmpty()) {
            smtpProps.put("smtp.host", envHost);
            String envPort = System.getenv("SMTP_PORT"); if (envPort != null) smtpProps.put("smtp.port", envPort);
            String envUser = System.getenv("SMTP_USERNAME"); if (envUser != null) smtpProps.put("smtp.username", envUser);
            String envPass = System.getenv("SMTP_PASSWORD"); if (envPass != null) smtpProps.put("smtp.password", envPass);
            String envFrom = System.getenv("SMTP_FROM"); if (envFrom != null) smtpProps.put("smtp.from", envFrom);
            smtpProps.put("smtp.auth", System.getenv().getOrDefault("SMTP_AUTH", "true"));
            smtpProps.put("smtp.starttls.enable", System.getenv().getOrDefault("SMTP_STARTTLS", "true"));
            smtpProps.put("app.reset.url.prefix", System.getenv().getOrDefault("APP_RESET_URL_PREFIX", ""));
            configured = true;
            return;
        }
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
            if (in == null) return;
            smtpProps.load(in);
            // Vérifier qu'on a au moins host et from
            if (smtpProps.getProperty("smtp.host") != null && smtpProps.getProperty("smtp.from") != null) {
                configured = true;
            }
        } catch (Exception e) {
            System.err.println("[CLEAN] Impossible de charger config.properties pour EmailService: " + e.getMessage());
        }
    }

    public boolean isConfigured() {
        return configured;
    }

    /**
     * Méthode fallback : n'envoie pas réellement d'email mais loggue le contenu.
     * Si vous souhaitez l'envoi réel, remplacez cette implémentation par une utilisant Jakarta Mail
     * ou une autre librairie SMTP et configurez correctement `config.properties` ou les variables d'environnement.
     */
    public boolean sendResetEmail(String to, String token) {
        System.out.println("[CLEAN] (Simulé) Envoi d'email à: " + to);
        String prefix = smtpProps.getProperty("app.reset.url.prefix", "");
        if (!prefix.isEmpty()) {
            System.out.println("🔗 Lien de réinitialisation: " + prefix + token);
        } else {
            System.out.println("[CLEAN] Token: " + token);
        }
        // Retourner false pour indiquer que l'envoi réel n'a pas été effectué; le code caller utilisera le fallback console.
        return false;
    }
}
