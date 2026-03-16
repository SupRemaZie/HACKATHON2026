-- ============================================================
-- V1__init.sql  —  CarbonTrack — Hackathon #26
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ------------------------------------------------------------
-- 1. USERS
-- ------------------------------------------------------------
CREATE TABLE users (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255),
    role          VARCHAR(50)  NOT NULL DEFAULT 'USER',  -- USER | ADMIN
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 2. SITES  (sans colonnes énergie scalaires — gérées via site_energy_sources)
-- ------------------------------------------------------------
CREATE TABLE sites (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    created_by           UUID         NOT NULL REFERENCES users(id),
    name                 VARCHAR(255) NOT NULL,
    address              VARCHAR(500),
    city                 VARCHAR(255),
    surface_m2           FLOAT        NOT NULL CHECK (surface_m2 > 0),
    nb_employees         INT          NOT NULL DEFAULT 0,
    nb_workstations      INT          NOT NULL DEFAULT 0,
    parking_underground  INT          NOT NULL DEFAULT 0,  -- sous-dalle
    parking_basement     INT          NOT NULL DEFAULT 0,  -- sous-sol
    parking_outdoor      INT          NOT NULL DEFAULT 0,  -- aériens
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 3. FACTEURS D'EMISSION (référentiel ADEME)
-- ------------------------------------------------------------
CREATE TABLE emission_factors (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    category             VARCHAR(100) NOT NULL,   -- construction | energy | parking
    material_name        VARCHAR(255) NOT NULL,   -- beton, acier, electricity_fr...
    factor_kg_co2_per_kg FLOAT        NOT NULL,
    unit                 VARCHAR(50)  NOT NULL DEFAULT 'kg',  -- kg | kWh | place
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
    co2_kg             FLOAT NOT NULL DEFAULT 0   -- calculé : quantity_kg × factor
);

-- ------------------------------------------------------------
-- 5. SOURCES D'ENERGIE PAR SITE (0..N par site)
-- ------------------------------------------------------------
CREATE TABLE site_energy_sources (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id            UUID         NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    emission_factor_id UUID         NOT NULL REFERENCES emission_factors(id),
    label              VARCHAR(255),               -- ex: "Électricité bâtiment A"
    energy_kwh_year    FLOAT        NOT NULL CHECK (energy_kwh_year >= 0),
    co2_kg             FLOAT        NOT NULL DEFAULT 0  -- calculé : kwh × factor
);

-- ------------------------------------------------------------
-- 6. CALCULS CARBONE
-- ------------------------------------------------------------
CREATE TABLE carbon_calculations (
    id                   UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id              UUID      NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    calculated_by        UUID      NOT NULL REFERENCES users(id),
    co2_construction_kg  FLOAT     NOT NULL DEFAULT 0,
    co2_energy_kg        FLOAT     NOT NULL DEFAULT 0,
    co2_parking_kg       FLOAT     NOT NULL DEFAULT 0,
    co2_total_kg         FLOAT     NOT NULL DEFAULT 0,
    co2_per_m2           FLOAT     NOT NULL DEFAULT 0,
    co2_per_employee     FLOAT     NOT NULL DEFAULT 0,
    breakdown            JSONB,    -- détail par matériau + source énergie sérialisé
    calculated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 7. HISTORIQUE MENSUEL (courbes de progression)
-- ------------------------------------------------------------
CREATE TABLE calculation_history (
    id                   UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id              UUID      NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    calculation_id       UUID      REFERENCES carbon_calculations(id) ON DELETE SET NULL,
    year                 INT       NOT NULL,
    month                INT       NOT NULL CHECK (month BETWEEN 1 AND 12),
    co2_total_kg         FLOAT     NOT NULL,
    co2_construction_kg  FLOAT     NOT NULL DEFAULT 0,
    co2_energy_kg        FLOAT     NOT NULL DEFAULT 0,
    co2_parking_kg       FLOAT     NOT NULL DEFAULT 0,
    label                VARCHAR(255),
    recorded_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (site_id, year, month)   -- un seul snapshot par mois par site
);

-- ============================================================
-- INDEX
-- ============================================================
CREATE INDEX idx_sites_created_by         ON sites(created_by);
CREATE INDEX idx_site_materials_site      ON site_materials(site_id);
CREATE INDEX idx_site_energy_site         ON site_energy_sources(site_id);
CREATE INDEX idx_calculations_site        ON carbon_calculations(site_id);
CREATE INDEX idx_calculations_date        ON carbon_calculations(calculated_at);
CREATE INDEX idx_history_site_year_month  ON calculation_history(site_id, year, month);

-- ============================================================
-- VUE : progression mensuelle avec deltas
-- ============================================================
CREATE OR REPLACE VIEW monthly_progress AS
SELECT
    h.site_id,
    s.name                                                        AS site_name,
    s.city,
    h.year,
    h.month,
    TO_DATE(
        h.year::text || '-' || LPAD(h.month::text, 2, '0') || '-01',
        'YYYY-MM-DD'
    )                                                             AS period_date,
    h.co2_total_kg,
    h.co2_construction_kg,
    h.co2_energy_kg,
    h.co2_parking_kg,

    -- Delta absolu vs mois précédent (kgCO₂e)
    h.co2_total_kg
        - LAG(h.co2_total_kg) OVER (
            PARTITION BY h.site_id
            ORDER BY h.year, h.month
          )                                                       AS delta_vs_prev_month_kg,

    -- Progression en % vs mois précédent
    ROUND(
        (h.co2_total_kg
            - LAG(h.co2_total_kg) OVER (
                PARTITION BY h.site_id
                ORDER BY h.year, h.month
              )
        ) / NULLIF(
            LAG(h.co2_total_kg) OVER (
                PARTITION BY h.site_id
                ORDER BY h.year, h.month
            ), 0
        ) * 100
    , 2)                                                          AS progression_pct,

    -- Delta absolu vs même mois N-1 (kgCO₂e)
    h.co2_total_kg
        - LAG(h.co2_total_kg, 12) OVER (
            PARTITION BY h.site_id
            ORDER BY h.year, h.month
          )                                                       AS delta_vs_n1_kg,

    -- Progression en % vs même mois N-1
    ROUND(
        (h.co2_total_kg
            - LAG(h.co2_total_kg, 12) OVER (
                PARTITION BY h.site_id
                ORDER BY h.year, h.month
              )
        ) / NULLIF(
            LAG(h.co2_total_kg, 12) OVER (
                PARTITION BY h.site_id
                ORDER BY h.year, h.month
            ), 0
        ) * 100
    , 2)                                                          AS progression_pct_n1

FROM calculation_history h
JOIN sites s ON s.id = h.site_id
ORDER BY h.site_id, h.year, h.month;

-- ============================================================
-- SEED — Facteurs d'émission ADEME
-- ============================================================

-- Matériaux de construction (kgCO₂e/kg)
INSERT INTO emission_factors (category, material_name, factor_kg_co2_per_kg, unit, source, year) VALUES
('construction', 'beton',          0.110,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'acier',          1.460,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'verre',          0.850,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'bois',           0.030,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'aluminium',      8.240,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'cuivre',         2.710,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'pvc',            2.410,  'kg',  'ADEME Base Carbone', 2023),
('construction', 'laine_verre',    1.280,  'kg',  'ADEME Base Carbone', 2023),

-- Énergie (kgCO₂e/kWh)
('energy', 'electricity_fr',       0.0571, 'kWh', 'RTE / ADEME',        2023),
('energy', 'gaz_naturel',          0.2270, 'kWh', 'ADEME Base Carbone', 2023),
('energy', 'fioul',                0.3240, 'kWh', 'ADEME Base Carbone', 2023),
('energy', 'electricite_renouv',   0.0130, 'kWh', 'ADEME Base Carbone', 2023),

-- Parking (kgCO₂e/place — ACV amorti 50 ans)
('parking', 'parking_sousol',      2400.0, 'place','ADEME ACV bâtiment', 2023),
('parking', 'parking_aerien',       600.0, 'place','ADEME ACV bâtiment', 2023);

-- ============================================================
-- SEED — Utilisateur admin + site Capgemini Rennes
-- ============================================================
INSERT INTO users (id, email, password_hash, full_name, role) VALUES
(
    '00000000-0000-0000-0000-000000000001',
    'admin@hackathon.local',
    '$2a$10$REMPLACER_PAR_HASH_BCRYPT',
    'Admin Hackathon',
    'ADMIN'
);

INSERT INTO sites (
    id, created_by,
    name, address, city,
    surface_m2, nb_employees, nb_workstations,
    parking_underground, parking_basement, parking_outdoor
) VALUES (
    '00000000-0000-0000-0000-000000000010',
    '00000000-0000-0000-0000-000000000001',
    'Capgemini Rennes',
    'Avenue de Joinville',
    'Rennes',
    11771, 1800, 1037,
    41, 184, 83
);

-- Sources d'énergie du site Capgemini Rennes (multi-sources)
INSERT INTO site_energy_sources (site_id, emission_factor_id, label, energy_kwh_year) VALUES
(
    '00000000-0000-0000-0000-000000000010',
    (SELECT id FROM emission_factors WHERE material_name = 'electricity_fr'),
    'Électricité (contrat principal)',
    1748000   -- 95% des 1 840 MWh
),
(
    '00000000-0000-0000-0000-000000000010',
    (SELECT id FROM emission_factors WHERE material_name = 'gaz_naturel'),
    'Gaz naturel (chauffage appoint)',
    92000     -- 5% restants, converti en kWh équivalent
);

-- Matériaux de construction du site (estimations open-source bâtiment tertiaire)
-- Base : ~1 000 kg/m² béton, ~50 kg/m² acier, ~30 kg/m² verre, ~10 kg/m² aluminium
INSERT INTO site_materials (site_id, emission_factor_id, quantity_kg) VALUES
(
    '00000000-0000-0000-0000-000000000010',
    (SELECT id FROM emission_factors WHERE material_name = 'beton'),
    11771000   -- 11 771 m² × 1 000 kg/m²
),
(
    '00000000-0000-0000-0000-000000000010',
    (SELECT id FROM emission_factors WHERE material_name = 'acier'),
    588550     -- 11 771 m² × 50 kg/m²
),
(
    '00000000-0000-0000-0000-000000000010',
    (SELECT id FROM emission_factors WHERE material_name = 'verre'),
    353130     -- 11 771 m² × 30 kg/m²
),
(
    '00000000-0000-0000-0000-000000000010',
    (SELECT id FROM emission_factors WHERE material_name = 'aluminium'),
    117710     -- 11 771 m² × 10 kg/m²
);

-- Snapshot historique de démo (pour avoir des courbes dès le lancement)
-- CO₂ total estimé Capgemini Rennes :
--   Construction : (11771000×0.11 + 588550×1.46 + 353130×0.85 + 117710×8.24) / 50 ans
--                = (1294810 + 859283 + 300160 + 969930) / 50 = 68847 kgCO₂e/an
--   Énergie      : 1748000×0.0571 + 92000×0.227 = 99811 + 20884 = 120695 kgCO₂e
--   Parking      : (41+184)×2400/50 + 83×600/50 = 10800 + 996 = 11796 kgCO₂e
--   TOTAL        : ~201 338 kgCO₂e/an

INSERT INTO calculation_history
    (site_id, year, month, co2_total_kg, co2_construction_kg, co2_energy_kg, co2_parking_kg, label)
VALUES
('00000000-0000-0000-0000-000000000010', 2025,  1, 201338, 68847, 120695, 11796, 'Snapshot janvier 2025'),
('00000000-0000-0000-0000-000000000010', 2025,  2, 199800, 68847, 119200, 11796, 'Snapshot février 2025'),
('00000000-0000-0000-0000-000000000010', 2025,  3, 197500, 68847, 116900, 11796, 'Snapshot mars 2025'),
('00000000-0000-0000-0000-000000000010', 2025,  4, 195200, 68847, 114600, 11796, 'Snapshot avril 2025'),
('00000000-0000-0000-0000-000000000010', 2025,  5, 193100, 68847, 112500, 11796, 'Snapshot mai 2025'),
('00000000-0000-0000-0000-000000000010', 2025,  6, 191400, 68847, 110800, 11796, 'Snapshot juin 2025'),
('00000000-0000-0000-0000-000000000010', 2025,  7, 190800, 68847, 110200, 11796, 'Snapshot juillet 2025'),
('00000000-0000-0000-0000-000000000010', 2025,  8, 190200, 68847, 109600, 11796, 'Snapshot août 2025'),
('00000000-0000-0000-0000-000000000010', 2025,  9, 192100, 68847, 111500, 11796, 'Snapshot septembre 2025'),
('00000000-0000-0000-0000-000000000010', 2025, 10, 194800, 68847, 114200, 11796, 'Snapshot octobre 2025'),
('00000000-0000-0000-0000-000000000010', 2025, 11, 197200, 68847, 116600, 11796, 'Snapshot novembre 2025'),
('00000000-0000-0000-0000-000000000010', 2025, 12, 199500, 68847, 118900, 11796, 'Snapshot décembre 2025'),
('00000000-0000-0000-0000-000000000010', 2026,  1, 198400, 68847, 117800, 11796, 'Snapshot janvier 2026'),
('00000000-0000-0000-0000-000000000010', 2026,  2, 196100, 68847, 115500, 11796, 'Snapshot février 2026'),
('00000000-0000-0000-0000-000000000010', 2026,  3, 194200, 68847, 113600, 11796, 'Snapshot mars 2026');