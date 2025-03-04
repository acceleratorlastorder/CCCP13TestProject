# CCCP13 AUTH SERVICE

# Architecture:

```mermaid
graph TB
   AD["<div style='color:black'>Azure Active Directory</div>"] <--> |"OpenID Connect / OAuth2"| KC["<div style='color:black'>Keycloak Server</div>"]
   KC --> |"User & Role Sync"| DB[("<div style='color:black'>PostgreSQL DB</div>")]
   DB --> |"User Data & Roles"| FE["<div style='color:black'>Frontend Angular</div>"]
   DB --> |"User Data & Roles"| BE["<div style='color:black'>Backend Spring</div>"]
    
   %% Authentication Flow
   FE -->|Login Request| KC
   KC -->|Token Response| FE

   FE -- "Token-based Auth (JWT)" --> BE
   BE -->|Token Validation Request| KC
   KC -->|Validation Response| BE
    
   style AD fill:#f0f0f0,stroke:#333,stroke-width:2px
   style KC fill:#e1f5fe,stroke:#0288d1,stroke-width:2px
   style DB fill:#e8f5e9,stroke:#388e3c,stroke-width:2px
   style FE fill:#fff3e0,stroke:#f57c00,stroke-width:2px
   style BE fill:#fff3e0,stroke:#f57c00,stroke-width:2px
```

Mode Online:
1. L'utilisateur se connecte via Azure AD
2. Keycloak valide avec Azure AD et génère un token
3. Le token est utilisé pour toutes les requêtes API

Mode Offline:
1. Les tokens et profils sont mis en cache
2. Authentication locale via Keycloak
3. Synchronisation périodique avec Azure AD

## Flux d'authentification détaillé:

### Mode Online:

```mermaid
sequenceDiagram
    participant A as Angular
    participant B as Backend
    participant K as Keycloak
    participant AD as Azure AD
    participant DB as PostgreSQL

    A->>B: 1. Accès
    B-->>A: 2. Redirection Keycloak
    A->>K: 3. Demande authentification
    K->>AD: 4. Auth Azure
    AD-->>K: 5. Token
    K->>DB: 6. Cache User
    K-->>A: 7. Token JWT Keycloak
    A->>B: 8. Requête API + Token
    B->>K: 9. Validation
    B-->>A: 10. Réponse API
```

### Mode Offline:

```mermaid
sequenceDiagram
    participant A as Angular
    participant B as Backend
    participant K as Keycloak
    participant DB as PostgreSQL

    A->>B: 1. Requête API
    B->>K: 2. Validation
    K->>DB: 3. Check Cache
    DB-->>K: 4. User Data
    K-->>B: 5. Token OK
    B-->>A: 6. Réponse
```

## Composants principaux:

1. **Azure Active Directory**
   - Fournisseur d'identité principal (IdP)
   - Gestion des utilisateurs et groupes
   - Authentification primaire

2. **Keycloak Server**
   - Serveur d'authentification intermédiaire
   - Gestion des tokens et sessions
   - Cache des profils utilisateurs
   - Mode de fonctionnement hybride (online/offline)

3. **PostgreSQL Database**
   - Stockage persistant pour Keycloak
   - Cache des utilisateurs et rôles
   - Réplication pour la haute disponibilité

4. **Frontend Angular**
   - Interface utilisateur moderne
   - Gestion du state de connexion
   - Support du mode hors ligne
   - Composants réutilisables pour l'authentification

5. **Backend Spring**
   - API REST sécurisées
   - Validation des tokens
   - Support du mode dégradé
   - Monitoring et métriques

## Composants et flux de données:

```mermaid
flowchart TB
    subgraph Azure["<div style='color:black'>Azure Active Directory</div>"]
        Users[Users & Groups]
        Auth[Authentication]
    end

    subgraph Keycloak["<div style='color:black'>Keycloak Server</div>"]
        TokenMgmt[Token Management]
        Cache[User Cache]
        Sync[Synchronization]
    end

    subgraph Storage["<div style='color:black'>PostgreSQL Database</div>"]
        UserDB[User Data]
        RoleDB[Role Data]
        TokenDB[Token Cache]
    end

    subgraph Frontend["<div style='color:black'>Frontend Angular</div>"]
        UI[User Interface]
        AuthState[Auth State]
        OfflineSupport[Offline Mode]
    end

    subgraph Backend["<div style='color:black'>Spring Backend</div>"]
        API[REST APIs]
        TokenValidation[Token Validation]
        Monitoring[Health Check]
    end

    Azure --> Keycloak
    Keycloak --> Storage
    Storage --> Frontend
    Storage --> Backend
    Frontend --> Backend

    style Azure fill:#f0f0f0,stroke:#333
    style Keycloak fill:#e1f5fe,stroke:#0288d1
    style Storage fill:#e8f5e9,stroke:#388e3c
    style Frontend fill:#fff3e0,stroke:#f57c00
    style Backend fill:#fff3e0,stroke:#f57c00
```
## Stratégie de résilience:

1. **Mise en cache**
   - Tokens d'authentification
   - Profils utilisateurs
   - Rôles et permissions

2. **Synchronisation**
   - Périodique avec Azure AD
   - Réplication de la base PostgreSQL
   - Export régulier des données critiques

3. **Haute disponibilité**
   - Cluster Keycloak
   - Base de données répliquée
   - Monitoring continu

# Configuration de Keycloak avec Azure AD

## Étape 1: Configuration côté Azure AD

1. Connectez-vous au [portail Azure](https://portal.azure.com/)
2. Accédez à "Azure Active Directory" > "Inscriptions d'applications" > "Nouvelle inscription"
3. Nommez l'application (ex: "CCCP13-Keycloak-Integration")
4. Définissez l'URI de redirection (Web): `https://[votre-serveur-keycloak]/auth/realms/CCCP13/broker/azuread/endpoint`
5. Cliquez sur "Enregistrer" et notez l'ID d'application (client) et le répertoire (tenant) ID
6. Sous "Certificats et secrets", créez un nouveau secret client et notez sa valeur
7. Sous "Autorisations API", ajoutez les autorisations Microsoft Graph suivantes:
   - User.Read (déléguée)
   - Directory.Read.All (déléguée)
   - OpenId permissions, email, profile

## Étape 2: Configuration de Keycloak

### Création du Realm CCCP13

1. Accédez à la console d'administration de Keycloak (`https://[votre-serveur-keycloak]/auth/admin`)
2. Créez un nouveau realm nommé "CCCP13"

### Configuration du fournisseur d'identité Azure AD

1. Dans le realm CCCP13, accédez à "Identity Providers" > "Add provider" > "OpenID Connect v1.0"
2. Configurez les paramètres:
   - Alias: `azuread`
   - Display Name: `Microsoft Office 365`
   - Discovery URL: `https://login.microsoftonline.com/{tenant-id}/v2.0/.well-known/openid-configuration`
   - Client ID: `{votre-client-id-azure}`
   - Client Secret: `{votre-client-secret-azure}`
   - Scope: `openid email profile`
   - Prompt: `login`
   
3. Dans "Advanced Settings":
   - Store Tokens: `ON` permet de réutiliser les token d'auth pendant la durée de validité sans avoir à questioner azure AD
   - Stored Tokens Readable: `OFF` normalement déjà OFF par défaut, il permet de ne pas les store en clair
   - Sync Mode: `force` update les info de l'user depuis AD à chaque connection

### Configuration du mode Failover

1. Accédez à "Realm Settings" > "Cache Policy"
2. Configurez "Default Max Lifespan of User Cache" à une valeur élevée (ex: 72 heures)
3. Dans "Authentication" > "Flows" > "Browser", modifiez le flow pour permettre les modes de fallback

## Étape 3: Configuration de la stratégie de maintien du service en cas de panne

1. Créez un script de synchronisation périodique:
   - Ajoutez une tâche planifiée qui exporte régulièrement les utilisateurs et rôles
   - Configurez une réplication de la base de données PostgreSQL

2. Configuration de haute disponibilité:
   - Déployez Keycloak en cluster
   - Mettez en place une réplication de la base de données

3. Configuration du mode dégradé:
   - Ajoutez une règle dans les flux d'authentification pour permettre l'authentification locale quand Azure n'est pas disponible 


### Limitations:
 - Les users doivent se connecter au moins une fois avant d'être ajouté sur la base keycloak, cependant il est possible de rajouter un script de synchro périodique plus tard
