package de.muenchen.eh.db.service;

import de.muenchen.eh.db.entity.Xta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class XtaService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Xta> refreshMessageStatus() {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Xta> query = cb.createQuery(Xta.class);
        Root<Xta> root = query.from(Xta.class);

        Predicate statusPredicate = cb.equal(root.get("transportMessageStatus"), 0L);

        query.select(root).where(statusPredicate);

        return entityManager.createQuery(query)
                .getResultList();

    }

}
