package pt.ist.dsi.movies.api;

import pt.ist.dsi.movies.domain.Actor;
import pt.ist.dsi.movies.domain.ActorService;
import pt.ist.dsi.movies.domain.View;
import pt.ist.dsi.movies.domain.View.ActorWithCharacters;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */

@RestController
@RequestMapping("/actors")
public class ActorsResource {
    
    static class IncomingActor {
        
        private String name;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    private ActorService actorService;
    
    @Autowired
    public ActorsResource(ActorService actorService) {
        this.actorService = actorService;
    }
    
    @GetMapping
    @JsonView(View.class)
    public List<Actor> all() {
        return actorService.findAll();
    }
    
    @JsonView(ActorWithCharacters.class)
    @GetMapping(value = "/{actor}")
    public Actor get(@PathVariable Optional<Actor> actor) {
        return actor.orElseThrow(this::actorNotFound);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(View.class)
    public Actor create(@RequestBody IncomingActor incomingActor) {
        if (StringUtils.isEmpty(incomingActor.getName())) {
            throw new MovieAPIException(HttpStatus.BAD_REQUEST, "Can't create actor because name is null.");
        }
        return actorService.create(incomingActor.getName());
    }
    
    @PutMapping(value = "/{actor}")
    @JsonView(View.class)
    public Actor update(@PathVariable Optional<Actor> actor, @RequestBody IncomingActor incomingActor) {
        return actorService.update(actor.orElseThrow(this::actorNotFound), incomingActor.getName());
    }
    
    @DeleteMapping("/{actor}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Optional<Actor> actor) {
        actorService.delete(actor.orElseThrow(this::actorNotFound));
    }
    
    private MovieAPIException actorNotFound() {
        return new MovieAPIException(HttpStatus.NOT_FOUND, "Actor not found");
    }
}