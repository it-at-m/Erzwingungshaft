package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimEfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimEfileRepository extends CrudRepository<ClaimEfile, UUID> {

    Optional<ClaimEfile> findByClaimId(Integer claimId);

}
