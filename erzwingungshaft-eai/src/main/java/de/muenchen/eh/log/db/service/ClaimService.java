package de.muenchen.eh.log.db.service;

import de.muenchen.eh.log.db.entity.ClaimEntity;
import de.muenchen.eh.log.db.entity.ClaimImport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClaimService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<ClaimImport> claimsForProcessing() {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<ClaimImport> claimImportRoot = query.from(ClaimImport.class);

        Join<ClaimImport, ClaimEntity> claimImportClaimJoin = claimImportRoot.join("claim", JoinType.LEFT);

        query.where(cb.equal(claimImportRoot.get("isDataImport"), true),
                    cb.equal(claimImportRoot.get("isAntragImport"), true),
                    cb.equal(claimImportRoot.get("isBescheidImport"), true),
                    cb.isNull(claimImportClaimJoin.get("ehUuid")));

       List<Object[]> objects = entityManager.createQuery(query).getResultList();

       List<ClaimImport> claims = Arrays.stream(objects.getFirst()).filter(obj -> obj instanceof ClaimImport).map(obj -> (ClaimImport)obj).toList();

        return  claims;
    }

}
