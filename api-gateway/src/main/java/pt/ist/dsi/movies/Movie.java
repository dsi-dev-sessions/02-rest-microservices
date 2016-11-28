package pt.ist.dsi.movies;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
@JsonInclude(Include.NON_EMPTY)
public class Movie extends ResourceSupport {
    
    private String title;
    
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
    
}
