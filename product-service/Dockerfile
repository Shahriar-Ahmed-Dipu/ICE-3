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
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/product-service.jar

#DEFAULTS
ENV MONGO_DB_USERNAME=admin \
    MONGO_DB_PWD=password

#Different port than the original one
EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/product-service.jar"]