package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimXmlEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClaimXmlRepository extends CrudRepository<ClaimXmlEntity, UUID> {
}
