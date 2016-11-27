package pt.ist.dsi.movies.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    
    Optional<Actor> findById(Long id);
}