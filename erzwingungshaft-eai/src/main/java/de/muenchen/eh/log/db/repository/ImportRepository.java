package de.muenchen.eh.log.db.repository;

import de.muenchen.eh.log.db.entity.ImportEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImportRepository extends CrudRepository<ImportEntity, UUID> {

    List<ImportEntity> findByIsDataImportTrueAndIsAntragImportTrueAndIsBescheidImportTrue();


    List<ImportEntity> findByOutputDirectory(String outputDirectory);

}
