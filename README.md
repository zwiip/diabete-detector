# Medilabo solutions: Diabetes-detector
## Contexte
*Ce projet est le neuvième et dernier projet de la formation Développement d'applications Java d'OpenClassrooms.*

Il s'agit d'une application en microservices permettant d'évaluer le risque de diabète d'un patient à partir de ses données médicales et des notes cliniques associés.

## Objectifs
L'objectif de l'application est de :
- Gérer les informations patients,
- Enregistrer les notes médicales,
- Analyser le risque de diabète selon des mots "déclencheurs" présents dans les notes.

## Architecture
L'application repose sur une architecture microservices conteneurisée avec Docker.
### Services :
- Gateway : gestion de l'authentification et du routage des requêtes vers les services correspondants,
- front-service : interface web + gestion des requêtes utilisateur,
- patient-service : gestion des données patients (MySQL),
- note-service : gestion des notes médicales (MongoDB),
- assessment-service : calcul du niveau de risque.

## Technologies principales utilisées
- Java 21,
- Ecosystème Spring : 
  - Spring Boot,
  - Spring Data JPA,
  - Spring Data Mongo DB,
  - Spring Security,
  - Spring Cloud Gateway,
- Thymeleaf,
- MySQL,
- MongoDB,
- Docker,
- Maven,
- Junit5,
- Mockito,
- Git.

## Prérequis
- Java 21,
- Maven 3.9+,
- Docker Desktop,
- Git
Le projet est paramétré pour peupler automatiquement les base de données grâce à hibernate pour MySQL et un CommandLineRunner pour MongoDB.

## Lancement du projet
### 1, Cloner le repository,
- git clone https://github.com/zwiip/diabete-detector/
- cd diabete-detector

### 2, Construire les images Docker
- docker-compose build

### 3, Lancer les conteneurs
- docker-compose up

### 4, Accéder à l'application
- Dans un navigateur : http://localhost:8080

### 5, Se connecter
- Login : doctor
- mdp : doctor-secret

## Focus Green Code
Parallèlement au développement, il est demandé pour ce projet de mener des recherches sur le Green Code.

Le green code, ou écoconception logicielle, désigne l'ensemble des pratiques permettant de développer des applications en réduisant leur consommation de ressources afin de réduire leur impact environnemental.

### Les "bons points" du projet

Même sans démarche Green Code formalisée, certains choix techniques contribuent à une sobriété numérique :
#### L'architecture microservices conteneurisée (Docker)

Permet :
- Une allocation contrôlée des ressources,
- Un déploiement isolé et optimisé,
- Une meilleure scalabilité.

#### La séparation des responsabilités
Chaque microservice a une responsabilité limitée.
Cela limite les traitements inutiles et favorise l'optimisation ciblée.

#### Utilisation de DTO pour les échanges API
Les données échangées sont structurées et limitées, ce qui évite l'exposition d'entités complètes et réduit le volume transféré.

#### Bases de données dédiées par service
Evite les requêtes croisées complexes et réduit les traitements lourds.

### Les pistes d'améliorations
#### Optimisation des requêtes SQL
- Ajout d’index sur les colonnes fréquemment interrogées
- Suppression des SELECT *
- Analyse des plans d’exécution

#### Mise en place d’un cache applicatif
- Cache des patients ou des notes fréquemment consultés
- Réduction des appels inter-services

#### Réduction du volume des réponses API
- DTO encore plus ciblés
- Pagination systématique

#### Optimisation des images Docker
- Utilisation d’images légères (ex : Alpine)
- Multi-stage build
- Définition de limites CPU/mémoire

#### Gestion responsable des logs
- Réduction du niveau de log en production
- Rotation automatique des logs

#### Monitoring et mesure
- Intégration d’outils de mesure d’empreinte (ex : plugin type Scaphandre)
- Analyse des pics de consommation CPU/mémoire