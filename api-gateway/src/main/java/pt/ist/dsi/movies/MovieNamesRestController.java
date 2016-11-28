package pt.ist.dsi.movies;

import pt.ist.dsi.movies.clients.MoviesReader;
import pt.ist.dsi.movies.clients.RatingsReader;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
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
    
    @GetMapping
    public Resources<Movie> movies() {
        return moviesReader.getMovies();
    }
    
    @SuppressWarnings("unused")
    public Collection<String> movieNamesFallback() {
        return Stream.of("Mock Movie 1", "Mock Movie 2", "Mock Movie 3").collect(Collectors.toList());
    }
    
    @HystrixCommand(fallbackMethod = "movieNamesFallback")
    @RequestMapping(path = "names", method = RequestMethod.GET)
    public Collection<String> getMovieNames() {
        return moviesReader.getMovies().getContent().stream().map(Movie::getTitle).collect(Collectors.toList());
    }
    
    @RequestMapping(path = "{movieId}", method = RequestMethod.GET)
    public MovieRating getMovieWithRating(@PathVariable String movieId) {
        final Resource<Movie> movieResource = moviesReader.getMovie(movieId);
        Movie movie = movieResource.getContent();
        MovieRating movieRating = ratingsReader.getRating(movieId).getContent();
        movieRating.setTitle(movie.getTitle());
        movieRating.add(movieResource.getLinks());
        return movieRating;
    }
    
}