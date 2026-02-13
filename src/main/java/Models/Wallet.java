package Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a Green Wallet that holds carbon credits.
 * Can be either an internal wallet (managed by system) or external (linked to registry).
 */
public class Wallet {
    
    private int id;
    private Integer walletNumber;        // Wallet number (INT in database)
    private String name;                  // Wallet display name
    private String ownerType;             // ENTERPRISE or BANK (varchar)
    private int ownerId;                  // Reference to user/enterprise record (NOT NULL)
    private double availableCredits;      // Credits available for use (double)
    private double retiredCredits;        // Credits permanently retired (double)
    private LocalDateTime createdAt;

    // Constructors
    public Wallet() {
        this.availableCredits = 0.0;
        this.retiredCredits = 0.0;
    }

    public Wallet(String ownerType, int ownerId) {
        this();
        this.ownerType = ownerType;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(Integer walletNumber) {
        this.walletNumber = walletNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public double getAvailableCredits() {
        return availableCredits;
    }

    public void setAvailableCredits(double availableCredits) {
        this.availableCredits = availableCredits;
    }

    public double getRetiredCredits() {
        return retiredCredits;
    }

    public void setRetiredCredits(double retiredCredits) {
        this.retiredCredits = retiredCredits;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Utility methods
    public double getTotalCredits() {
        return availableCredits + retiredCredits;
    }

    @Override
    public String toString() {
        return String.format("Wallet[%d - %s - %s: %.2f credits available]", 
            walletNumber, name != null ? name : "Unnamed", ownerType, availableCredits);
    }
}
