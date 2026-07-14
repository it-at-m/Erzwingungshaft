package de.muenchen.eh.infrastructure.db.repository;

import de.muenchen.eh.infrastructure.db.entity.ClaimData;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimDataRepository extends CrudRepository<ClaimData, UUID> {

    ClaimData findByClaimId(Integer claimId);
}


