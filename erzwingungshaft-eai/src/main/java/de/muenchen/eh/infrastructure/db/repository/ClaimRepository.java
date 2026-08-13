package de.muenchen.eh.infrastructure.db.repository;

import de.muenchen.eh.infrastructure.db.entity.Claim;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends CrudRepository<Claim, Integer> {

    Claim findByClaimImportId(Integer claimImportId);

}
