package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.Claim;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends CrudRepository<Claim, UUID> {

    Claim findByClaimImportId(Integer claimImportId);

}
