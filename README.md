# CarbonTrack 🌿

> Calculateur d'empreinte carbone de sites physiques — Hackathon #26 Capgemini × SUP Vinci — Rennes, 16 & 17 Mars 2026

---

## Sommaire

- [Contexte & Vision](#1-contexte--vision)
- [Stack Technique](#2-stack-technique)
- [Lancement rapide](#3-lancement-rapide)
- [Fonctionnalités](#4-fonctionnalités)
- [Schéma Base de Données](#5-schéma-base-de-données)
- [Facteurs d'émission ADEME](#6-facteurs-démission-ademe)
- [API REST](#7-api-rest)
- [Tâches planifiées](#8-tâches-planifiées-spring-scheduled)
- [Structure du projet](#9-structure-du-projet)
- [Livrables attendus](#10-livrables-attendus)
- [Critères d'évaluation](#11-critères-dévaluation)
- [Équipe](#12-équipe)

---

## 1. Contexte & Vision

### Mission

Développer une application **fullstack** permettant aux organisations de :

- Mesurer l'impact environnemental de leurs sites physiques
- Identifier les sources majeures d'émissions CO₂
- Comparer plusieurs sites entre eux
- Faciliter la prise de décision pour la réduction des émissions

### Problématique

La majorité des organisations manquent :

- d'outils simples pour mesurer l'empreinte carbone d'un site
- de transparence sur l'impact des matériaux de construction
- de moyens pour historiser et comparer plusieurs sites
- de tableaux de bord clairs et actionnables

### Site de référence — Capgemini Rennes

| Donnée | Valeur |
|--------|--------|
| Surface totale | 11 771 m² |
| Parking sous-dalle | 41 places |
| Parking sous-sol | 184 places |
| Parking aériens | 83 places |
| **Total parking** | **308 places** |
| Consommation énergie | 1 840 MWh/an (2025) |
| Collaborateurs | ~1 800 (affectés Rennes) |
| Postes de travail | 1 037 workstations |

---

## 2. Stack Technique

| Composant | Technologie | Statut | Palier |
|-----------|-------------|--------|--------|
| Frontend Web | Angular 17+ | Obligatoire | Palier 1 |
| Application Mobile | React Native | Obligatoire | Palier 1 |
| Backend | Java Spring Boot 3 | Obligatoire | Palier 1 |
| Base de données | PostgreSQL 15 | Obligatoire | Palier 1 |
| Authentification | JWT (Bearer Token) | Obligatoire | Palier 1 |
| Conteneurisation | Docker / Docker Compose | Recommandé | Palier 1 |
| Migrations BDD | Flyway | Recommandé | Palier 1 |
| Tâches planifiées | Spring `@Scheduled` | Recommandé | Palier 2 |
| Facteurs d'émission | ADEME Base Carbone | Obligatoire | Palier 1 |
| Export rapport | jsPDF / iText | Bonus | Palier 3 |

### Architecture globale

```
[ Nextjs (Web) ]  ──REST──►  [ Spring Boot API ]  ──►  [ PostgreSQL ]
[ React Native  ]  ──REST──►  [ Spring Boot API ]          ↑
                                      │                [ Scheduler Cron ]
                                      └──►  [ ADEME Emission Factors ]
```

---

## 3. Lancement rapide

**Prérequis :** Docker Desktop installé et démarré.

```bash
git clone https://github.com/votre-equipe/carbontrack.git
cd carbontrack
docker compose up --build
```

| Service | URL |
|---------|-----|
| Frontend Nextjs | http://localhost:3000 |
| API Spring Boot | http://localhost:8080 |
| PostgreSQL | localhost:5432 |


---

## 4. Fonctionnalités

### Palier 1 — Base fonctionnelle *(obligatoire)*

#### Authentification
- Inscription et connexion utilisateur
- JWT (Bearer Token) sur tous les endpoints REST
- Rôles `USER` et `ADMIN`

#### Gestion des sites
- Création d'un site avec :
  - Surface totale (m²)
  - Nombre de places de parking (sous-dalle, sous-sol, aériens)
  - Consommation énergétique annuelle (kWh)
  - Nombre d'employés et de postes de travail
  - Matériaux de construction + quantités (kg)
- Formulaire Nextjs avec validation des champs
- Stockage persistant PostgreSQL

#### Calcul CO₂
- Calcul automatique à la soumission du formulaire
- **Construction** : `Σ (quantité_kg × facteur_ADEME)`
- **Énergie** : `kWh × 0,0571 kgCO₂e/kWh` (mix électrique FR, RTE 2023)
- **Parking** : `places × facteur_ACV / 50 ans`
- Affichage immédiat du résultat dans Nextjs
- Historisation automatique en base (`calculation_history`)

#### Application mobile (React Native)
- Écrans de base : login, saisie rapide, résultat CO₂
- Authentification JWT partagée avec le backend
- Appels API REST identiques au front web

**Preuve de succès :** un site peut être saisi, son CO₂ calculé, le résultat affiché dans Nextjs, et la base contient au moins un historique.

---

### Palier 2 — Dashboard & Mobile complet

#### Dashboard Nextjs

KPIs affichés en temps réel :

| KPI | Formule | Unité |
|-----|---------|-------|
| CO₂ total | construction + énergie + parking | kgCO₂e |
| CO₂ / m² | CO₂ total ÷ surface | kgCO₂e/m² |
| CO₂ / employé | CO₂ total ÷ nb_employees | kgCO₂e/pers |
| Part construction | CO₂ construction ÷ CO₂ total × 100 | % |
| Part énergie | CO₂ énergie ÷ CO₂ total × 100 | % |
| Part parking | CO₂ parking ÷ CO₂ total × 100 | % |

Graphiques dynamiques (Chart.js / ng2-charts) :
- Pie chart : répartition construction / énergie / parking
- Bar chart : historique des calculs dans le temps
- Graphique linéaire : courbes d'évolution par site

#### Application mobile — fonctionnalités complètes
- Connexion JWT avec persistance du token
- Liste des sites de l'utilisateur
- Saisie rapide optimisée pour usage terrain
- Consultation des indicateurs simples par site

**Preuve de succès :** navigation complète dans le dashboard, graphiques visibles, mobile peut créer et consulter un site.

---

### Palier 3 — Fonctions avancées *(bonus)*

#### Comparaison multi-sites
- Sélection de 2 sites ou plus
- Tableau côte à côte (CO₂ total, CO₂/m², CO₂/employé)
- Classement du moins au plus émetteur
- Visualisation graphique des différences

#### Export PDF
- Rapport PDF par site : KPIs + graphiques + détail par catégorie
- Téléchargement direct depuis le dashboard Nextjs

#### Historisation avancée
- Courbes d'évolution sur plusieurs années
- Snapshot mensuel automatique via cron Spring
- Comparaison visuelle entre périodes

#### Intégrations externes *(bonus)*
- API ADEME Base Carbone pour facteurs dynamiques
- Mise à jour automatique des facteurs via scheduler

**Preuve de succès :** comparaison de 2 sites visualisée, rapport PDF généré, données externes enrichissent les calculs.

---

## 5. Schéma Base de Données

6 tables principales, toutes les PK en UUID :

```
users
  └──< sites (created_by)
         └──< site_materials (site_id)
         │      └──> emission_factors (emission_factor_id)
         └──< carbon_calculations (site_id)
         │      └──< calculation_history (calculation_id)
         └──< calculation_history (site_id)
```

### Tables

#### `users`
| Colonne | Type | Description |
|---------|------|-------------|
| id | UUID PK | Identifiant unique |
| email | VARCHAR UNIQUE | Email de connexion |
| password_hash | VARCHAR | Hash BCrypt |
| full_name | VARCHAR | Nom complet |
| role | VARCHAR | `USER` ou `ADMIN` |
| created_at | TIMESTAMP | Date de création |

#### `sites`
| Colonne | Type | Description |
|---------|------|-------------|
| id | UUID PK | Identifiant unique |
| created_by | UUID FK → users | Propriétaire |
| name | VARCHAR | Nom du site |
| address / city | VARCHAR | Localisation |
| surface_m2 | FLOAT | Surface totale (m²) |
| nb_employees | INT | Nombre d'employés |
| nb_workstations | INT | Postes de travail |
| parking_underground | INT | Places sous-dalle |
| parking_basement | INT | Places sous-sol |
| parking_outdoor | INT | Places aériens |
| energy_kwh_year | FLOAT | Consommation annuelle (kWh) |
| energy_source | VARCHAR | Clé facteur énergie (ex: `electricity_fr`) |

#### `emission_factors`
| Colonne | Type | Description |
|---------|------|-------------|
| id | UUID PK | Identifiant unique |
| category | VARCHAR | `construction`, `energy`, `parking` |
| material_name | VARCHAR | Nom du matériau |
| factor_kg_co2_per_kg | FLOAT | Facteur d'émission |
| unit | VARCHAR | `kg`, `kWh`, `place` |
| source | VARCHAR | `ADEME Base Carbone` |
| year | INT | Année de référence |

#### `site_materials`
| Colonne | Type | Description |
|---------|------|-------------|
| id | UUID PK | Identifiant unique |
| site_id | UUID FK → sites | Site concerné |
| emission_factor_id | UUID FK → emission_factors | Facteur utilisé |
| quantity_kg | FLOAT | Quantité (kg) |
| co2_kg | FLOAT | CO₂ calculé = quantité × facteur |

#### `carbon_calculations`
| Colonne | Type | Description |
|---------|------|-------------|
| id | UUID PK | Identifiant unique |
| site_id | UUID FK → sites | Site concerné |
| calculated_by | UUID FK → users | Auteur du calcul |
| co2_construction_kg | FLOAT | CO₂ construction |
| co2_energy_kg | FLOAT | CO₂ énergie |
| co2_parking_kg | FLOAT | CO₂ parking |
| co2_total_kg | FLOAT | Total CO₂ |
| co2_per_m2 | FLOAT | CO₂ / m² |
| co2_per_employee | FLOAT | CO₂ / employé |
| breakdown | JSONB | Détail par matériau |
| calculated_at | TIMESTAMP | Date du calcul |

#### `calculation_history`
| Colonne | Type | Description |
|---------|------|-------------|
| id | UUID PK | Identifiant unique |
| site_id | UUID FK → sites | Site concerné |
| calculation_id | UUID FK nullable | Calcul source |
| year | INT | Année de la mesure |
| co2_total_kg | FLOAT | CO₂ total enregistré |
| label | VARCHAR | Description (ex: "Snapshot mensuel 2025") |
| recorded_at | TIMESTAMP | Date d'enregistrement |

---

## 6. Facteurs d'émission ADEME

Intégrés au démarrage via `V2__seed_emission_factors.sql` :

### Matériaux de construction

| Matériau | Facteur | Unité | Source |
|----------|---------|-------|--------|
| Béton | 0,110 | kgCO₂e/kg | ADEME Base Carbone 2023 |
| Acier | 1,460 | kgCO₂e/kg | ADEME Base Carbone 2023 |
| Verre | 0,850 | kgCO₂e/kg | ADEME Base Carbone 2023 |
| Bois | 0,030 | kgCO₂e/kg | ADEME Base Carbone 2023 |
| Aluminium | 8,240 | kgCO₂e/kg | ADEME Base Carbone 2023 |
| Cuivre | 2,710 | kgCO₂e/kg | ADEME Base Carbone 2023 |
| PVC | 2,410 | kgCO₂e/kg | ADEME Base Carbone 2023 |
| Laine de verre | 1,280 | kgCO₂e/kg | ADEME Base Carbone 2023 |

### Énergie

| Source | Facteur | Unité | Source |
|--------|---------|-------|--------|
| Électricité FR | 0,0571 | kgCO₂e/kWh | RTE / ADEME 2023 |
| Gaz naturel | 0,2270 | kgCO₂e/kWh | ADEME Base Carbone 2023 |
| Fioul | 0,3240 | kgCO₂e/kWh | ADEME Base Carbone 2023 |

### Parking (ACV amorti sur 50 ans)

| Type | Facteur | Unité | Source |
|------|---------|-------|--------|
| Sous-sol / sous-dalle | 2 400 | kgCO₂e/place | ADEME ACV bâtiment |
| Aérien | 600 | kgCO₂e/place | ADEME ACV bâtiment |

> **Note :** le CO₂ parking annualisé se calcule ainsi : `nb_places × facteur / 50`

---

## 7. API REST

Tous les endpoints (sauf auth) nécessitent le header : `Authorization: Bearer <token>`

### Authentification

```
POST   /api/auth/register       Créer un compte
POST   /api/auth/login          Obtenir un token JWT
```

### Sites

```
GET    /api/sites               Lister les sites de l'utilisateur
POST   /api/sites               Créer un nouveau site
GET    /api/sites/{id}          Détail d'un site
PUT    /api/sites/{id}          Modifier un site
DELETE /api/sites/{id}          Supprimer un site
```

### Calculs CO₂

```
POST   /api/sites/{id}/calculate        Déclencher un calcul
GET    /api/sites/{id}/calculations     Historique des calculs
GET    /api/sites/{id}/latest           Dernier calcul
GET    /api/compare?ids=id1,id2,id3     Comparer plusieurs sites  [Palier 3]
```

### Historique

```
GET    /api/sites/{id}/history          Courbes d'évolution
GET    /api/sites/{id}/history/{year}   Snapshot d'une année
```

### Facteurs d'émission

```
GET    /api/emission-factors              Tous les facteurs
GET    /api/emission-factors?cat=energy   Filtrés par catégorie
```

---



## 12. Équipe

| Rôle | Responsabilités principales | Priorité J1 |
|------|-----------------------------|-------------|
| Tech Lead / Archi | Docker Compose, JWT, review inter-couches | Matin — critique |
| Backend (Spring Boot) | API REST, calcul CO₂, cron, Flyway | J1 complet — critique |
| Frontend (Nextjs) | Formulaire, dashboard, Chart.js, PDF | Après-midi J1 — critique |
| Mobile (React Native) | Login, saisie terrain, indicateurs | Après-midi J1 — critique |
| Chef de Projet / PO | Vision produit, backlog, pitch vidéo | Transverse J1+J2 |

---

*Hackathon #26 — Capgemini × SUP Vinci — Rennes, 16 & 17 Mars 2026*