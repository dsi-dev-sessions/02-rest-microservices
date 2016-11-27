package pt.ist.dsi.movies.api;

import pt.ist.dsi.movies.domain.Movie;
import pt.ist.dsi.movies.domain.MovieRepository;
import pt.ist.dsi.movies.domain.View;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */

@RestController
@RequestMapping("/movies")
public class MoviesResource {
    
    private Supplier<MovieAPIException> movieNotFound = () -> new MovieAPIException(HttpStatus.NOT_FOUND, "Movie not found");
    
    private final MovieRepository movieRepository;
    
    @Autowired
    public MoviesResource(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
    
    //@RequestMapping(method = RequestMethod.GET)
    @GetMapping
    @JsonView(View.Movie.class)
    public List<Movie> all() {
        return movieRepository.findAll();
    }
    
    @GetMapping("/{movie}")
    @JsonView(View.MovieWithCharacters.class)
    public Movie get(@PathVariable Optional<Movie> movie) {
        return movie.orElseThrow(movieNotFound);
    }
    
    @PutMapping("/{movie}")
    @JsonView(View.MovieWithCharacters.class)
    public Movie update(@PathVariable Optional<Movie> movie, @RequestBody IncomingMovie incomingMovie) {
        return movie.map(mov -> {
            validateName(incomingMovie);
            mov.setTitle(incomingMovie.getTitle());
            return movieRepository.save(mov);
        }).orElseThrow(movieNotFound);
    }
    
    @PostMapping
    @JsonView(View.MovieWithCharacters.class)
    public Movie create(@RequestBody IncomingMovie incomingMovie) {
        validateName(incomingMovie);
        Movie movie = new Movie(incomingMovie.getTitle());
        return movieRepository.save(movie);
    }
    
    static class IncomingMovie {
        private String title;
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
    }
    
    private void validateName(@RequestBody IncomingMovie incomingMovie) {
        if (StringUtils.isEmpty(incomingMovie.getTitle())) {
            throw new MovieAPIException(HttpStatus.BAD_REQUEST, "Title can't be null.");
        }
    }
}
