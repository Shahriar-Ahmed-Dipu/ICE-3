#This file stays the same for every other uses in this course
#-------------
# BUILD STAGE
#-------------

FROM gradle:8-jdk-21-and-22-alpine AS builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build -x test

#---------------
# PACKAGE STAGE
#---------------

FROM openjdk:22-jdk

RUN mkdir /app

#--The * in *.jar means wildcard
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/api-gateway.jar

#DEFAULTS
ENV POSTGRES_USER=admin \
    POSTGRES_PASSWORD=password \
    DB_USER=admin \
    DB_PASSWORD=password \
    KEYCLOAK_ADMIN=admin \
    KEYCLOAK_ADMIN_PASSWORD=password

#Different port than the original one
EXPOSE 9000

ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]