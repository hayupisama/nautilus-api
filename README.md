# nautilus-api

API Quarkus (hexagonale par module) du projet Nautilus. Voir `CLAUDE.md` pour l'architecture.

## Prerequis

JDK 21 et Docker (Dev Services : PostgreSQL/PostGIS et Keycloak jetables).

## Commandes

- `./mvnw quarkus:dev` : mode dev (Flyway applique les migrations au demarrage).
- `./mvnw verify` : tests (dont tests d'architecture ArchUnit) et build JVM.
- `./mvnw verify -Dnative -Dquarkus.native.container-build=true` : build natif.

Licence : PolyForm Noncommercial 1.0.0.
