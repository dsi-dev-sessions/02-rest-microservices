# DSI Dev Sessions - REST API

# Domain Model
    1. A movie has a title and zero or many characters
    2. An actor has a name and zero or many characters
    3. A character has a name, a movie and an actor

# Application Architecture
This application is written in Java using Spring Boot.
It uses Spring Data along with JPA to persistent entities in an In Memory Database provided by H2.

It implements several endpoints available at ```src/main/java/pt/ist/dsi/movies/api```

# Running

Choose your poison:

1. Install java8 and maven and execute ```mvn clean spring-boot:run```
2. run it inside Intellij or Eclipse
3. docker build -t movies-service . && docker run movies-service

# Invoking the API

Import the postman collection available inside.