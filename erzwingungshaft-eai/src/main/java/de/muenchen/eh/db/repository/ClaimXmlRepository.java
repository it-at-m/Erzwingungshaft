package de.muenchen.eh.db.repository;

import de.muenchen.eh.db.entity.ClaimXml;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimXmlRepository extends CrudRepository<ClaimXml, UUID> {
}
