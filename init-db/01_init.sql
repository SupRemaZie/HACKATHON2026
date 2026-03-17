-- ============================================================
-- V1__init.sql  —  CarbonTrack — Hackathon #26
-- Tables + seed emission_factors avec UUIDs fixes
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ------------------------------------------------------------
-- 1. USERS
-- ------------------------------------------------------------
CREATE TABLE users (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255),
    role          VARCHAR(50)  NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 2. SITES
-- ------------------------------------------------------------
CREATE TABLE sites (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    created_by          UUID         NOT NULL REFERENCES users(id),
    name                VARCHAR(255) NOT NULL,
    address             VARCHAR(500),
    city                VARCHAR(255),
    surface_m2          FLOAT        NOT NULL CHECK (surface_m2 > 0),
    nb_employees        INT          NOT NULL DEFAULT 0,
    nb_workstations     INT          NOT NULL DEFAULT 0,
    parking_underground INT          NOT NULL DEFAULT 0,
    parking_basement    INT          NOT NULL DEFAULT 0,
    parking_outdoor     INT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 3. FACTEURS D'EMISSION
-- ------------------------------------------------------------
CREATE TABLE emission_factors (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    ademe_id             VARCHAR(100),
    category             VARCHAR(100) NOT NULL,
    material_name        VARCHAR(255) NOT NULL,
    factor_kg_co2_per_kg FLOAT        NOT NULL,
    unit                 VARCHAR(50)  NOT NULL DEFAULT 'kg',
    source               VARCHAR(255) NOT NULL DEFAULT 'ADEME',
    year                 INT          NOT NULL DEFAULT 2023,
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
    co2_kg             FLOAT NOT NULL DEFAULT 0
);

-- ------------------------------------------------------------
-- 5. SOURCES D'ENERGIE PAR SITE
-- ------------------------------------------------------------
CREATE TABLE site_energy_sources (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id            UUID         NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    emission_factor_id UUID         NOT NULL REFERENCES emission_factors(id),
    label              VARCHAR(255),
    energy_kwh_year    FLOAT        NOT NULL CHECK (energy_kwh_year >= 0),
    co2_kg             FLOAT        NOT NULL DEFAULT 0
);

-- ------------------------------------------------------------
-- 6. CALCULS CARBONE
-- ------------------------------------------------------------
CREATE TABLE carbon_calculations (
    id                  UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id             UUID      NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    calculated_by       UUID      NOT NULL REFERENCES users(id),
    co2_construction_kg FLOAT     NOT NULL DEFAULT 0,
    co2_energy_kg       FLOAT     NOT NULL DEFAULT 0,
    co2_parking_kg      FLOAT     NOT NULL DEFAULT 0,
    co2_total_kg        FLOAT     NOT NULL DEFAULT 0,
    co2_per_m2          FLOAT     NOT NULL DEFAULT 0,
    co2_per_employee    FLOAT     NOT NULL DEFAULT 0,
    breakdown           JSONB,
    calculated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 7. HISTORIQUE MENSUEL
-- ------------------------------------------------------------
CREATE TABLE calculation_history (
    id                  UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id             UUID      NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    calculation_id      UUID      REFERENCES carbon_calculations(id) ON DELETE SET NULL,
    year                INT       NOT NULL,
    month               INT       NOT NULL CHECK (month BETWEEN 1 AND 12),
    co2_total_kg        FLOAT     NOT NULL,
    co2_construction_kg FLOAT     NOT NULL DEFAULT 0,
    co2_energy_kg       FLOAT     NOT NULL DEFAULT 0,
    co2_parking_kg      FLOAT     NOT NULL DEFAULT 0,
    label               VARCHAR(255),
    recorded_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (site_id, year, month)
);

-- ============================================================
-- INDEX
-- ============================================================
CREATE INDEX idx_sites_created_by        ON sites(created_by);
CREATE INDEX idx_site_materials_site     ON site_materials(site_id);
CREATE INDEX idx_site_energy_site        ON site_energy_sources(site_id);
CREATE INDEX idx_calculations_site       ON carbon_calculations(site_id);
CREATE INDEX idx_calculations_date       ON carbon_calculations(calculated_at);
CREATE INDEX idx_history_site_year_month ON calculation_history(site_id, year, month);





INSERT INTO emission_factors (id, category, material_name, factor_kg_co2_per_kg, unit, source, year) VALUES
-- Construction
('ef000000-0000-0000-0000-000000000001', 'construction', 'beton',              0.1100, 'kg',    'ADEME Base Carbone', 2023),
('ef000000-0000-0000-0000-000000000002', 'construction', 'acier',              1.4600, 'kg',    'ADEME Base Carbone', 2023),
('ef000000-0000-0000-0000-000000000003', 'construction', 'verre',              0.8500, 'kg',    'ADEME Base Carbone', 2023),
('ef000000-0000-0000-0000-000000000004', 'construction', 'bois',               0.0300, 'kg',    'ADEME Base Carbone', 2023),
('ef000000-0000-0000-0000-000000000005', 'construction', 'aluminium',          8.2400, 'kg',    'ADEME Base Carbone', 2023),
('ef000000-0000-0000-0000-000000000006', 'construction', 'cuivre',             2.7100, 'kg',    'ADEME Base Carbone', 2023),
('ef000000-0000-0000-0000-000000000007', 'construction', 'pvc',                2.4100, 'kg',    'ADEME Base Carbone', 2023),
('ef000000-0000-0000-0000-000000000008', 'construction', 'laine_verre',        1.2800, 'kg',    'ADEME Base Carbone', 2023),
-- Énergie
('ef000000-0000-0000-0000-000000000009', 'energy',       'electricity_fr',     0.0571, 'kWh',   'RTE / ADEME',        2023),
('ef000000-0000-0000-0000-000000000010', 'energy',       'gaz_naturel',        0.2270, 'kWh',   'ADEME Base Carbone', 2023),
('ef000000-0000-0000-0000-000000000011', 'energy',       'fioul',              0.3240, 'kWh',   'ADEME Base Carbone', 2023),
('ef000000-0000-0000-0000-000000000012', 'energy',       'electricite_renouv', 0.0130, 'kWh',   'ADEME Base Carbone', 2023),
-- Parking
('ef000000-0000-0000-0000-000000000013', 'parking',      'parking_sousol',  2400.0000, 'place', 'ADEME ACV bâtiment', 2023),
('ef000000-0000-0000-0000-000000000014', 'parking',      'parking_aerien',   600.0000, 'place', 'ADEME ACV bâtiment', 2023);