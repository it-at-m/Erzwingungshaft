package de.muenchen.eh.infrastructure.db.repository;

import de.muenchen.eh.infrastructure.db.entity.ClaimXml;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimXmlRepository extends CrudRepository<ClaimXml, UUID> {

    List<ClaimXml> findByClaimId(@NotEmpty Integer claimId);

}


