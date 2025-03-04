# CCCP13 - Docker Salary App

## Description
Un début d'application de gestion des salaires des dockers avec calcul automatique des primes et bonus basé sur les règles métier.
Cela permet de voir la vision global du projet et de voir les points à améliorer.
Avec ce projet et quelque ajustement côté front et back, il est possible facilement d'ajouter en base des règles de calcul pour les primes et bonus, simplement depuis l'interface web, sans avoir besoin de modifier le code.
Côté back il faudrait utiliser plus de DTOs pour les échanges entre le front et le back, mais cela nécessiterait plus de temps pour mettre en place.

## Architecture

[Architecture](./architecture.md)

## Technologies Utilisées
- **Backend**: Java 21, Spring Boot
- **Frontend**: Angular 19
- **Base de données**: PostgreSQL
- **Cache**: Redis

## Installation

### Backend
1. Cloner le repository
2. Configurer la base de données dans `application.properties`
3. Exécuter `mvn clean install`
4. Lancer l'application avec `mvn spring-boot:run`

### Frontend
1. Naviguer vers le dossier `salary-app`
2. Installer les dépendances: `npm install`
3. Lancer l'application: `ng serve`
4. Accéder à l'application sur `http://localhost:4200`

## Affichages
![alt text](./salary-calculations.png)



## Points à Améliorer
- Ajouter un formulaire Angular pour la gestion des règles de calcul
- Renforcer les tests pour la validation des règles de calcul
- Ajouter des tests d'intégration pour les scénarios complexes
- Implémenter l'export des données en différents formats
- Ajouter des graphiques pour la visualisation des statistiques
- Améliorer la gestion des erreurs côté frontend
- Ajouter des validations côté client
- Ajouter un dockerfile pour le frontend et le backend
- Ajouter un docker-compose pour lancer l'application
