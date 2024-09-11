package com.ExpenceManager.expencemanager.repo;

import com.ExpenceManager.expencemanager.dto.FilterTransactionDto;
import com.ExpenceManager.expencemanager.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class TransactionRepoBean {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CategoryRepo categoryRepo;

    public List<Transaction> findByCriteria(FilterTransactionDto requiredTransaction) throws ParseException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
        Root<Transaction> root = criteriaQuery.from(Transaction.class);

        Predicate predicates = criteriaBuilder.conjunction();

        if (requiredTransaction.getFromDate() != null && !requiredTransaction.getFromDate().isEmpty()) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThanOrEqualTo(root.get("date"), new SimpleDateFormat("yyyy-MM-dd").parse(requiredTransaction.getFromDate())));
        }
        if (requiredTransaction.getToDate() != null && !requiredTransaction.getToDate().isEmpty()) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.lessThanOrEqualTo(root.get("date"), new SimpleDateFormat("yyyy-MM-dd").parse(requiredTransaction.getToDate())));
        }
        if (requiredTransaction.getAmountFrom() != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), requiredTransaction.getAmountFrom()));
        }
        if (requiredTransaction.getAmountTo() != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.lessThanOrEqualTo(root.get("amount"), requiredTransaction.getAmountTo()));
        }
        if (requiredTransaction.getNote() != null && !requiredTransaction.getNote().isEmpty()) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.get("note"), requiredTransaction.getNote()));
        }
        if (requiredTransaction.getType() != null && !requiredTransaction.getType().isEmpty()) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("type"), requiredTransaction.getType()));
        }
        if (requiredTransaction.getCategoryId() != 0L) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("category"), categoryRepo.findById(requiredTransaction.getCategoryId())));
        }

        criteriaQuery.where(predicates);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
