package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ContentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContentRepository extends CrudRepository<ContentEntity, UUID> {
}
