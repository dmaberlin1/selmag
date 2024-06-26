# Разработка проекта со Spring (2024)

## Run with  IT tests
```shell
mvn clean verify
```


## Keycloak

Запуск

**keycloak**в Docker:
```shell
docker run --name selmag-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v ./config/keycloak/import:/opt/keycloak/data/import quay.io/keycloak/keycloak:23.0.4 start-dev --import-realm
```


DB **catalogue-db** in docker:
```shell
docker run --name catalogue-db -p 5444:5432 -e POSTGRES_DB=catalogue -e POSTGRES_USER=catalogue -e POSTGRES_PASSWORD=catalogue postgres:16
```
#### `-p 5444:5432 - перенаправляет порт 5444 на хосте на порт 5432 внутри контейнера.`

MONGODB **feedback-db** in docker:
```shell
docker run --name feedback-db -p 27018:27017 mongo:7
```
#### `-p 5444:5432 - перенаправляет порт 27017 на хосте на порт 27017 внутри контейнера.`


DB **manager-db** in docker:
```shell
docker run --name manager-db -p 5445:5432 -e POSTGRES_DB=manager -e POSTGRES_USER=manager -e POSTGRES_PASSWORD=manager postgres:16
```
#### `-p 5445:5432 - перенаправляет порт 5445 на хосте на порт 5432 внутри контейнера.`



