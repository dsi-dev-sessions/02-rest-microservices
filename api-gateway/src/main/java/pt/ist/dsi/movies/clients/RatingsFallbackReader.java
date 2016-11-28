package pt.ist.dsi.movies.clients;

import pt.ist.dsi.movies.MovieRating;

import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
@Component
class RatingsFallbackReader implements RatingsReader {
    
    @Override
    public Resource<MovieRating> getRating(String movie) {
        MovieRating movieRating = new MovieRating();
        movieRating.setAverageRating(2.5);
        return new Resource(movieRating);
    }
}