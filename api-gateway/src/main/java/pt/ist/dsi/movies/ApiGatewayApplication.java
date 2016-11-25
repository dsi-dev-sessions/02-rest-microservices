package pt.ist.dsi.movies;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

@RestController
@RequestMapping("/movies")
class MovieNamesRestController {

    private final MoviesReader moviesReader;
    private final RatingsReader ratingsReader;

    @Autowired
    public MovieNamesRestController(MoviesReader moviesReader, RatingsReader ratingsReader) {
        this.moviesReader = moviesReader;
        this.ratingsReader = ratingsReader;
    }

    @RequestMapping(path = "hello", method = RequestMethod.GET)
    public String hello() {
        return "Hello from API Gateway!";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Resources<Movie> movies() {
        return moviesReader.getMovies();
    }

    public Collection<String> failback() {
        return Stream.of("Mock Movie 1", "Mock Movie 2", "Mock Movie 3").collect(Collectors.toList());
    }

    @HystrixCommand(fallbackMethod = "failback")
    @RequestMapping(path = "names", method = RequestMethod.GET)
    public Collection<String> getMovieNames() {
        return moviesReader.getMovies().getContent().stream().map(Movie::getTitle).collect(Collectors.toList());
    }

    @RequestMapping(path = "{movieId}", method = RequestMethod.GET)
    public Movie getMovieWithRating(@PathVariable String movieId) {
        final Resource<Movie> movieResource = moviesReader.getMovie(movieId);
        Movie movie = movieResource.getContent();
        MovieRating rating = ratingsReader.getRating(movieId).getContent();
        movie.setRating(rating.getAverageRating().toString());
        return movie;
    }

}

@FeignClient("movie-service")
interface MoviesReader {

    @RequestMapping(method = RequestMethod.GET, path = "/movies")
    Resources<Movie> getMovies();

    @RequestMapping(method = RequestMethod.GET, path = "/movies/{movie}")
    Resource<Movie> getMovie(@PathVariable("movie") String movie);
}

@FeignClient("ratings-service")
interface RatingsReader {

    default Resource<MovieRating> fallbackRating() {
        MovieRating movieRating = new MovieRating();
        movieRating.setAverageRating(2.5);
        return new Resource(movieRating);
    }

    @HystrixCommand(fallbackMethod = "fallbackRating")
    @RequestMapping(method = RequestMethod.GET, path = "/ratings/{movie}")
    Resource<MovieRating> getRating(@PathVariable("movie") String movie);
}

class MovieRating {
    private Double averageRating;

    public MovieRating() {
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

}

class Movie {

    private String title;

    private String rating;

    public Movie() {
    }

    public Movie(String title) {
        super();
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}