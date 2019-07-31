# RIEAU API

[![CircleCI](https://circleci.com/gh/MTES-MCT/rieau-api/tree/master.svg?style=svg)](https://circleci.com/gh/MTES-MCT/rieau-api/tree/master)

> API backend de RIEAU

## Développement

### Prérequis

* Java 8, par exemple AdoptOpenJDK 8 installé depuis [sdkman](https://sdkman.io).

* Adaptez les variables à chaque environnement:

```
cp src/main/resources/application-{env}.properties.sample src/main/resources/application-{env}.properties
```

env = test, dev, staging ou production

* Activez les environnements en ajoutant:

```
-Dspring.profiles.active=<env>
```

### Dev

* Maven 3.6+, par exemple installé depuis [sdkman](https://sdkman.io).
* Spring boot CLI 2.1+, par exemple installé depuis [sdkman](https://sdkman.io).

### Lancement

```
./mvnw spring-boot:run
```

### Tests unitaires

* Lancez tous les tests:

```
./mvnw test
```

* Lancez une seule classe de test:

```
./mvnw test -Dtest=<nomdelaclasse> 
```

* Lancez une seule méthode de test:

```
./mvnw test -Dtest=<nomdelaclasse>#<nomdelamethode>
```

### Tests d'intégration

* Nécessitent une base de données PostgreSQL:

```
docker-compose -f src/main/docker/stack.yml up --build -d
```

* Lancez tous les tests:

```
./mvnw test -Dspring.profiles.active=staging
```

### Vérification des vulnérabilités

```
./mvnw verify 
```

### Construction

Pour la prod:

```
./mvnw package -Dspring.profiles.active=production
```