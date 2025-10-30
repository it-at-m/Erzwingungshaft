package de.muenchen.eh.log.db.service;

import de.muenchen.eh.log.db.entity.Claim;
import de.muenchen.eh.log.db.entity.ClaimImport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ClaimService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<ClaimImport> claimsForProcessing() {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ClaimImport> query = cb.createQuery(ClaimImport.class);
        Root<ClaimImport> claimImportRoot = query.from(ClaimImport.class);

        Join<ClaimImport, Claim> claimImportClaimJoin = claimImportRoot.join("claim", JoinType.LEFT);

        query.where(cb.and(cb.equal(claimImportRoot.get("isDataImport"), true),
                cb.equal(claimImportRoot.get("isAntragImport"), true),
                cb.equal(claimImportRoot.get("isBescheidImport"), true),
                cb.isNull(claimImportClaimJoin)));

        return entityManager.createQuery(query).getResultList();
    }

}
