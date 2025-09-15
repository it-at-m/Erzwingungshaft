package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.BaseEntity;
import de.muenchen.eh.log.db.entity.ClaimContentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClaimContentRepository extends CrudRepository<ClaimContentEntity, UUID> {
}
