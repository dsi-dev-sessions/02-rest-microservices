package pt.ist.dsi.movies;

import pt.ist.dsi.movies.domain.Actor;
import pt.ist.dsi.movies.domain.ActorRepository;
import pt.ist.dsi.movies.domain.Character;
import pt.ist.dsi.movies.domain.Movie;
import pt.ist.dsi.movies.domain.MovieRepository;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ApplicationDataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationDataInitializer.class);
    
    private final ActorRepository actorRepository;
    
    private final MovieRepository movieRepository;
    
    @Value("${omdb.endpoint:http://www.omdbapi.com}")
    private String omdbEndpoint;
    
    @Value("${MOVIE_NAME:superman}")
    private String searchMovieName;
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public ApplicationDataInitializer(ActorRepository actorRepository, MovieRepository movieRepository,
            RestTemplate restTemplate) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OmdbQuery implements Serializable {
        @JsonProperty("Search")
        private List<OmdbMovie> search = new ArrayList<>();
        
        @JsonProperty("Search")
        List<OmdbMovie> getSearch() {
            return search;
        }
        
        @JsonProperty("Search")
        public void setSearch(List<OmdbMovie> movies) {
            this.search = movies;
        }
    }
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OmdbMovie implements Serializable {
        @JsonProperty("Title")
        private String title;
        
        @JsonProperty("Title")
        String getTitle() {
            return title;
        }
        
        @JsonProperty("Title")
        public void setTitle(String title) {
            this.title = title;
        }
    }
    
    private List<Actor> getRandomActors() {
        List<Actor> actors = actorRepository.findAll();
        Collections.shuffle(actors);
        return actors.subList(0, Math.max(1, new Random().nextInt(actors.size())));
    }
    
    private URI getUriFor(int page) {
        return UriComponentsBuilder.fromHttpUrl(omdbEndpoint).queryParam("type", "movie").queryParam("s", searchMovieName)
                .queryParam("page", page).build().toUri();
    }
    
    private void createActors() {
        Stream.of("David Martinho", "Gonçalo Sousa", "Sérgio Silva")
                .forEach(actorName -> actorRepository.save(new Actor(actorName)));
    }
    
    @Override
    @Transactional
    public void run(String... arg0) throws Exception {
        createActors();
        logger.info("Start population of {}", searchMovieName);
        IntStream.range(1, 11).forEach(this::createMovies);
    }
    
    private void createMovies(int page) {
        URI omdbUri = getUriFor(page);
        logger.info("Loading movies from {}", omdbUri.toString());
        ResponseEntity<OmdbQuery> response = restTemplate.getForEntity(omdbUri, OmdbQuery.class);
        final OmdbQuery omdbQuery = response.getBody();
        omdbQuery.getSearch().stream().map(OmdbMovie::getTitle).map(Movie::new).forEach(movie -> {
            
            logger.debug("Movie {}", movie.toString());
            
            List<Actor> randomActors = getRandomActors();
            logger.debug("Actors {}", Arrays.toString(randomActors.toArray()));
            
            randomActors.forEach(actor -> {
                Character character = new Character("Himself", actor, movie);
                movie.getCharacters().add(character);
                movieRepository.save(movie);
            });
        });
    }
    
}
