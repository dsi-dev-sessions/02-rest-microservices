package pt.ist.dsi.movies.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Actor {
    
    public interface View {
        public interface WithCharacters extends View {
            
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(unique = true)
    private String name;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "actor", orphanRemoval = true)
    private Set<Character> characters;
    
    protected Actor() {
    }
    
    public Actor(String name) {
        this.name = name;
        this.characters = new HashSet<>();
    }
    
    @JsonView({ View.class })
    public Long getId() {
        return id;
    }
    
    @JsonView({ View.class })
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonView({ View.WithCharacters.class })
    public Set<Character> getCharacters() {
        return characters;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Actor)) {
            return false;
        }
        Actor other = (Actor) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Actor [id=" + id + ", name=" + name + "]";
    }
}