package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimImportLog;
import de.muenchen.eh.log.db.entity.MessageType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimImportLogRepository extends CrudRepository<ClaimImportLog, UUID> {

    List<ClaimImportLog> findByClaimImportIdAndMessageType(@NotNull Integer claimId, @NotNull MessageType messageTyp);

}
