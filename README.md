# nautilus-api

API du projet Nautilus (nom de code) : réservation de VTC dédiée aux PMR.

- Quarkus 3.40, Java 21, Maven
- PostgreSQL + PostGIS, Flyway
- Keycloak (OIDC), WebSockets Next pour le suivi en temps réel
- Contrat : [nautilus-contrat-api](https://github.com/hayupisama/nautilus-contrat-api) (copie versionnée dans `src/main/openapi/`, version et sha256 dans `CONTRACT_VERSION`)

Voir `CLAUDE.md` pour l'architecture hexagonale par module.

## Prérequis

JDK 21 et Docker (Dev Services : PostgreSQL/PostGIS et Keycloak jetables).

## Commandes

- `./mvnw quarkus:dev` : mode dev (Flyway applique les migrations au démarrage).
- `./mvnw verify` : tests (dont les tests d'architecture ArchUnit) et build JVM.
- `./mvnw verify -Dnative -Dquarkus.native.container-build=true` : build natif.
- `scripts/sync-contract.sh <tag>` : met à jour la copie du contrat depuis un tag (nécessite `gh` authentifié) ; la CI vérifie le sha256.

Les endpoints métier sont sous `/v1` ; les endpoints techniques (`/q/health`, `/q/metrics`, `/q/openapi`) restent sous `/q`.

## Licence

[PolyForm Noncommercial 1.0.0](LICENSE)
