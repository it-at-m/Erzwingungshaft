package de.muenchen.eh.infrastructure.db.repository;

import de.muenchen.eh.infrastructure.db.entity.ClaimImport;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimImportRepository extends CrudRepository<ClaimImport, Integer> {

    List<ClaimImport> findByIsDataImportTrueAndIsAntragImportTrueAndIsBescheidImportTrueOrderByIdAsc();

    List<ClaimImport> findByOutputDirectoryAndIsAntragImportIsNullAndIsBescheidImportIsNull(String outputDirectory);

    List<ClaimImport> findByGeschaeftspartnerIdAndKassenzeichen(String geschaeftspartnerId, String kassenzeichen);

}
