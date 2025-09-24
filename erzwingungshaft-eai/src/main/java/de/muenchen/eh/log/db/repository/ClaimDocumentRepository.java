package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimDocumentRepository extends CrudRepository<ClaimDocument, Long> {

    List<ClaimDocument> findByClaimImportId(Integer claimImportId);
}
