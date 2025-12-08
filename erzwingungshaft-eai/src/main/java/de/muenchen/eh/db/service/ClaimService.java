package de.muenchen.eh.db.service;

import de.muenchen.eh.db.entity.Claim;
import de.muenchen.eh.db.entity.ClaimEfile;
import de.muenchen.eh.db.entity.ClaimImport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ClaimService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ClaimImport> claimsForProcessing() {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ClaimImport> query = cb.createQuery(ClaimImport.class);
        Root<ClaimImport> claimImportRoot = query.from(ClaimImport.class);

        Join<ClaimImport, Claim> claimImportClaimJoin = claimImportRoot.join("claim", JoinType.LEFT);

        query.where(cb.and(cb.equal(claimImportRoot.get("isDataImport"), true),
                cb.equal(claimImportRoot.get("isAntragImport"), true),
                cb.equal(claimImportRoot.get("isBescheidImport"), true),
                cb.isNull(claimImportClaimJoin)));
        List<ClaimImport> claimImports = entityManager.createQuery(query).getResultList();
        log.info("Claims found for processing : {}", claimImports.size());
        return claimImports;
    }

    public List<Claim> claimEfilesWithCorrespondingGId(String geschaeftspartnerId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Claim> query = cb.createQuery(Claim.class);
        Root<ClaimImport> claimImportRoot = query.from(ClaimImport.class);
        Join<ClaimImport, Claim> claimImportClaimJoin = claimImportRoot.join("claim", JoinType.INNER);
        Join<Claim, ClaimEfile> claimClaimFileJoin = claimImportClaimJoin.join("claimEfile", JoinType.INNER);
        query.select(claimImportClaimJoin);
        query.where(cb.equal(claimImportRoot.get("geschaeftspartnerId"), geschaeftspartnerId));
        return entityManager.createQuery(query).getResultList();

    }

}
