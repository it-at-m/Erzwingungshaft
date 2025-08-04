package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.LogEntity;
import de.muenchen.eh.log.db.entity.MessageType;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogRepository extends CrudRepository<LogEntity, UUID> {

    List<LogEntity> findByEntryIdAndMessageTyp(@NotEmpty Integer entryId, @NotEmpty MessageType messageTyp);

}
