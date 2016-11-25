package pt.ist.dsi.movies.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.ist.dsi.movies.domain.Actor;

@RepositoryRestResource(collectionResourceRel = "actors", path = "actors")
public interface ActorRepository extends JpaRepository<Actor, Long> {

}