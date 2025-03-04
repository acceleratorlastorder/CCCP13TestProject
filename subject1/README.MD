# CCCP13 TEST


## Configuration Keycloak avec Azure

Pour des informations détaillées sur l'intégration de Keycloak avec Azure AD, veuillez consulter le [Guide de Configuration Keycloak Azure](keycloak-azure-config.md).

## Configuration du Frontend

Il faudra prévoir un client Keycloak pour le frontend.

## Configuration du Backend

Il faudra prévoir un client Keycloak pour le backend.


#### Déroulé d'une connexion :

1. L'utilisateur tente d'accéder à une page protégée par Keycloak
2. Le backend vérifie que le jeton est valide
3. Si le jeton est invalide, le backend redirige vers Keycloak ou un frontend de notre choix si configuré
4. L'utilisateur se connecte via Keycloak
5. Keycloak authentifie l'utilisateur et renvoie un jeton au client
6. Le client stocke le jeton et l'utilise pour accéder à la ressource protégée


Il est possible aussi d'avoir un serveur front dédier à la page d'authentification de Keycloak, et après connection successful il peut rediriger au referer.
