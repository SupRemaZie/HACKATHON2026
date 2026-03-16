-- ============================================================
-- V1__init.sql  —  Carbon Footprint DB — Hackathon #26
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ------------------------------------------------------------
-- 1. USERS
-- ------------------------------------------------------------
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(255),
    role            VARCHAR(50)  NOT NULL DEFAULT 'USER',  -- USER | ADMIN
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 2. SITES
-- ------------------------------------------------------------
CREATE TABLE sites (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_by          UUID NOT NULL REFERENCES users(id),
    name                VARCHAR(255) NOT NULL,
    address             VARCHAR(500),
    city                VARCHAR(255),
    surface_m2          FLOAT        NOT NULL CHECK (surface_m2 > 0),
    nb_employees        INT          NOT NULL DEFAULT 0,
    nb_workstations     INT          NOT NULL DEFAULT 0,
    parking_underground INT          NOT NULL DEFAULT 0,  -- sous-dalle
    parking_basement    INT          NOT NULL DEFAULT 0,  -- sous-sol
    parking_outdoor     INT          NOT NULL DEFAULT 0,  -- aériens
    energy_kwh_year     FLOAT        NOT NULL DEFAULT 0,
    energy_source       VARCHAR(100) NOT NULL DEFAULT 'electricity_fr', -- clé vers facteur
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 3. FACTEURS D'EMISSION (référentiel ADEME)
-- ------------------------------------------------------------
CREATE TABLE emission_factors (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category              VARCHAR(100) NOT NULL,  -- construction | energy | parking
    material_name         VARCHAR(255) NOT NULL,  -- beton, acier, verre, bois...
    factor_kg_co2_per_kg  FLOAT        NOT NULL,
    unit                  VARCHAR(50)  NOT NULL DEFAULT 'kg',  -- kg | kWh | place
    source                VARCHAR(255) NOT NULL DEFAULT 'ADEME',
    year                  INT          NOT NULL DEFAULT 2023,
    UNIQUE (material_name, year)
);

-- ------------------------------------------------------------
-- 4. MATERIAUX PAR SITE
-- ------------------------------------------------------------
CREATE TABLE site_materials (
    id                 UUID  PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id            UUID  NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    emission_factor_id UUID  NOT NULL REFERENCES emission_factors(id),
    quantity_kg        FLOAT NOT NULL CHECK (quantity_kg >= 0),
    co2_kg             FLOAT NOT NULL DEFAULT 0   -- calculé = quantity_kg * factor
);

-- ------------------------------------------------------------
-- 5. CALCULS CARBONE
-- ------------------------------------------------------------
CREATE TABLE carbon_calculations (
    id                   UUID  PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id              UUID  NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    calculated_by        UUID  NOT NULL REFERENCES users(id),
    co2_construction_kg  FLOAT NOT NULL DEFAULT 0,
    co2_energy_kg        FLOAT NOT NULL DEFAULT 0,
    co2_parking_kg       FLOAT NOT NULL DEFAULT 0,
    co2_total_kg         FLOAT NOT NULL DEFAULT 0,
    co2_per_m2           FLOAT NOT NULL DEFAULT 0,
    co2_per_employee     FLOAT NOT NULL DEFAULT 0,
    breakdown            JSONB,   -- détail par matériau sérialisé
    calculated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 6. HISTORIQUE (courbes d'évolution)
-- ------------------------------------------------------------
CREATE TABLE calculation_history (
    id             UUID  PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id        UUID  NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    calculation_id UUID  REFERENCES carbon_calculations(id) ON DELETE SET NULL,
    year           INT   NOT NULL,
    co2_total_kg   FLOAT NOT NULL,
    label          VARCHAR(255),   -- ex: "Mesure initiale 2025"
    recorded_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- INDEX utiles
-- ------------------------------------------------------------
CREATE INDEX idx_sites_created_by        ON sites(created_by);
CREATE INDEX idx_site_materials_site     ON site_materials(site_id);
CREATE INDEX idx_calculations_site       ON carbon_calculations(site_id);
CREATE INDEX idx_calculations_date       ON carbon_calculations(calculated_at);
CREATE INDEX idx_history_site_year       ON calculation_history(site_id, year);

-- ============================================================
-- SEED — Facteurs d'émission ADEME (valeurs officielles)
-- ============================================================
INSERT INTO emission_factors (category, material_name, factor_kg_co2_per_kg, unit, source, year) VALUES
-- Matériaux de construction (kgCO₂e/kg)
('construction', 'beton',          0.110,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'acier',          1.460,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'verre',          0.850,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'bois',           0.030,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'aluminium',      8.240,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'cuivre',         2.710,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'pvc',            2.410,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'laine_verre',    1.280,  'kg',  'ADEME Base Carbone', 2023),
-- Énergie (kgCO₂e/kWh) — unité = kWh ici
('energy',       'electricity_fr', 0.0571, 'kWh', 'RTE / ADEME 2023',   2023),
('energy',       'gaz_naturel',    0.2270, 'kWh', 'ADEME Base Carbone', 2023),
('energy',       'fioul',          0.3240, 'kWh', 'ADEME Base Carbone', 2023),
-- Parking (kgCO₂e/place construite, amortie sur 50 ans)
('parking',      'parking_sousol', 2400.0, 'place','ADEME / étude ACV', 2023),
('parking',      'parking_aerien', 600.0,  'place','ADEME / étude ACV', 2023);

-- ============================================================
-- SEED — Site Capgemini Rennes (données annexe cahier des charges)
-- ============================================================
INSERT INTO users (id, email, password_hash, full_name, role) VALUES
('00000000-0000-0000-0000-000000000001',
 'admin@hackathon.local',
 '$2a$10$PLACEHOLDER_HASH',   -- à remplacer par BCrypt réel
 'Admin Hackathon', 'ADMIN');

INSERT INTO sites (
    id, created_by, name, address, city,
    surface_m2, nb_employees, nb_workstations,
    parking_underground, parking_basement, parking_outdoor,
    energy_kwh_year, energy_source
) VALUES (
    '00000000-0000-0000-0000-000000000010',
    '00000000-0000-0000-0000-000000000001',
    'Capgemini Rennes',
    'Avenue de Joinville',
    'Rennes',
    11771, 1800, 1037,
    41, 184, 83,     -- parking: sous-dalle / sous-sol / aériens
    1840000,         -- 1840 MWh → converti en kWh
    'electricity_fr'
);