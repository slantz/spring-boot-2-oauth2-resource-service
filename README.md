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
#### POST /super-endpoint/samples

### Admin endpoints
The path `/admin/super-endpoint` is restricted only to users with ADMIN authority.

#### GET /admin/super-endpoint/samples
#### GET /admin/super-endpoint/samples/{username}
#### GET /admin/super-endpoint/{username}/authorities

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

1. sign in as application or other stuff with clientId and clientSecret
2. sign in as user via grant_type=password username and password
3. use sign in token to get user info from /users/me
4. use sign in token to get resource service endpoints received with grant_type=client_credentials to reach non-user specific endpoints
5. use sign in token to get resource service endpoints received with grant_type=password to reach user specific endpoints,
get username from OAuth2 token and filter resource service data from DBs bu username, not user id to match Spring Security UserDetails interface.

## Useful notes

- Spring interfaces and their implementations should live into same package for Spring to be able to autoconfigure those.
- http://stytex.de/blog/2016/02/01/spring-cloud-security-with-oauth2/
- https://spring.io/guides/gs/testing-web/ - testing
