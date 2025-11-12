package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimXml;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimXmlRepository extends CrudRepository<ClaimXml, UUID> {
}
