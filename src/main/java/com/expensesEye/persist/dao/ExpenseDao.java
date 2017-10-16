package com.expensesEye.persist.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.expensesEye.model.request.ExpenseSearchRequest;
import com.expensesEye.persist.entity.Expense;

/**
 * A class that extends {@link AbstractDao}, and it defines the persistence
 * operations to be performed on {@link Expense}
 */
@Repository
public class ExpenseDao extends AbstractDao<Expense, Long> {

	public ExpenseDao() {
		super(Expense.class);
	}

	/**
	 * This method searches through the expenses given a
	 * {@link ExpenseSearchRequest} having all the parameters needed to perform the
	 * search
	 * 
	 * @param searchRequest
	 *            a {@link ExpenseSearchRequest} object
	 * @return list of expenses if the selection is successful, otherwise it will
	 *         return <b>null</b>
	 */
	public List<Expense> searchExpenses(ExpenseSearchRequest searchRequest) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Expense> cq = cb.createQuery(Expense.class);
			Root<Expense> from = cq.from(Expense.class);
			cq.select(from);

			// List of predicates
			List<Predicate> predicates = new ArrayList<>();
			if (searchRequest.getMinAmount() != null) {
				predicates.add(cb.greaterThanOrEqualTo(from.get("amount"), searchRequest.getMinAmount()));
			}
			if (searchRequest.getMaxAmount() != null) {
				predicates.add(cb.lessThanOrEqualTo(from.get("amount"), searchRequest.getMaxAmount()));
			}
			if (searchRequest.getStartTime() != null) {
				predicates.add(cb.greaterThanOrEqualTo(from.get("time"), searchRequest.getStartTime()));
			}
			if (searchRequest.getEndTime() != null) {
				predicates.add(cb.lessThanOrEqualTo(from.get("time"), searchRequest.getEndTime()));
			}
			if (searchRequest.getUserId() != null) {
				predicates.add(cb.equal(from.get("user"), searchRequest.getUserId()));
			}

			// If there is no predicates then get all expenses
			if (!predicates.isEmpty()) {
				cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
			}
			TypedQuery<Expense> query = entityManager.createQuery(cq);
			return query.getResultList();
		} catch (Exception ex) {
			logger.error("Unable Search Expense given the following request: " + searchRequest, ex);
			return null;
		}
	}

}
