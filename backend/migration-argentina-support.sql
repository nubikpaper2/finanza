-- Migración: Soporte para Argentina - Tarjetas, Tipos de Cambio e Impuestos
-- Fecha: 2025-11-11

-- 1. Tabla de tipos de cambio
CREATE TABLE IF NOT EXISTS exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    rate_type VARCHAR(20) NOT NULL,
    buy_rate DECIMAL(15,4) NOT NULL,
    sell_rate DECIMAL(15,4) NOT NULL,
    organization_id BIGINT REFERENCES organizations(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_exchange_rate UNIQUE (date, rate_type, organization_id)
);

CREATE INDEX idx_exchange_rates_org_date ON exchange_rates(organization_id, date);
CREATE INDEX idx_exchange_rates_type ON exchange_rates(rate_type);

-- 2. Tabla de tarjetas de crédito
CREATE TABLE IF NOT EXISTS credit_cards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_four_digits VARCHAR(4),
    closing_day INTEGER NOT NULL CHECK (closing_day BETWEEN 1 AND 31),
    due_day INTEGER NOT NULL CHECK (due_day BETWEEN 1 AND 31),
    credit_limit DECIMAL(15,2),
    currency VARCHAR(10) DEFAULT 'ARS',
    bank VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    account_id BIGINT REFERENCES accounts(id) ON DELETE SET NULL,
    organization_id BIGINT REFERENCES organizations(id) ON DELETE CASCADE,
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_cards_org ON credit_cards(organization_id);
CREATE INDEX idx_credit_cards_active ON credit_cards(active);

-- 3. Tabla de cuotas de tarjetas de crédito
CREATE TABLE IF NOT EXISTS credit_card_installments (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    credit_card_id BIGINT NOT NULL REFERENCES credit_cards(id) ON DELETE CASCADE,
    installment_number INTEGER NOT NULL,
    total_installments INTEGER NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    due_date DATE NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    paid_date DATE,
    organization_id BIGINT REFERENCES organizations(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_installments_transaction ON credit_card_installments(transaction_id);
CREATE INDEX idx_installments_card ON credit_card_installments(credit_card_id);
CREATE INDEX idx_installments_due_date ON credit_card_installments(due_date);
CREATE INDEX idx_installments_paid ON credit_card_installments(paid);

-- 4. Tabla de impuestos argentinos
CREATE TABLE IF NOT EXISTS argentina_taxes (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    tax_type VARCHAR(30) NOT NULL,
    percentage DECIMAL(10,2) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    description VARCHAR(500),
    organization_id BIGINT REFERENCES organizations(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_argentina_taxes_transaction ON argentina_taxes(transaction_id);
CREATE INDEX idx_argentina_taxes_type ON argentina_taxes(tax_type);

-- 5. Modificar tabla transactions para agregar campos de moneda y tarjetas
ALTER TABLE transactions 
ADD COLUMN IF NOT EXISTS currency VARCHAR(10) DEFAULT 'ARS',
ADD COLUMN IF NOT EXISTS exchange_rate DECIMAL(15,4),
ADD COLUMN IF NOT EXISTS amount_in_local_currency DECIMAL(15,2),
ADD COLUMN IF NOT EXISTS credit_card_id BIGINT REFERENCES credit_cards(id) ON DELETE SET NULL,
ADD COLUMN IF NOT EXISTS installments INTEGER;

CREATE INDEX IF NOT EXISTS idx_transactions_currency ON transactions(currency);
CREATE INDEX IF NOT EXISTS idx_transactions_credit_card ON transactions(credit_card_id);

-- 6. Actualizar transacciones existentes
UPDATE transactions 
SET amount_in_local_currency = amount 
WHERE amount_in_local_currency IS NULL;

-- 7. Insertar tipos de cambio de ejemplo (opcional)
INSERT INTO exchange_rates (date, rate_type, buy_rate, sell_rate, organization_id)
SELECT 
    CURRENT_DATE,
    'OFICIAL',
    1000.00,
    1010.00,
    id
FROM organizations
WHERE NOT EXISTS (
    SELECT 1 FROM exchange_rates 
    WHERE date = CURRENT_DATE 
    AND rate_type = 'OFICIAL' 
    AND exchange_rates.organization_id = organizations.id
);

INSERT INTO exchange_rates (date, rate_type, buy_rate, sell_rate, organization_id)
SELECT 
    CURRENT_DATE,
    'BLUE',
    1150.00,
    1170.00,
    id
FROM organizations
WHERE NOT EXISTS (
    SELECT 1 FROM exchange_rates 
    WHERE date = CURRENT_DATE 
    AND rate_type = 'BLUE' 
    AND exchange_rates.organization_id = organizations.id
);

INSERT INTO exchange_rates (date, rate_type, buy_rate, sell_rate, organization_id)
SELECT 
    CURRENT_DATE,
    'MEP',
    1100.00,
    1110.00,
    id
FROM organizations
WHERE NOT EXISTS (
    SELECT 1 FROM exchange_rates 
    WHERE date = CURRENT_DATE 
    AND rate_type = 'MEP' 
    AND exchange_rates.organization_id = organizations.id
);

INSERT INTO exchange_rates (date, rate_type, buy_rate, sell_rate, organization_id)
SELECT 
    CURRENT_DATE,
    'TARJETA',
    1750.00,
    1760.00,
    id
FROM organizations
WHERE NOT EXISTS (
    SELECT 1 FROM exchange_rates 
    WHERE date = CURRENT_DATE 
    AND rate_type = 'TARJETA' 
    AND exchange_rates.organization_id = organizations.id
);

COMMENT ON TABLE exchange_rates IS 'Tipos de cambio diarios (USD/ARS)';
COMMENT ON TABLE credit_cards IS 'Tarjetas de crédito';
COMMENT ON TABLE credit_card_installments IS 'Cuotas de compras en tarjeta de crédito';
COMMENT ON TABLE argentina_taxes IS 'Impuestos argentinos (PAIS, percepciones, etc.)';

COMMENT ON COLUMN exchange_rates.rate_type IS 'OFICIAL, BLUE, MEP, TARJETA';
COMMENT ON COLUMN credit_cards.closing_day IS 'Día del mes de cierre (1-31)';
COMMENT ON COLUMN credit_cards.due_day IS 'Día del mes de vencimiento (1-31)';
COMMENT ON COLUMN argentina_taxes.tax_type IS 'PAIS, PERCEPCION_RG_5371, PERCEPCION_RG_4815, IVA, IIBB, OTROS';
