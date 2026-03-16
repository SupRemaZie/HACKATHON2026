# Swagger / OpenAPI Documentation

## Accès à la documentation

Une fois que l'API Spring Boot est lancée, accédez à la documentation Swagger UI :

**URL de Swagger UI :** `http://localhost:8080/swagger-ui.html`

**JSON OpenAPI :** `http://localhost:8080/api-docs`

---

## Structure de l'API

Tous les endpoints (sauf `/api/auth/register` et `/api/auth/login`) nécessitent une authentification **Bearer Token** :

```
Authorization: Bearer <token_jwt>
```

### 📋 Endpoints disponibles

#### **1. Authentification** (`/api/auth`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/auth/register` | Créer un compte utilisateur |
| POST | `/api/auth/login` | Obtenir un token JWT |

**Réponse** : `AuthResponse { token, type, email, role }`

---

#### **2. Gestion des sites** (`/api/sites`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/sites` | Lister tous les sites de l'utilisateur |
| POST | `/api/sites` | Créer un nouveau site |
| GET | `/api/sites/{id}` | Récupérer les détails d'un site |
| PUT | `/api/sites/{id}` | Modifier un site |
| DELETE | `/api/sites/{id}` | Supprimer un site |

**Schéma** : `SiteResponse { id, name, address, city, surfaceM2, nbEmployees, nbWorkstations, parkingUnderground, parkingBasement, parkingOutdoor, energyKwhYear, energySource, createdAt, updatedAt }`

---

#### **3. Calculs CO₂** (`/api/sites/{id}`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/sites/{id}/calculate` | Déclencher le calcul CO₂ |
| GET | `/api/sites/{id}/calculations` | Historique des calculs |
| GET | `/api/sites/{id}/latest` | Dernier calcul effectué |
| GET | `/api/sites/{id}/history` | Courves d'évolution (historisées) |
| GET | `/api/sites/{id}/history/{year}` | Données d'une année spécifique |
| GET | `/api/compare?ids=id1,id2,id3` | Comparer plusieurs sites (Palier 3) |

**Schémas** :
- `CarbonCalculationResponse` : `{ id, siteId, co2ConstructionKg, co2EnergyKg, co2ParkingKg, co2TotalKg, co2PerM2, co2PerEmployee, breakdown, calculatedAt }`
- `CalculationHistoryResponse` : `{ id, siteId, year, co2TotalKg, label, recordedAt }`
- `SiteComparisonResponse` : `{ siteId, siteName, surfaceM2, nbEmployees, co2TotalKg, co2PerM2, co2PerEmployee, constructionPercent, energyPercent, parkingPercent }`

---

#### **4. Facteurs d'émission ADEME** (`/api/emission-factors`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/emission-factors` | Tous les facteurs (construction, énergie, parking) |
| GET | `/api/emission-factors?category=energy` | Facteurs filtrés par catégorie |

**Schéma** : `EmissionFactorResponse { id, category, materialName, factorKgCo2PerKg, unit, source, year }`

---

## Exemple d'utilisation

### 1️⃣ Inscription

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "MySecurePassword123!",
    "fullName": "Jean Dupont"
  }'
```

**Réponse** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "email": "user@example.com",
  "role": "USER"
}
```

### 2️⃣ Connexion

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "MySecurePassword123!"
  }'
```

### 3️⃣ Créer un site

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8080/api/sites \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Capgemini Rennes",
    "address": "Avenue de Joinville",
    "city": "Rennes",
    "surfaceM2": 11771,
    "nbEmployees": 1800,
    "nbWorkstations": 1037,
    "parkingUnderground": 41,
    "parkingBasement": 184,
    "parkingOutdoor": 83,
    "energyKwhYear": 1840000,
    "energySource": "electricity_fr"
  }'
```

### 4️⃣ Calculer le CO₂

```bash
SITE_ID="550e8400-e29b-41d4-a716-446655440000"

curl -X POST http://localhost:8080/api/sites/$SITE_ID/calculate \
  -H "Authorization: Bearer $TOKEN"
```

### 5️⃣ Récupérer les facteurs d'émission

```bash
curl http://localhost:8080/api/emission-factors?category=energy
```

---

## Notes d'implémentation

- ✅ **Swagger / OpenAPI générés automatiquement** via Springdoc
- ✅ **JWT Bearer Token** configuré dans le schéma de sécurité
- ✅ **Tous les DTOs documentés** avec exemples
- ✅ **Codes d'erreur** inclus (400, 401, 404, 500)
- ⏳ **Controllers implémentés en stubs** (les logiques métier sont à venir)

---

## Prochaines étapes

1. Implémenter les **entities JPA** (User, Site, CarbonCalculation, etc.)
2. Implémenter les **repositories** Hibernate
3. Implémenter la **logique JWT** (génération, validation)
4. Implémenter les **services métier** (calcul CO₂, etc.)
5. Implémenter les **contrôleurs** efficacement
6. Tester les routes avec `curl` ou Swagger UI

---

**Généré le 16/03/2026 pour le Hackathon #26**
