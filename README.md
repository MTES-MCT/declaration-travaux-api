# RIEAU API

> API backend de RIEAU

## Développement

### Prérequis

* Java 8, par exemple AdoptOpenJDK 8 installé depuis [sdkman](https://sdkman.io).

* Adaptez les variables d'environnement:

```
cp src/main/resources/application.properties.sample src/main/resources/application.properties
```

### Dev

* Maven 3.6+, par exemple installé depuis [sdkman](https://sdkman.io).
* Spring boot CLI 2.1+, par exemple installé depuis [sdkman](https://sdkman.io).

### Lancement

```
./mvnw spring-boot:run
```

### Tests unitaires

```
./mvnw test
```

### Construction

```
./mvnw clean package
```