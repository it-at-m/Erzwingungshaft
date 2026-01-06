package de.muenchen.eh.db.repository;

import de.muenchen.eh.db.entity.ClaimLog;
import de.muenchen.eh.db.entity.MessageType;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimLogRepository extends CrudRepository<ClaimLog, UUID> {

    List<ClaimLog> findByClaimIdAndMessageTyp(@NotEmpty Integer claimId, @NotEmpty MessageType messageTyp);

    List<ClaimLog> findByMessageTyp(@NotEmpty MessageType messageType);

}
