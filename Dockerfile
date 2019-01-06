FROM openjdk:8-jdk

COPY . .

# Required to run wait-for-db.sh
RUN apt-get update
RUN apt-get install -y netcat

RUN ./gradlew clean build