package de.muenchen.eh.infrastructure.db.repository;

import de.muenchen.eh.infrastructure.db.entity.EfileIdentifier;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EfileIdentifierRepository extends CrudRepository<EfileIdentifier, Integer> {

    EfileIdentifier id(Integer id);

    List<EfileIdentifier> findByGeschaeftspartnerId(String gpdi);

    List<EfileIdentifier> findByGeschaeftspartnerIdAndKassenzeichenEfile(String geschaeftspartnerId, String kassenzeichenEfile);

}
