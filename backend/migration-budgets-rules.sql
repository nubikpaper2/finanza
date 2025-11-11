-- Migración: Agregar tablas para Presupuestos y Reglas de Categorización
-- Fecha: 2024-11-10

-- Tabla de Presupuestos
CREATE TABLE IF NOT EXISTS budgets (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    organization_id BIGINT,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_budget_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_budget_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT unique_budget_per_category_month UNIQUE (category_id, year, month, organization_id)
);

-- Índices para mejorar rendimiento de búsquedas
CREATE INDEX idx_budgets_organization_year_month ON budgets(organization_id, year, month);
CREATE INDEX idx_budgets_category ON budgets(category_id);
CREATE INDEX idx_budgets_year_month ON budgets(year, month);

-- Tabla de Reglas de Categorización
CREATE TABLE IF NOT EXISTS category_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL,
    pattern VARCHAR(500) NOT NULL,
    category_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    priority INTEGER NOT NULL DEFAULT 0,
    organization_id BIGINT,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rule_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_rule_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_rule_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT check_rule_type CHECK (type IN ('CONTAINS', 'STARTS_WITH', 'ENDS_WITH', 'EXACT_MATCH', 'REGEX', 'AMOUNT_RANGE'))
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_category_rules_organization ON category_rules(organization_id);
CREATE INDEX idx_category_rules_organization_active_priority ON category_rules(organization_id, active, priority DESC);
CREATE INDEX idx_category_rules_category ON category_rules(category_id);

-- Comentarios en las tablas
COMMENT ON TABLE budgets IS 'Presupuestos mensuales por categoría';
COMMENT ON COLUMN budgets.amount IS 'Monto presupuestado para la categoría en el mes';
COMMENT ON COLUMN budgets.year IS 'Año del presupuesto';
COMMENT ON COLUMN budgets.month IS 'Mes del presupuesto (1-12)';

COMMENT ON TABLE category_rules IS 'Reglas para auto-categorizar transacciones';
COMMENT ON COLUMN category_rules.type IS 'Tipo de regla: CONTAINS, STARTS_WITH, ENDS_WITH, EXACT_MATCH, REGEX, AMOUNT_RANGE';
COMMENT ON COLUMN category_rules.pattern IS 'Patrón a buscar según el tipo de regla';
COMMENT ON COLUMN category_rules.priority IS 'Prioridad de ejecución (mayor número = mayor prioridad)';

-- Datos de ejemplo (opcional, descomentar si se desea)
/*
-- Ejemplo de reglas de categorización
INSERT INTO category_rules (name, description, type, pattern, category_id, active, priority, organization_id, created_by)
SELECT 
    'Auto-categorizar supermercado',
    'Asigna automáticamente la categoría Alimentación a compras en supermercado',
    'CONTAINS',
    'supermercado',
    c.id,
    true,
    10,
    c.organization_id,
    c.created_by
FROM categories c
WHERE c.name = 'Alimentación' AND c.type = 'EXPENSE'
LIMIT 1;

INSERT INTO category_rules (name, description, type, pattern, category_id, active, priority, organization_id, created_by)
SELECT 
    'Auto-categorizar salario',
    'Asigna automáticamente la categoría Salario a pagos de nómina',
    'CONTAINS',
    'salario',
    c.id,
    true,
    10,
    c.organization_id,
    c.created_by
FROM categories c
WHERE c.name = 'Salario' AND c.type = 'INCOME'
LIMIT 1;
*/

-- Verificación
SELECT 'Tablas creadas exitosamente' AS status;
SELECT COUNT(*) AS budgets_count FROM budgets;
SELECT COUNT(*) AS category_rules_count FROM category_rules;
