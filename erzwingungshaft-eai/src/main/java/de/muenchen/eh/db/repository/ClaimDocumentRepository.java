package de.muenchen.eh.db.repository;

import de.muenchen.eh.db.entity.ClaimDocument;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimDocumentRepository extends CrudRepository<ClaimDocument, Long> {

    List<ClaimDocument> findByClaimImportId(Integer claimImportId);
}
