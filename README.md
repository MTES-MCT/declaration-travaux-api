# RIEAU API

[![CircleCI](https://circleci.com/gh/MTES-MCT/rieau-api/tree/master.svg?style=svg)](https://circleci.com/gh/MTES-MCT/rieau-api/tree/master)

> API backend de RIEAU

## Développement

### Prérequis

* Java 11, par exemple AdoptOpenJDK 11 installé depuis [sdkman](https://sdkman.io).

* Adaptez les variables à l'environnement de `dev`:

```shell
cp src/main/resources/application-{env}.properties.sample src/main/resources/application-{env}.properties
```

* Activez l'environnement de `dev` en ajoutant:

```shell
-Pdev
```

Ce dernier bouchonne la persistence des fichiers en filesystem, des données en mémoire dans une base H2 et l'authentification est basique avec Spring security.

Sinon, par défaut c'est l'environnement d'intégration qui est activé. Ce dernier intègre la persistence des fichiers avec un serveur Minio, des données avec une base PostgreSQL et l'authentification avec un serveur Oauth2/OIDC Keycloak. Ces derniers doivent être lancés en préalable avec une stack docker-compose en phase de développement:

```shell
docker-compose -f src/main/docker/docker-compose.test.yml up --build -d
```

Exporter le realm de test avec les users:

```shell
docker exec -it keycloak keycloak/bin/standalone.sh \
-Djboss.socket.binding.port-offset=100 \
-Dkeycloak.migration.action=export \
-Dkeycloak.migration.provider=dir \
-Dkeycloak.migration.realmName=rieau \
-Dkeycloak.migration.usersExportStrategy=REALM_FILE \
-Dkeycloak.migration.dir=/tmp/export
```

Importer le realm de test avec les users:

```shell
docker exec -it keycloak keycloak/bin/standalone.sh \
-Djboss.socket.binding.port-offset=100 \
-Dkeycloak.migration.action=import \
-Dkeycloak.migration.provider=singleFile \
-Dkeycloak.migration.file=/tmp/realm-rieau-test.json \
-Dkeycloak.migration.strategy=OVERWRITE_EXISTING
```

### Dev

* Maven 3.6+, par exemple installé depuis [sdkman](https://sdkman.io).
* Spring boot CLI 2.1+, par exemple installé depuis [sdkman](https://sdkman.io).

### Lancement

```shell
./mvnw spring-boot:run
```

### Tests unitaires

* Lancez tous les tests:

```shell
./mvnw clean test -Pdev
```

* Lancez une seule classe de test:

```shell
./mvnw clean test -Dtest=<nomdelaclasse> -Pdev
```

* Lancez une seule méthode de test:

```shell
./mvnw clean test -Dtest=<nomdelaclasse>#<nomdelamethode> -Pdev
```

### Tests d'intégration

Prérequis: la stack lancée avec docker-compose.

Attention [geo.api.gouv.fr](https://api.gouv.fr/api/api-geo.html) est limité à 10 appels/s/IP!

Pour désactiver la vérification des communes, ajouter:

```shell
-Dapp.communes-url=
```

* Lancez tous les tests sans échec:

```shell
./mvnw clean integration-test -Dapp.communes-url=
```

avec échec mais sans l'analyse des vulnérabilités:

```shell
./mvnw clean verify -Dapp.communes-url= -Dskip.check=true
```

* Lancez une seule classe de test:

```shell
./mvnw clean integration-test -Dit.test=<nomdelaclasse>IT -Dapp.communes-url=
```

* Lancez une seule méthode de test:

```shell
./mvnw clean integration-test -Dit.test=<nomdelaclasse>IT#<nomdelamethode> -Dapp.communes-url=
```

### Tests manuels

Prérequis: la stack lancée avec docker-compose.

```shell
KC_REALM=rieau
KC_USERNAME=jean.martin
KC_PASSWORD=
KC_CLIENT=rieau
KC_URL="http://localhost:8080/auth"
KC_RESPONSE=$( \
   curl -k -v \
        -d "username=$KC_USERNAME" \
        -d "password=$KC_PASSWORD" \
        -d 'grant_type=password' \
        -d "client_id=$KC_CLIENT" \
        "$KC_URL/realms/$KC_REALM/protocol/openid-connect/token" \
    | jq .
)
KC_ACCESS_TOKEN=$(echo $KC_RESPONSE| jq -r .access_token)
KC_ID_TOKEN=$(echo $KC_RESPONSE| jq -r .id_token)
KC_REFRESH_TOKEN=$(echo $KC_RESPONSE| jq -r .refresh_token)
curl -k -H "Authorization: Bearer $KC_ACCESS_TOKEN" -v http://localhost:5000/dossiers
```

### Vérification des vulnérabilités

```shell
./mvnw clean check -Pdev -DskipTests
```

### Gestion des versions

Avec le plugin [maven-release](https://maven.apache.org/guides/mini/guide-releasing.html), par exemples:

Tag git et pom puis update de la dev version dans pom:

```shell
./mvnw --batch-mode -Dtag=v1.0.0 -Ddry.run=true -Dskip.check=true release:prepare \
    -DreleaseVersion=1.0.0 \
    -DdevelopmentVersion=1.1.0-SNAPSHOT
```

Ajout de `-DdryRun=true` pour tester seulement, puis `mvn release:clean`.

Update seulement de la dev version dans pom:

```shell
mvn --batch-mode release:update-versions -DdevelopmentVersion=1.1.0-SNAPSHOT
```

### Construction de l'image Docker

* Build:

```shell
./mvnw clean package -DskipTests -DskipITs
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t tristanrobert/rieau-api -f src/main/docker/Dockerfile .
```

* Run:

```shell
docker run -p 5000:5000 --name rieau-api -d -t tristanrobert/rieau-api
```

Il est possible de changer le port http du serveur à l'éxécution en ajoutant `-e SERVER_PORT=5000`. Par défaut, il est égal à `5000`.

Il est possible de changer à l'exécution les variables d'environnement (cf. [rieau-infra](https://github.com/MTES-MCT/rieau-infra)).

* Scan des vulnérabilités:

```shell
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
-v $HOME/.trivy/caches:/root/.cache/ knqyf263/trivy tristanrobert/rieau-api:latest
```

* Publier:

Tagger une version semver et pousser la sur le registry:

```shell
docker tag tristanrobert/rieau-api:[digestid] tristanrobert/rieau-api:[semver]
docker push tristanrobert/rieau-api:[semver]
```
