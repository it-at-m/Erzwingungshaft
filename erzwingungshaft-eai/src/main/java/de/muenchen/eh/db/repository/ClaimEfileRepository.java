package de.muenchen.eh.db.repository;

import de.muenchen.eh.db.entity.ClaimEfile;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimEfileRepository extends CrudRepository<ClaimEfile, UUID> {
}
