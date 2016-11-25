package pt.ist.dsi.movies;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
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

import pt.ist.dsi.movies.api.ActorRepository;
import pt.ist.dsi.movies.api.MovieRepository;
import pt.ist.dsi.movies.domain.Actor;
import pt.ist.dsi.movies.domain.Movie;

@Component
public class ApplicationDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationDataInitializer.class);

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Value("${omdb.endpoint:http://www.omdbapi.com}")
    private String omdbEndpoint;

    @Value("${MOVIE_NAME:superman}")
    private String searchMovieName;

    @Autowired
    private RestTemplate restTemplate;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OmdbQuery implements Serializable {
        @JsonProperty("Search")
        private List<OmdbMovie> search = new ArrayList<>();

        @JsonProperty("Search")
        public List<OmdbMovie> getSearch() {
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
        public String getTitle() {
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
        actors.subList(0, new Random().nextInt(actors.size()));
        return actors;
    }

    private URI getUriFor(int page) {
        return UriComponentsBuilder.fromHttpUrl(omdbEndpoint).queryParam("type", "movie").queryParam("s", searchMovieName)
                .queryParam("page", page).build().toUri();
    }

    private void createActors() {
        Stream.of("David Martinho", "Gonçalo Sousa", "Sérgio Silva").forEach(actorName -> {
            actorRepository.save(new Actor(actorName));
        });
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
        omdbQuery.getSearch().stream().map(m -> {
            return new Movie(m.getTitle(), getRandomActors());
        }).forEach(movieRepository::save);
    }

}
