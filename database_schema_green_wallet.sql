-- ==================== GREEN WALLET SYSTEM DATABASE SCHEMA ====================
-- MariaDB/MySQL compatible schema for carbon credit wallet management
-- Created for GreenLedger Carbon Accounting Platform

-- ==================== TABLE: green_wallets ====================
-- Stores carbon credit wallets for enterprises and banks
CREATE TABLE IF NOT EXISTS green_wallets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    wallet_number VARCHAR(50) UNIQUE NOT NULL,
    holder_name VARCHAR(150) NOT NULL,
    owner_type ENUM('ENTERPRISE', 'BANK') NOT NULL,
    owner_id INT NULL,  -- Reference to user/enterprise table
    available_credits DECIMAL(15,2) DEFAULT 0.00,  -- Credits available for use
    retired_credits DECIMAL(15,2) DEFAULT 0.00,    -- Credits permanently retired
    status ENUM('ACTIVE', 'PENDING_REVIEW', 'INACTIVE') DEFAULT 'ACTIVE',
    registry_id VARCHAR(100) NULL,  -- External registry identifier (optional)
    is_external BOOLEAN DEFAULT FALSE,  -- Internal vs external wallet
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_wallet_number (wallet_number),
    INDEX idx_owner_type (owner_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==================== TABLE: carbon_projects ====================
-- Extended carbon projects table (may already exist, this adds verification fields)
-- If table exists, run ALTER TABLE statements below instead
CREATE TABLE IF NOT EXISTS carbon_projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id INT NOT NULL,
    project_name VARCHAR(200) NOT NULL,
    description TEXT,
    estimated_reduction DECIMAL(15,2),  -- Estimated CO2 reduction in tons
    verified_reduction DECIMAL(15,2),   -- Actual verified reduction
    status ENUM('PENDING', 'VERIFIED', 'REJECTED', 'EXPIRED') DEFAULT 'PENDING',
    verification_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_enterprise (enterprise_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- If carbon_projects table already exists, add these columns:
-- ALTER TABLE carbon_projects ADD COLUMN verified_reduction DECIMAL(15,2) AFTER estimated_reduction;
-- ALTER TABLE carbon_projects ADD COLUMN verification_date TIMESTAMP NULL AFTER status;

-- ==================== TABLE: carbon_credit_batches ====================
-- Tracks batches of carbon credits issued from verified projects
CREATE TABLE IF NOT EXISTS carbon_credit_batches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    wallet_id INT NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,      -- Total credits in batch
    remaining_amount DECIMAL(15,2) NOT NULL,  -- Credits not yet retired
    status ENUM('AVAILABLE', 'PARTIALLY_RETIRED', 'FULLY_RETIRED') DEFAULT 'AVAILABLE',
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (project_id) REFERENCES carbon_projects(id) ON DELETE RESTRICT,
    FOREIGN KEY (wallet_id) REFERENCES green_wallets(id) ON DELETE RESTRICT,
    
    INDEX idx_wallet (wallet_id),
    INDEX idx_project (project_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==================== TABLE: wallet_transactions ====================
-- Immutable audit trail of all credit movements (issues, retirements, transfers)
CREATE TABLE IF NOT EXISTS wallet_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    wallet_id INT NOT NULL,
    batch_id INT NULL,  -- NULL for retirement transactions
    type ENUM('ISSUE', 'RETIRE', 'TRANSFER') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    reference_note TEXT,  -- Description/reason for transaction
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (wallet_id) REFERENCES green_wallets(id) ON DELETE RESTRICT,
    FOREIGN KEY (batch_id) REFERENCES carbon_credit_batches(id) ON DELETE RESTRICT,
    
    INDEX idx_wallet (wallet_id),
    INDEX idx_type (type),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==================== SAMPLE DATA FOR TESTING ====================

-- Sample Wallet 1: Enterprise Internal Wallet
INSERT INTO green_wallets (wallet_number, holder_name, owner_type, owner_id, available_credits, retired_credits, status, is_external)
VALUES ('GW-1000001', 'EcoTech Industries', 'ENTERPRISE', 1, 1250.50, 749.50, 'ACTIVE', FALSE);

-- Sample Wallet 2: Bank Internal Wallet
INSERT INTO green_wallets (wallet_number, holder_name, owner_type, owner_id, available_credits, retired_credits, status, is_external)
VALUES ('GW-1000002', 'Green Finance Bank', 'BANK', 2, 5000.00, 2000.00, 'ACTIVE', FALSE);

-- Sample Wallet 3: External Wallet Pending Review
INSERT INTO green_wallets (wallet_number, holder_name, owner_type, owner_id, available_credits, retired_credits, status, registry_id, is_external)
VALUES ('EXT-REGISTRY-789', 'Carbon Registry Co.', 'ENTERPRISE', NULL, 0.00, 0.00, 'PENDING_REVIEW', 'REG-789-EXTERNAL', TRUE);

-- Sample Carbon Project (ensure this exists or adjust IDs)
INSERT INTO carbon_projects (id, enterprise_id, project_name, description, estimated_reduction, verified_reduction, status, verification_date)
VALUES (1, 1, 'Solar Farm Installation', 'Large-scale solar energy project reducing grid emissions', 5000.00, 4500.00, 'VERIFIED', NOW())
ON DUPLICATE KEY UPDATE verified_reduction = 4500.00, status = 'VERIFIED';

-- Sample Credit Batch
INSERT INTO carbon_credit_batches (project_id, wallet_id, total_amount, remaining_amount, status)
VALUES (1, 1, 2000.00, 1250.50, 'PARTIALLY_RETIRED');

-- Sample Transactions
INSERT INTO wallet_transactions (wallet_id, batch_id, type, amount, reference_note)
VALUES 
    (1, 1, 'ISSUE', 2000.00, 'Initial credit issuance from Solar Farm Project verification'),
    (1, 1, 'RETIRE', 749.50, 'Offset for Q1 2026 corporate carbon emissions');

-- ==================== USEFUL QUERIES ====================

-- View wallet summary with credit details
/*
SELECT 
    w.wallet_number,
    w.holder_name,
    w.owner_type,
    w.available_credits,
    w.retired_credits,
    (w.available_credits + w.retired_credits) AS total_credits,
    w.status,
    COUNT(DISTINCT t.id) AS transaction_count
FROM green_wallets w
LEFT JOIN wallet_transactions t ON w.id = t.wallet_id
GROUP BY w.id
ORDER BY w.created_at DESC;
*/

-- View credit traceability (project → batch → wallet → retirement)
/*
SELECT 
    cp.project_name,
    b.total_amount AS batch_total,
    b.remaining_amount AS batch_remaining,
    w.wallet_number,
    w.holder_name,
    t.type AS transaction_type,
    t.amount AS transaction_amount,
    t.created_at AS transaction_date
FROM carbon_credit_batches b
INNER JOIN carbon_projects cp ON b.project_id = cp.id
INNER JOIN green_wallets w ON b.wallet_id = w.id
LEFT JOIN wallet_transactions t ON t.batch_id = b.id
ORDER BY cp.id, b.issued_at, t.created_at;
*/

-- ==================== NOTES ====================
-- 1. All credit amounts are in metric tons CO₂ equivalent (tCO₂e)
-- 2. Credits can only be issued from VERIFIED projects
-- 3. Retired credits are permanently removed from circulation
-- 4. External wallets require admin approval before becoming ACTIVE
-- 5. Transaction history is immutable (no DELETE or UPDATE allowed on wallet_transactions)
-- 6. Batch retirement follows FIFO (First In, First Out) principle
