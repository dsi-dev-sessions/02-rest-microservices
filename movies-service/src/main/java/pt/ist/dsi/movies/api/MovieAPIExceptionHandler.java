package pt.ist.dsi.movies.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */

@ControllerAdvice
public class MovieAPIExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(MovieAPIException.class)
    @JsonView(MovieAPIException.View.class)
    public ResponseEntity<?> handleAPIExceptionHandler(MovieAPIException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception, new HttpHeaders(), exception.getStatus(), request);
    }
}