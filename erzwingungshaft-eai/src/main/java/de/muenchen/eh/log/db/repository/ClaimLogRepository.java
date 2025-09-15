package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimLogEntity;
import de.muenchen.eh.log.db.entity.MessageType;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimLogRepository extends CrudRepository<ClaimLogEntity, UUID> {

    List<ClaimLogEntity> findByClaimIdAndMessageTyp(@NotEmpty Integer claimId, @NotEmpty MessageType messageTyp);

}
