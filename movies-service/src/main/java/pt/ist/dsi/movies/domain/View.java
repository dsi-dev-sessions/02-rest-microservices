package pt.ist.dsi.movies.domain;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public interface View {
    
    interface Actor extends View {
        
    }
    
    interface Movie extends View {
        
    }
    
    interface ActorWithCharacters extends Actor {
        
    }
    
    interface MovieWithCharacters extends Movie {
        
    }
    
    interface Character extends Actor, Movie {
        
    }
}