package de.muenchen.eh.db.repository;

import de.muenchen.eh.db.entity.ClaimImport;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimImportRepository extends CrudRepository<ClaimImport, UUID> {

    List<ClaimImport> findByIsDataImportTrueAndIsAntragImportTrueAndIsBescheidImportTrueOrderByIdAsc();

    List<ClaimImport> findByOutputDirectoryAndIsAntragImportIsNullAndIsBescheidImportIsNull(String outputDirectory);

}
