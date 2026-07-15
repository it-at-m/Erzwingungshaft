package de.muenchen.eh.infrastructure.db.repository;

import de.muenchen.eh.infrastructure.db.entity.Identifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierRepository extends CrudRepository<Identifier, Integer> {
}
