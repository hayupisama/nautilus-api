# nautilus-api

API du projet Nautilus (nom de code) : réservation de VTC dédiée aux PMR.

- Quarkus, Java 21, Maven
- PostgreSQL + PostGIS, Flyway
- Keycloak (OIDC), WebSockets Next pour le suivi en temps réel
- Contrat : [nautilus-contrat-api](https://github.com/hayupisama/nautilus-contrat-api)

## Démarrer (une fois le squelette généré)

```bash
./mvnw quarkus:dev   # Dev Services démarre PostgreSQL et Keycloak en conteneurs
```

## Licence

[PolyForm Noncommercial 1.0.0](LICENSE)
