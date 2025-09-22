package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ClaimImport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimImportRepository extends CrudRepository<ClaimImport, UUID> {

    List<ClaimImport> findByIsDataImportTrueAndIsAntragImportTrueAndIsBescheidImportTrue();
    List<ClaimImport> findByOutputDirectoryAndIsAntragImportIsNullAndIsBescheidImportIsNull(String outputDirectory);

}
