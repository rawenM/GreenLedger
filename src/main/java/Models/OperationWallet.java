package Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a transaction in the wallet - either credit issuance or retirement.
 * This creates an immutable audit trail of all credit movements.
 */
public class OperationWallet {
    
    private int id;
    private int walletId;                 // Wallet affected by this transaction
    private Integer batchId;              // Credit batch involved (nullable for some ops)
    private String type;                  // ISSUE, RETIRE, TRANSFER
    private BigDecimal amount;            // Number of credits
    private String referenceNote;         // Description/reason for transaction
    private LocalDateTime createdAt;

    // Constructors
    public OperationWallet() {}

    public OperationWallet(int walletId, String type, BigDecimal amount, String referenceNote) {
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.referenceNote = referenceNote;
    }

    public OperationWallet(int walletId, Integer batchId, String type, BigDecimal amount, String referenceNote) {
        this(walletId, type, amount, referenceNote);
        this.batchId = batchId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReferenceNote() {
        return referenceNote;
    }

    public void setReferenceNote(String referenceNote) {
        this.referenceNote = referenceNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("Transaction[%s - %.2f credits - %s]", 
            type, amount, referenceNote);
    }
}

