package pt.ist.dsi.movies.api;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class MovieAPIException extends RuntimeException {
    
    public interface View {
        
    }
    
    private HttpStatus status;
    
    public MovieAPIException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
    
    @JsonView(View.class)
    public HttpStatus getStatus() {
        return status;
    }
    
    @Override
    @JsonView(View.class)
    public String getMessage() {
        return super.getMessage();
    }
}
