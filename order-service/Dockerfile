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
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/order-service.jar

#DEFAULTS
ENV POSTGRES_USER=admin \
    POSTGRES_PASSWORD=password \
    POSTGRES_DB=order-services

#Different port than the original one
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/order-service.jar"]