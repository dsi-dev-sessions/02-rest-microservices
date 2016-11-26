package pt.ist.dsi.movies.api;

import pt.ist.dsi.movies.domain.Actor;
import pt.ist.dsi.movies.domain.ActorService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Actor> all() {
        return actorService.findAll();
    }

//    @RequestMapping(value = "/{actor}", method = RequestMethod.GET)
//    public ResponseEntity<?> getActor(@PathVariable("actor") String actor) {
//
//        if (actor == null || actor.isEmpty()) {
//            return ResponseEntity.badRequest().body("Actor can't be null");
//        }
//        try {
//            Long id = Long.parseLong(actor);
//            Actor result = actorRepository.findOne(id);
//
//            if (result == null) {
//                return ResponseEntity.notFound().build();
//            }
//
//            return ResponseEntity.ok(result);
//        } catch (NumberFormatException nfe) {
//            return ResponseEntity.badRequest().body("Actor is not a valid id.");
//        }
//
//    }
    
    @GetMapping(value = "/{actor}", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE })
    public Actor get(@PathVariable Optional<Actor> actor) {
        return actor.orElseThrow(this::actorNotFound);
    }
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Actor create(@RequestBody IncomingActor incomingActor) {
        if (incomingActor == null || incomingActor.getName() == null || incomingActor.getName().isEmpty()) {
            throw new MovieAPIException(HttpStatus.BAD_REQUEST, "Can't create actor because name is null.");
        }
        return actorService.create(incomingActor.getName());
    }
    
    @PutMapping(value = "/{actor}", consumes = MediaType.APPLICATION_JSON_VALUE)
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