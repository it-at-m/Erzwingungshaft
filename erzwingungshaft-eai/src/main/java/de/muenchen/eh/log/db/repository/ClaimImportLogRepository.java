package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimImportLog;
import de.muenchen.eh.log.db.entity.MessageType;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimImportLogRepository extends CrudRepository<ClaimImportLog, UUID> {

    List<ClaimImportLog> findByClaimImportIdAndMessageTyp(@NotEmpty Integer claimId, @NotEmpty MessageType messageTyp);

}
