package pt.ist.dsi.movies;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class MovieRating extends Movie {
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