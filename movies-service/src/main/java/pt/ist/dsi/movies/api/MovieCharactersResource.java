package pt.ist.dsi.movies.api;

import pt.ist.dsi.movies.domain.ActorRepository;
import pt.ist.dsi.movies.domain.Character;
import pt.ist.dsi.movies.domain.CharacterRepository;
import pt.ist.dsi.movies.domain.Movie;
import pt.ist.dsi.movies.domain.View;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */

@RestController
@RequestMapping("/movies/{movie}/characters")
public class MovieCharactersResource {
    
    public static class IncomingCharacter {
        private String name;
        private long actor;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Long getActor() {
            return actor;
        }
        
        public void setActor(Long actor) {
            this.actor = actor;
        }
    }
    
    private CharacterRepository characterRepository;
    private ActorRepository actorRepository;
    
    @Autowired
    public MovieCharactersResource(CharacterRepository characterRepository, ActorRepository actorRepository) {
        this.characterRepository = characterRepository;
        this.actorRepository = actorRepository;
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(View.Character.class)
    public Character create(@PathVariable Movie movie, @RequestBody IncomingCharacter input) {
        validate(movie);
        return actorRepository.findById(input.getActor())
                .map(actor -> characterRepository.save(new Character(input.getName(), actor, movie))).orElseThrow(
                        () -> new MovieAPIException(HttpStatus.NOT_FOUND,
                                String.format("Actor %d does not exist.", input.getActor())));
    }
    
    @DeleteMapping("{character}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Movie movie, @PathVariable Character character) {
        validate(movie);
        if (!remove(movie, character)) {
            throw new MovieAPIException(HttpStatus.BAD_REQUEST,
                    String.format("Character %d does not exist in movie %d", character.getId(), movie.getId()));
        }
    }
    
    private void validate(@PathVariable Movie movie) {
        if (movie == null) {
            throw new MovieAPIException(HttpStatus.NOT_FOUND, "Movie does not exist");
        }
    }
    
    @Transactional
    private boolean remove(Movie movie, Character character) {
        if (character.getMovie().equals(movie)) {
            movie.getCharacters().remove(character);
            characterRepository.delete(character);
            return true;
        }
        return false;
    }
}