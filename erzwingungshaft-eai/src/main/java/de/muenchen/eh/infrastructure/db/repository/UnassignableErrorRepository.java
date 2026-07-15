package de.muenchen.eh.infrastructure.db.repository;

import de.muenchen.eh.infrastructure.db.entity.UnassignableError;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnassignableErrorRepository extends CrudRepository<UnassignableError, Integer> {
}
