package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimEfile;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimEfileRepository extends CrudRepository<ClaimEfile, UUID> {
}
