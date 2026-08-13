package de.muenchen.eh.infrastructure.db.repository;

import de.muenchen.eh.infrastructure.db.entity.ClaimImportLog;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimImportLogRepository extends CrudRepository<ClaimImportLog, Integer> {

    List<ClaimImportLog> findByClaimImportIdAndMessageType(@NotNull Integer claimId, @NotNull MessageType messageTyp);

    List<ClaimImportLog> findByMessage(@NotNull String message);

}
