# RIEAU API

> API backend de RIEAU

## Développement

### Prérequis

* Java 8, par exemple AdoptOpenJDK 8 installé depuis [sdkman](https://sdkman.io).

* Adaptez les variables à chaque environnement:

```
cp src/main/resources/application-{env}.properties.sample src/main/resources/application-{env}.properties
```

env = test, dev ou production

* Activez les environnements en ajoutant:

```
-Dspring.profiles.active=test
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
./mvnw test -Dspring.profiles.active=test
```

* Lancez un seul test:

```
./mvnw test -Dtest=<nomdelaclasse> -Dspring.profiles.active=test
```

### Construction

Pour la prod:

```
./mvnw package
```