# spring-boot-2-oauth2-resource-servcice
This is the boilerplate Spring Boot repository introducing microservice architecture for Resource service, implementing OAuth2, using JWT tokens for authorization and running in Docker.

## How to run
### Prerequisites
This specific sample requires mongo db, it could be anything, even users set on a startup in the main class.

1. Install mongo
    1. Docker - start mongo using Kitematic
        1. Search for `mongo:latest` image on Docker Hub
        1. Hit the `CREATE` button and viola!
    2. Using docker command line
        1. `docker search mongo`
        2. `docker pull mongo`
        3. `docker run -d -p 27017:27017 --name mongo mongo`
    3. Download mongo using internet and install locally on the machine.

2. Secure the database
    1. `use auth`
    2. create desired db user `db.createUser({user: "test",pwd: "test",roles:[{role: "readWrite", db: "resource"}]})`

### Run with Java < 9

So looks like you are oldschool enterprise dude, well, that's fine, no one will judge you and moreover there's an option to run with Java 8 for instance.

#### `build.gradle`

1. change `sourceCompatibility=1.10` to `sourceCompatibility=1.8`
2. comment out
```groovy
bootRun {
    jvmArgs = ["--add-modules", "java.xml.bind"]
}
```
this is necessary only starting from Java 9 as it supports modules.

#### `.java-version`

1. change `1.10` to `1.8`

## Dockerize all the things

### Use docker-compose to build a service image and run with mongo

### Docker network create only once

```
docker network create spring_boot_2_oauth2_default_network
```

#### Start
```
docker-compose up -d
```

#### Stop
```
docker-compose down -v
```

### Each authorization service in it's own image _WITHOUT_ common network

```
./gradlew build docker
```

```
docker run -d -p 9999:9999 -p 8081:8081 -e "spring.data.mongodb.host=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <mongo_container_name>)" -e "spring.data.mongodb.username=test" -e "spring.data.mongodb.password=test" -e "rest.api.auth-service-path=http://$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' spring-boot-auth-service):9999" -e "security.oauth2.client.access-token-uri=http://$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' spring-boot-auth-service):9999/oauth/token" --name 'resource-service' your-docker-repo/spring-boot-resource-service
```

it's important to pay attention here to `spring.data.mongodb.host` environment variable, since mongo container is not a part of the docker network here, it's IP should be defined first if it's in docker.
The same rule applies to spring-boot-auth-service.

## API
This section introduces sample API that relies on the resource Mongo database and Spring boot authorization service to issue tokens both for users and the resource service itself with different scopes and authorities.
Two API sections described here: for regular user and for admininstrator one, separate endpoints introducing broader permissions.

### Endpoints
The path `/super-endpoint` is secured for any non-authenticated user and requires regular USER authority.

#### GET /super-endpoint/samples

##### Authorization token
curl -X POST -vu web:111 -H "Accept: application/json" "http://authorization-service:9999/oauth/token" -d "grant_type=password&username=guest&password=guest" | jq .

##### Request (AUTHORITY: USER, USERNAME: GUEST)
```
@GET https://resource-service:8080/resource-services/super-endpoint/samples
```

##### Response (AUTHORITY: USER, USERNAME: GUEST)

```
[
    {
        "id": "5dd7ce793ceeab156f747704",
        "title": "and",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-01-01T00:00:00.000Z",
        "expiredDate": "2019-02-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747705",
        "title": "fulfills",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fb",
            "code": "USD",
            "symbol": "$"
        },
        "date": "2019-02-01T00:00:00.000Z",
        "expiredDate": "2019-03-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747706",
        "title": "database",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fb",
            "code": "USD",
            "symbol": "$"
        },
        "date": "2019-03-01T00:00:00.000Z",
        "expiredDate": "2019-04-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747707",
        "title": "with",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-04-01T00:00:00.000Z",
        "expiredDate": "2019-05-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747708",
        "title": "data",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-05-01T00:00:00.000Z",
        "expiredDate": "2019-06-01T00:00:00.000Z",
        "username": "guest"
    }
]
```

#### POST /super-endpoint/samples

##### Authorization token
curl -X POST -vu web:111 -H "Accept: application/json" "http://authorization-service:9999/oauth/token" -d "grant_type=password&username=admin&password=admin" | jq .

##### Request (AUTHORITY: ADMIN, USERNAME: ADMIN)
```
@POST https://resource-service:8080/resource-services/super-endpoint/samples
```

```
[
  {
    "title": "new-sample-item",
    "currency": {
      "id": "5dd7ce793ceeab156f7476fc",
      "code": "EUR",
      "symbol": "€"
    },
    "date": "2019-11-01T00:00:00.000Z",
    "expiredDate": "2019-11-30T00:00:00.000Z"
  }
]
```
##### Response (AUTHORITY: ADMIN, USERNAME: ADMIN)
```
[
    {
        "id": "5dde96517d20a610e1ebb091",
        "title": "new-sample-item",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-11-01T00:00:00.000Z",
        "expiredDate": "2019-11-30T00:00:00.000Z",
        "username": "admin"
    }
]
```

### Admin endpoints
The path `/admin/super-endpoint` is restricted only to users with ADMIN authority.

#### GET /admin/super-endpoint/samples

##### Request (AUTHORITY: ADMIN, USERNAME: ADMIN)
```
@GET https://resource-service:8080/resource-services/admin/super-endpoint/samples
```

##### Response (AUTHORITY: ADMIN, USERNAME: ADMIN)
```
[
    {
        "id": "5dd7ce793ceeab156f7476fd",
        "title": "super",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fb",
            "code": "USD",
            "symbol": "$"
        },
        "date": "2019-01-01T00:00:00.000Z",
        "expiredDate": "2019-02-01T00:00:00.000Z",
        "username": "admin"
    },
    {
        "id": "5dd7ce793ceeab156f7476fe",
        "title": "title",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-01-01T00:00:00.000Z",
        "expiredDate": "2019-02-01T00:00:00.000Z",
        "username": "admin"
    },
    {
        "id": "5dd7ce793ceeab156f7476ff",
        "title": "that",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fb",
            "code": "USD",
            "symbol": "$"
        },
        "date": "2019-02-01T00:00:00.000Z",
        "expiredDate": "2019-03-01T00:00:00.000Z",
        "username": "admin"
    },
    {
        "id": "5dd7ce793ceeab156f747700",
        "title": "describes",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fb",
            "code": "USD",
            "symbol": "$"
        },
        "date": "2019-03-01T00:00:00.000Z",
        "expiredDate": "2019-04-01T00:00:00.000Z",
        "username": "admin"
    },
    {
        "id": "5dd7ce793ceeab156f747701",
        "title": "this",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-03-01T00:00:00.000Z",
        "expiredDate": "2019-04-01T00:00:00.000Z",
        "username": "admin"
    },
    {
        "id": "5dd7ce793ceeab156f747702",
        "title": "random",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-04-01T00:00:00.000Z",
        "expiredDate": "2019-05-01T00:00:00.000Z",
        "username": "admin"
    },
    {
        "id": "5dd7ce793ceeab156f747703",
        "title": "model",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-05-01T00:00:00.000Z",
        "expiredDate": "2019-06-01T00:00:00.000Z",
        "username": "admin"
    },
    {
        "id": "5dd7ce793ceeab156f747704",
        "title": "and",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-01-01T00:00:00.000Z",
        "expiredDate": "2019-02-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747705",
        "title": "fulfills",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fb",
            "code": "USD",
            "symbol": "$"
        },
        "date": "2019-02-01T00:00:00.000Z",
        "expiredDate": "2019-03-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747706",
        "title": "database",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fb",
            "code": "USD",
            "symbol": "$"
        },
        "date": "2019-03-01T00:00:00.000Z",
        "expiredDate": "2019-04-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747707",
        "title": "with",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-04-01T00:00:00.000Z",
        "expiredDate": "2019-05-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747708",
        "title": "data",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-05-01T00:00:00.000Z",
        "expiredDate": "2019-06-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dde96517d20a610e1ebb091",
        "title": "new-sample-item",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-11-01T00:00:00.000Z",
        "expiredDate": "2019-11-30T00:00:00.000Z",
        "username": "admin"
    }
]
```

#### GET /admin/super-endpoint/samples/{username}

##### Request (AUTHORITY: ADMIN, USERNAME: ADMIN)
```
@GET https://resource-service:8080/resource-services/admin/super-endpoint/samples/guest
```

##### Response (AUTHORITY: ADMIN, USERNAME: ADMIN)
```
[
    {
        "id": "5dd7ce793ceeab156f747704",
        "title": "and",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-01-01T00:00:00.000Z",
        "expiredDate": "2019-02-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747705",
        "title": "fulfills",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fb",
            "code": "USD",
            "symbol": "$"
        },
        "date": "2019-02-01T00:00:00.000Z",
        "expiredDate": "2019-03-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747706",
        "title": "database",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fb",
            "code": "USD",
            "symbol": "$"
        },
        "date": "2019-03-01T00:00:00.000Z",
        "expiredDate": "2019-04-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747707",
        "title": "with",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-04-01T00:00:00.000Z",
        "expiredDate": "2019-05-01T00:00:00.000Z",
        "username": "guest"
    },
    {
        "id": "5dd7ce793ceeab156f747708",
        "title": "data",
        "currency": {
            "id": "5dd7ce793ceeab156f7476fc",
            "code": "EUR",
            "symbol": "€"
        },
        "date": "2019-05-01T00:00:00.000Z",
        "expiredDate": "2019-06-01T00:00:00.000Z",
        "username": "guest"
    }
]
```

#### GET /admin/super-endpoint/{username}/authorities

##### Request (AUTHORITY: ADMIN, USERNAME: ADMIN)
```
@GET https://resource-service:8080/resource-services/admin/super-endpoint/guest/authorities
```

##### Response (AUTHORITY: ADMIN, USERNAME: ADMIN)
```
[
    {
        "authority": "USER"
    }
]
```

## Useful links

1. These are official docs to start
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide#authenticationmanager-bean
- https://github.com/spring-guides/tut-bookmarks
2. These are links to proceed
- http://sivatechlab.com/secure-rest-api-using-spring-security-oauth2-part-4/
- https://github.com/spring-projects/spring-security-oauth/issues/685
- http://websystique.com/spring-security/secure-spring-rest-api-using-oauth2/
- https://stackoverflow.com/questions/49348551/could-not-autowire-authentication-manager-in-spring-boot-2-0-0?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
- https://stackoverflow.com/questions/3021200/how-to-check-hasrole-in-java-code-with-spring-security?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
- https://docs.spring.io/spring-security/site/docs/current/reference/html/el-access.html
- https://piotrminkowski.wordpress.com/2017/12/01/part-2-microservices-security-with-oauth2/
- https://hellokoding.com/registration-and-login-example-with-spring-security-spring-boot-spring-data-jpa-hsql-jsp/
- http://www.baeldung.com/spring-security-authentication-with-a-database


## Description how it works

1. application signs in itself with clientId and clientSecret as a client with grant_type=client_credentials.
2. user signs in as user via grant_type=password username and password
3. use sign in token to get user info from /sample-endpoint
4. sign in as admin to get info from /admin/sample-endpoint
5. resource service can get authorization service's specific endpoints restricted by the scope SPRING_BOOT_RESOURCE_SERVICE via @PreAuthorize annotation.
6. users can access endpoints in resource service considering the authorities of users and endpoints restrictions via @PreAuthorize.

## Useful notes

- Spring interfaces and their implementations should live into same package for Spring to be able to autoconfigure those.
- http://stytex.de/blog/2016/02/01/spring-cloud-security-with-oauth2/
- https://spring.io/guides/gs/testing-web/ - testing
