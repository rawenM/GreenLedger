package Controllers;

import Utils.ThemeManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * Base controller with common functionality like theme switching.
 * All your controllers can extend this class to inherit theme switching capability.
 */
public abstract class BaseController {
    
    @FXML
    protected ComboBox<String> themeSelector;
    
    /**
     * Call this in your controller's initialize() method after super.initialize()
     */
    protected void initializeThemeSelector() {
        if (themeSelector != null) {
            // Populate with theme options
            themeSelector.setItems(FXCollections.observableArrayList(
                ThemeManager.getInstance().getThemeDisplayNames()
            ));
            
            // Set current theme as selected
            String currentTheme = ThemeManager.getInstance().getCurrentTheme();
            themeSelector.setValue(ThemeManager.getInstance().getDisplayName(currentTheme));
            
            // Listen for changes
            themeSelector.setOnAction(event -> onThemeChange());
        }
    }
    
    /**
     * Handle theme change from ComboBox
     */
    @FXML
    protected void onThemeChange() {
        if (themeSelector == null || themeSelector.getValue() == null) {
            return;
        }
        
        String selectedTheme = ThemeManager.getInstance()
            .themeFromDisplayName(themeSelector.getValue());
        
        ThemeManager.getInstance().setTheme(selectedTheme);
    }
    
    /**
     * Override in child controllers for initialization logic
     */
    @FXML
    public void initialize() {
        // Base initialization
        initializeThemeSelector();
    }
}
