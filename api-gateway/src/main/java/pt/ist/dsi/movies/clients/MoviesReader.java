package pt.ist.dsi.movies.clients;

import pt.ist.dsi.movies.Movie;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("movies-service")
public interface MoviesReader {
    
    @RequestMapping(method = RequestMethod.GET, path = "/movies")
    Resources<Movie> getMovies();
    
    @RequestMapping(method = RequestMethod.GET, path = "/movies/{movie}")
    Resource<Movie> getMovie(@PathVariable("movie") String movie);
}