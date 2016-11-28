# DSI Dev Sessions - REST APIs and Microservices

# REST APIs Demo

It is in a separate branch called `movies-service`.
Just clone this project and
    ` git checkout -b movies-service origin/movies-service`

# Microservices Demo

## Pre-requirements
    1. Internet is required for this demo
    2. install docker

## Running the demo

Just run the following command in this folder
    `docker-compose up`

This will start the following services:

* `batman-service` and `star-service` (./movies-service)
    - Service with /movies endpoint that loads the database with movies from omdbapi based on a environment variable MOVIE_NAME (defaults to superman)
* `ratings-service` (./rating-service)
    - POST to /ratings with ```{movie : 1, rating : 2}```
        + postman collection here or ```curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSJ9.FxP_NNi2BrJe0jObxXb4_1YUSikvxOAZSdXUZWDG1vU" -d '{"rating": 2, "movie": 1}' "http://localhost:8000/ratings-service/ratings"```
        + you can generate other JWT (Authorization Bearer) for different users at https://jwt.io using secret 123
    - GET of /ratings/{movieId} to get average rating
* `service-discovery`
    - Launches a [consul](https://www.consul.io/) instance for service registry and discovery
        + Exposed to host at http://localhost:8500
* `api-gateway`
    - Exposed to host at http://localhost:8000
    - Available endpoints:
        + http://localhost:8000/movies
            * proxies to movies-service which round-robins to batman-service or star-service.
        + http://localhost:8000/movies/names
            * returns an array with only the movie names which round-robins to batman-service or star-service.
        + http://localhost:8000/movies/1
            * agregates info from movie-service and rating-service
    - All the services above are available at http://localhost:8000/{service-name}
        + example ```http://localhost:8000/movies-service/movies```
* `zipkin-server`
    - Show request tracing from api-gateway to all services
    - Exposed to host at http://localhost:8502/
* `hystrix-dashboard`
    - Show api-gateway circuits and their statuses
    - Exposed to host at http://localhost:8501