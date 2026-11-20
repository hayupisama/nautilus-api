# CLAUDE.md — nautilus-api

## Rôle

API unique du projet : comptes, profils de besoins, chauffeurs et véhicules, recherche et matching, réservations, notifications, journal d'audit.

## État

Dépôt vide. Première tâche : générer le squelette.

```bash
quarkus create app fr.nautilus:nautilus-api --java=21 --maven \
  --extension=rest-jackson,hibernate-orm-panache,jdbc-postgresql,flyway,oidc,websockets-next,scheduler,smallrye-health,micrometer-registry-prometheus,smallrye-openapi,hibernate-validator
```

Vérifier ensuite le support de Hibernate Spatial (PostGIS) pour la version de Quarkus utilisée et l'ajouter.

## Architecture : hexagonale par module

```
src/main/java/fr/nautilus/
  <module>/                       # account, needsprofile, driver, vehicle, search, booking, notification, audit
    domain/                       # Java pur : entités, value objects, services, ports (interfaces). AUCUNE annotation framework.
    application/                  # cas d'usage (orchestration), transactions
    adapter/in/rest/              # implémente les interfaces générées depuis nautilus-contrat-api
    adapter/in/ws/                # WebSockets Next (module booking : position chauffeur)
    adapter/out/persistence/      # entités JPA + repositories Panache (PanacheRepository, pas d'active record)
    adapter/out/<service>/        # OSRM, géocodage, FCM, Brevo...
  shared/                         # types transverses minimaux
```

Règles :
- Le domaine ne dépend ni de Quarkus, ni de JPA, ni de Jackson. Mapping explicite domaine <-> entité JPA dans l'adaptateur.
- Un module n'appelle un autre module que via ses ports `application`, jamais ses adaptateurs.
- Interfaces REST générées depuis le contrat (openapi-generator, interfaces JAX-RS uniquement). Ne jamais modifier le code généré.

## Données

- Migrations Flyway : `src/main/resources/db/migration/V<AAAAMMJJHHMM>__<description>.sql`. Jamais modifier une migration déjà fusionnée.
- PostGIS : départ/arrivée en `geography(Point,4326)`, zones chauffeur en `geography(Polygon,4326)`.
- Positions GPS purgées après la course ; comptes inactifs supprimés après 1 an.
- Tâches planifiées : table `deadline` (type, échéance, payload, état). Un `@Scheduled` traite les échéances dues, de façon idempotente (rappels, expiration des demandes à 1 h, purges).

## Règles métier à ne pas casser

- Réservation planifiée uniquement au lot 0/1, au moins 12 h à l'avance.
- Statuts : REQUESTED -> ACCEPTED -> DRIVER_EN_ROUTE -> DRIVER_ARRIVED -> IN_PROGRESS -> COMPLETED ; sorties REJECTED, EXPIRED, CANCELLED_BY_PASSENGER, CANCELLED_BY_DRIVER.
- Matching = filtre par contraintes bloquantes (véhicule/fauteuil, rampe si transfert impossible, aide requise, chien, places) puis tri choisi par le passager. Pas d'attribution automatique.
- La plateforme ne fixe pas les prix et n'encaisse rien : elle calcule une estimation à partir de la grille du chauffeur.
- Annulations tardives comptées : avertissement à la 2e, suspension temporaire à la 3e sur 30 jours.
- Toute validation de chauffeur et tout changement de statut écrit une ligne d'audit.

## Tests

- Unitaires du domaine (JUnit 5, sans Quarkus) : matching, transitions de statut, calcul de prix. Priorité absolue.
- Intégration : `@QuarkusTest` + Dev Services (PostgreSQL, Keycloak).
- Build natif vérifié en CI à chaque fusion ; JVM en dev.

## Contexte projet (commun)

- Projet : **Nautilus** (nom de code), appli VTC marketplace dédiée aux PMR, lancement à Nantes (métropole + aéroport + gare).
- Side project, développeur unique, budget quasi nul, < 5 h/semaine, démarrage janvier 2027.
- Référence fonctionnelle : `nautilus/docs/cahier-des-charges.md`. Glossaire : `nautilus/docs/glossaire.md`. Décisions transverses : `nautilus/docs/decisions.md`.
- Lot en cours : **lot 0 – prototype** avec données fictives. Objectif : un prototype fonctionnel et démontrable. La validation terrain est faite par un autre intervenant.
- Licence : PolyForm Noncommercial 1.0.0. Ne jamais ajouter de dépendance dont la licence interdit cet usage.

## Règles transverses

- Langue : identifiants de code en anglais (voir glossaire FR -> EN) ; docs, commits et messages à l'utilisateur en français.
- Commits : Conventional Commits.
- Mode de travail : Claude développe seul et pilote le projet en Scrum autogéré. Fouad est le client (MOA/MOE) : il supervise et teste, il ne lit pas les tickets. Processus complet : `nautilus/.claude/scrum/PROCESS.md` (à lire avant toute action sur le backlog).
- Autonomie : branches, commits, push de branches, PR, fusion après CI verte, gestion des issues et du tableau de projet sont autorisés sans demander. Demander à Fouad avant : déploiement en production, secret ou service payant, publication sur les stores, opération destructive (force push, suppression), changement de périmètre ou de règle métier du cahier des charges.
- Accessibilité WCAG 2.2 AA obligatoire sur toute interface : c'est un critère d'acceptation, pas une option.
- Données : profil de besoins = besoins fonctionnels, jamais de pathologie. Aucune donnée personnelle réelle dans le code, les tests ou les fixtures.
- Secrets : jamais dans le dépôt (même privé).
- Contrat d'API : la source de vérité est `nautilus-contrat-api`. Ne jamais modifier un client généré à la main.

## Sessions Claude Code

- Au démarrage, le dernier résumé est injecté automatiquement (hook SessionStart). Commande `/reprise` pour reprendre proprement.
- En fin de session, ou quand l'utilisateur annonce une pause : `/cloture`.
- Protocole : `.claude/sessions/_PROTOCOLE.md`. Ce dossier est réservé à Claude Code ; ne pas le mentionner dans la documentation destinée aux humains.
