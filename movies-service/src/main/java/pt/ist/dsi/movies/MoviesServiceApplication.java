package pt.ist.dsi.movies;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MoviesServiceApplication {
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate client = new RestTemplate();
        client.setInterceptors(Arrays.asList((req, body, exec) -> {
            req.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            return exec.execute(req, body);
        }));
        return client;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(MoviesServiceApplication.class, args);
    }
}