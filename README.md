# RIEAU API

> API backend de RIEAU

## Développement

### Prérequis

* Java 8, par exemple AdoptOpenJDK 8 installé depuis [sdkman](https://sdkman.io).

* Adaptez les variables à chaque environnement:

```
cp src/main/resources/application-dev.properties.sample src/main/resources/application-dev.properties
```

* Activez les environnements en ajoutant:

```
-Dspring.profiles.active=test
```

### Dev

* Maven 3.6+, par exemple installé depuis [sdkman](https://sdkman.io).
* Spring boot CLI 2.1+, par exemple installé depuis [sdkman](https://sdkman.io).

### Lancement

```
./mvnw spring-boot:run -Dspring.profiles.active=test
```

### Tests unitaires

* Lancez tous les tests:

```
./mvnw test -Dspring.profiles.active=test
```

* Lancez un seul test:

```
./mvnw test -Dspring.profiles.active=test -Dtest=
```

### Construction

```
./mvnw package -Dspring.profiles.active=test
```