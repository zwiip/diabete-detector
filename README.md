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
- Spring Boot (+ Data JPA, Data Mongo DB, Security)
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