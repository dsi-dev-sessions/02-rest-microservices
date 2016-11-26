package pt.ist.dsi.movies.domain;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */

@Component
public class ActorService {
    
    private ActorRepository actorRepository;
    private CharacterRepository characterRepository;
    
    public ActorService(ActorRepository actorRepository, CharacterRepository characterRepository) {
        this.actorRepository = actorRepository;
        this.characterRepository = characterRepository;
    }
    
    public List<Actor> findAll() {
        return actorRepository.findAll();
    }
    
    public Actor create(String name) {
        return actorRepository.save(new Actor(name));
    }
    
    public Actor update(Actor entity, String name) {
        entity.setName(name);
        return actorRepository.save(entity);
    }
    
    @Transactional
    public void delete(Actor actor) {
        characterRepository.deleteInBatch(actor.getCharacters());
        actorRepository.delete(actor);
    }
}
