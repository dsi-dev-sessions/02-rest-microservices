package pt.ist.dsi.movies.clients;

import pt.ist.dsi.movies.MovieRating;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
@FeignClient(value = "ratings-service", fallback = RatingsFallbackReader.class)
public interface RatingsReader {
    
    @RequestMapping(method = RequestMethod.GET, path = "/ratings/{movie}")
    Resource<MovieRating> getRating(@PathVariable("movie") String movie);
}
