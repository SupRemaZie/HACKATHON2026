-- ============================================================
-- V2__seed.sql  —  CarbonTrack — Données de démonstration
-- BIGSERIAL IDs + tokens sur sites
-- ============================================================

DELETE FROM calculation_history;
DELETE FROM carbon_calculations;
DELETE FROM site_materials;
DELETE FROM site_energy_sources;
DELETE FROM sites;
DELETE FROM users;
DELETE FROM emission_factors;

-- ============================================================
-- 1. EMISSION FACTORS (IDs 1-14)
-- ============================================================
INSERT INTO emission_factors (id, category, material_name, factor_kg_co2_per_kg, unit, source, year) VALUES
( 1, 'construction', 'beton',              0.110,  'kg',    'ADEME Base Carbone', 2023),
( 2, 'construction', 'acier',              1.460,  'kg',    'ADEME Base Carbone', 2023),
( 3, 'construction', 'verre',              0.850,  'kg',    'ADEME Base Carbone', 2023),
( 4, 'construction', 'bois',               0.030,  'kg',    'ADEME Base Carbone', 2023),
( 5, 'construction', 'aluminium',          8.240,  'kg',    'ADEME Base Carbone', 2023),
( 6, 'construction', 'cuivre',             2.710,  'kg',    'ADEME Base Carbone', 2023),
( 7, 'construction', 'pvc',                2.410,  'kg',    'ADEME Base Carbone', 2023),
( 8, 'construction', 'laine_verre',        1.280,  'kg',    'ADEME Base Carbone', 2023),
( 9, 'energy',       'electricity_fr',     0.0571, 'kWh',   'RTE / ADEME',        2023),
(10, 'energy',       'gaz_naturel',        0.2270, 'kWh',   'ADEME Base Carbone', 2023),
(11, 'energy',       'fioul',              0.3240, 'kWh',   'ADEME Base Carbone', 2023),
(12, 'energy',       'electricite_renouv', 0.0130, 'kWh',   'ADEME Base Carbone', 2023),
(13, 'parking',      'parking_sousol',  2400.000,  'place', 'ADEME ACV bâtiment', 2023),
(14, 'parking',      'parking_aerien',   600.000,  'place', 'ADEME ACV bâtiment', 2023);

SELECT setval('emission_factors_id_seq', 14);

-- ============================================================
-- 2. USERS (IDs 1-5)  — password = "password" (bcrypt)
-- ============================================================
INSERT INTO users (id, email, password_hash, full_name, role) VALUES
(1, 'admin@carbontrack.local',       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin CarbonTrack', 'ADMIN'),
(2, 'marie.dupont@capgemini.com',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Marie Dupont',      'USER'),
(3, 'thomas.martin@capgemini.com',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Thomas Martin',     'USER'),
(4, 'sophie.bernard@capgemini.com',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sophie Bernard',    'USER'),
(5, 'lucas.petit@capgemini.com',     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Lucas Petit',       'USER');

SELECT setval('users_id_seq', 5);

-- ============================================================
-- 3. SITES (IDs 1-5) — token = identifiant court pour les routes front
-- ============================================================
INSERT INTO sites (id, token, created_by, name, address, city, surface_m2, nb_employees, nb_workstations, parking_underground, parking_basement, parking_outdoor) VALUES
(1, 'rn001', 1, 'Capgemini Rennes',           'Avenue de Joinville',         'Rennes',   11771, 1800, 1037,  41, 184,  83),
(2, 'pa001', 2, 'Capgemini Paris La Défense', '11 Rue de Tilsitt',           'Paris',    22400, 3500, 2100,   0, 320,   0),
(3, 'ly001', 3, 'Capgemini Lyon Part-Dieu',   '17 Rue de la République',     'Lyon',      8500,  950,  680,   0,  95,  42),
(4, 'bx001', 4, 'Capgemini Bordeaux',         '10 Quai de Bacalan',          'Bordeaux',  4200,  420,  310,   0,  40,  55),
(5, 'li001', 5, 'Capgemini Lille Euralille',  '2 Place François Mitterrand', 'Lille',     6800,  720,  510,   0, 110,  30);

SELECT setval('sites_id_seq', 5);

-- ============================================================
-- 4. SOURCES D'ENERGIE
-- ============================================================
-- Rennes : électricité 95% + gaz 5%
INSERT INTO site_energy_sources (site_id, emission_factor_id, label, energy_kwh_year) VALUES
(1,  9, 'Électricité (contrat principal)',    1748000),
(1, 10, 'Gaz naturel (chauffage appoint)',      92000);

-- Paris : 100% électricité réseau
INSERT INTO site_energy_sources (site_id, emission_factor_id, label, energy_kwh_year) VALUES
(2,  9, 'Électricité (réseau ENEDIS)',        3850000);

-- Lyon : électricité 70% + gaz 20% + ENR 10%
INSERT INTO site_energy_sources (site_id, emission_factor_id, label, energy_kwh_year) VALUES
(3,  9, 'Électricité (réseau)',                980000),
(3, 10, 'Gaz naturel (chauffage)',             280000),
(3, 12, 'Panneaux solaires toiture',           140000);

-- Bordeaux : ENR 60% + électricité 40%
INSERT INTO site_energy_sources (site_id, emission_factor_id, label, energy_kwh_year) VALUES
(4, 12, 'Énergie renouvelable (PPA solaire)', 312000),
(4,  9, 'Électricité réseau (complément)',    208000);

-- Lille : électricité 60% + gaz 30% + fioul secours 10%
INSERT INTO site_energy_sources (site_id, emission_factor_id, label, energy_kwh_year) VALUES
(5,  9, 'Électricité (réseau)',                720000),
(5, 10, 'Gaz naturel (chauffage)',             360000),
(5, 11, 'Fioul (groupe électrogène secours)', 120000);

-- ============================================================
-- 5. MATERIAUX DE CONSTRUCTION
-- ============================================================
-- Rennes (11 771 m²)
INSERT INTO site_materials (site_id, emission_factor_id, quantity_kg) VALUES
(1,  1, 11771000), (1, 2,   588550), (1, 3,   353130),
(1,  5,   117710), (1, 8,    94168), (1, 7,    58855);

-- Paris La Défense (22 400 m²)
INSERT INTO site_materials (site_id, emission_factor_id, quantity_kg) VALUES
(2,  1, 17920000), (2, 2,  1568000), (2, 3,  1120000),
(2,  5,   336000), (2, 8,   179200), (2, 7,   112000);

-- Lyon (8 500 m²)
INSERT INTO site_materials (site_id, emission_factor_id, quantity_kg) VALUES
(3,  1,  8500000), (3, 2,   425000), (3, 3,   255000),
(3,  5,    85000), (3, 8,    68000), (3, 7,    42500);

-- Bordeaux (4 200 m²)
INSERT INTO site_materials (site_id, emission_factor_id, quantity_kg) VALUES
(4,  1,  2940000), (4, 4,   840000), (4, 2,   126000),
(4,  3,   126000), (4, 5,    42000), (4, 8,    33600);

-- Lille (6 800 m²)
INSERT INTO site_materials (site_id, emission_factor_id, quantity_kg) VALUES
(5,  1,  7480000), (5, 2,   544000), (5, 3,   204000),
(5,  5,    68000), (5, 8,    54400), (5, 7,    34000);

-- ============================================================
-- 6. CALCULS CARBONE (IDs 1-5)
-- ============================================================
INSERT INTO carbon_calculations (id, site_id, calculated_by, co2_construction_kg, co2_energy_kg, co2_parking_kg, co2_total_kg, co2_per_m2, co2_per_employee, breakdown, calculated_at) VALUES
(1, 1, 1,  73732, 120695, 11796, 206223, 17.52, 114.57, '{"construction":73732,"energie":120695,"parking":11796}', '2026-03-16 08:00:00'),
(2, 2, 2, 169608, 219835, 15360, 404803, 18.07, 115.66, '{"construction":169608,"energie":219835,"parking":15360}', '2026-03-16 08:05:00'),
(3, 3, 3,  53242, 121338,  5064, 179644, 21.13, 189.10, '{"construction":53242,"energie":121338,"parking":5064}',  '2026-03-16 08:10:00'),
(4, 4, 4,  20571,  15933,  2580,  39084,  9.31,  93.06, '{"construction":20571,"energie":15933,"parking":2580}',   '2026-03-16 08:15:00'),
(5, 5, 5,  50047, 161712,  5640, 217399, 31.97, 301.94, '{"construction":50047,"energie":161712,"parking":5640}',  '2026-03-16 08:20:00');

SELECT setval('carbon_calculations_id_seq', 5);

-- ============================================================
-- 7. HISTORIQUE MENSUEL (jan 2025 → mars 2026)
-- ============================================================
INSERT INTO calculation_history (site_id, year, month, co2_total_kg, co2_construction_kg, co2_energy_kg, co2_parking_kg, label) VALUES
-- Rennes
(1, 2025,  1, 210500, 73732, 124972, 11796, 'Snapshot janvier 2025'),
(1, 2025,  2, 208200, 73732, 122672, 11796, 'Snapshot février 2025'),
(1, 2025,  3, 205800, 73732, 120272, 11796, 'Snapshot mars 2025'),
(1, 2025,  4, 203100, 73732, 117572, 11796, 'Snapshot avril 2025'),
(1, 2025,  5, 200400, 73732, 114872, 11796, 'Snapshot mai 2025'),
(1, 2025,  6, 198200, 73732, 112672, 11796, 'Snapshot juin 2025'),
(1, 2025,  7, 197500, 73732, 111972, 11796, 'Snapshot juillet 2025'),
(1, 2025,  8, 197100, 73732, 111572, 11796, 'Snapshot août 2025'),
(1, 2025,  9, 199200, 73732, 113672, 11796, 'Snapshot septembre 2025'),
(1, 2025, 10, 202300, 73732, 116772, 11796, 'Snapshot octobre 2025'),
(1, 2025, 11, 205100, 73732, 119572, 11796, 'Snapshot novembre 2025'),
(1, 2025, 12, 207800, 73732, 122272, 11796, 'Snapshot décembre 2025'),
(1, 2026,  1, 207100, 73732, 121572, 11796, 'Snapshot janvier 2026'),
(1, 2026,  2, 205500, 73732, 119972, 11796, 'Snapshot février 2026'),
(1, 2026,  3, 206223, 73732, 120695, 11796, 'Snapshot mars 2026'),
-- Paris
(2, 2025,  1, 420100, 169608, 235132, 15360, 'Snapshot janvier 2025'),
(2, 2025,  2, 418500, 169608, 233532, 15360, 'Snapshot février 2025'),
(2, 2025,  3, 412300, 169608, 227332, 15360, 'Snapshot mars 2025'),
(2, 2025,  4, 406800, 169608, 221832, 15360, 'Snapshot avril 2025'),
(2, 2025,  5, 401200, 169608, 216232, 15360, 'Snapshot mai 2025'),
(2, 2025,  6, 397500, 169608, 212532, 15360, 'Snapshot juin 2025'),
(2, 2025,  7, 396100, 169608, 211132, 15360, 'Snapshot juillet 2025'),
(2, 2025,  8, 395800, 169608, 210832, 15360, 'Snapshot août 2025'),
(2, 2025,  9, 400200, 169608, 215232, 15360, 'Snapshot septembre 2025'),
(2, 2025, 10, 407600, 169608, 222632, 15360, 'Snapshot octobre 2025'),
(2, 2025, 11, 414900, 169608, 229932, 15360, 'Snapshot novembre 2025'),
(2, 2025, 12, 421200, 169608, 236232, 15360, 'Snapshot décembre 2025'),
(2, 2026,  1, 419800, 169608, 234832, 15360, 'Snapshot janvier 2026'),
(2, 2026,  2, 413200, 169608, 228232, 15360, 'Snapshot février 2026'),
(2, 2026,  3, 404803, 169608, 219835, 15360, 'Snapshot mars 2026'),
-- Lyon
(3, 2025,  1, 192400, 53242, 134094, 5064, 'Snapshot janvier 2025'),
(3, 2025,  2, 190100, 53242, 131794, 5064, 'Snapshot février 2025'),
(3, 2025,  3, 186500, 53242, 128194, 5064, 'Snapshot mars 2025'),
(3, 2025,  4, 181200, 53242, 122894, 5064, 'Snapshot avril 2025'),
(3, 2025,  5, 175800, 53242, 117494, 5064, 'Snapshot mai 2025'),
(3, 2025,  6, 168400, 53242, 110094, 5064, 'Snapshot juin 2025'),
(3, 2025,  7, 164200, 53242, 105894, 5064, 'Snapshot juillet 2025'),
(3, 2025,  8, 162800, 53242, 104494, 5064, 'Snapshot août 2025'),
(3, 2025,  9, 170100, 53242, 111794, 5064, 'Snapshot septembre 2025'),
(3, 2025, 10, 176500, 53242, 118194, 5064, 'Snapshot octobre 2025'),
(3, 2025, 11, 182100, 53242, 123794, 5064, 'Snapshot novembre 2025'),
(3, 2025, 12, 188900, 53242, 130594, 5064, 'Snapshot décembre 2025'),
(3, 2026,  1, 186200, 53242, 127894, 5064, 'Snapshot janvier 2026'),
(3, 2026,  2, 182400, 53242, 124094, 5064, 'Snapshot février 2026'),
(3, 2026,  3, 179644, 53242, 121338, 5064, 'Snapshot mars 2026'),
-- Bordeaux
(4, 2025,  1, 41200, 20571, 18049, 2580, 'Snapshot janvier 2025'),
(4, 2025,  2, 40800, 20571, 17649, 2580, 'Snapshot février 2025'),
(4, 2025,  3, 40200, 20571, 17049, 2580, 'Snapshot mars 2025'),
(4, 2025,  4, 39600, 20571, 16449, 2580, 'Snapshot avril 2025'),
(4, 2025,  5, 38900, 20571, 15749, 2580, 'Snapshot mai 2025'),
(4, 2025,  6, 37800, 20571, 14649, 2580, 'Snapshot juin 2025'),
(4, 2025,  7, 37200, 20571, 14049, 2580, 'Snapshot juillet 2025'),
(4, 2025,  8, 37100, 20571, 13949, 2580, 'Snapshot août 2025'),
(4, 2025,  9, 38100, 20571, 14949, 2580, 'Snapshot septembre 2025'),
(4, 2025, 10, 38900, 20571, 15749, 2580, 'Snapshot octobre 2025'),
(4, 2025, 11, 39500, 20571, 16349, 2580, 'Snapshot novembre 2025'),
(4, 2025, 12, 40100, 20571, 16949, 2580, 'Snapshot décembre 2025'),
(4, 2026,  1, 39800, 20571, 16649, 2580, 'Snapshot janvier 2026'),
(4, 2026,  2, 39300, 20571, 16149, 2580, 'Snapshot février 2026'),
(4, 2026,  3, 39084, 20571, 15933, 2580, 'Snapshot mars 2026'),
-- Lille
(5, 2025,  1, 238500, 50047, 182813, 5640, 'Snapshot janvier 2025'),
(5, 2025,  2, 236100, 50047, 180413, 5640, 'Snapshot février 2025'),
(5, 2025,  3, 231800, 50047, 176113, 5640, 'Snapshot mars 2025'),
(5, 2025,  4, 227400, 50047, 171713, 5640, 'Snapshot avril 2025'),
(5, 2025,  5, 222100, 50047, 166413, 5640, 'Snapshot mai 2025'),
(5, 2025,  6, 216500, 50047, 160813, 5640, 'Snapshot juin 2025'),
(5, 2025,  7, 214200, 50047, 158513, 5640, 'Snapshot juillet 2025'),
(5, 2025,  8, 213800, 50047, 158113, 5640, 'Snapshot août 2025'),
(5, 2025,  9, 218400, 50047, 162713, 5640, 'Snapshot septembre 2025'),
(5, 2025, 10, 222900, 50047, 167213, 5640, 'Snapshot octobre 2025'),
(5, 2025, 11, 228700, 50047, 173013, 5640, 'Snapshot novembre 2025'),
(5, 2025, 12, 234100, 50047, 178413, 5640, 'Snapshot décembre 2025'),
(5, 2026,  1, 230500, 50047, 174813, 5640, 'Snapshot janvier 2026'),
(5, 2026,  2, 224100, 50047, 168413, 5640, 'Snapshot février 2026'),
(5, 2026,  3, 217399, 50047, 161712, 5640, 'Snapshot mars 2026');
