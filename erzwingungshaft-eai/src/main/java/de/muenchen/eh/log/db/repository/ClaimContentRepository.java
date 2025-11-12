package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimContent;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimContentRepository extends CrudRepository<ClaimContent, UUID> {
}
