package com.expensesEye.persist.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract Dao class that defines the standard and generic operations to be
 * performed on a model object(s)
 * 
 * @param <T>
 *            the model's data type
 * @param <E>
 *            the id's data type
 */
public abstract class AbstractDao<T, E extends Serializable> {
	protected static Logger logger = LoggerFactory.getLogger(AbstractDao.class);

	@PersistenceContext
	protected EntityManager entityManager;
	private Class<T> classType;

	public AbstractDao(Class<T> classType) {
		this.classType = classType;
	}

	/**
	 * This method creates/saves a new model
	 * 
	 * @param model
	 *            the model to be added/saved
	 * @return true if the given model is created successfully, otherwise it will
	 *         return false
	 */
	public boolean create(T model) {
		try {
			entityManager.persist(model);
		} catch (Exception ex) {
			logger.error("Unable to persist the following entity: " + model, ex);
			return false;
		}
		return true;
	}

	/**
	 * This method updates a model, given the updated model
	 * 
	 * @param model
	 *            the updated model
	 * @return true if the given model is updated successfully, otherwise it will
	 *         return false
	 */
	public boolean update(T model) {
		try {
			entityManager.merge(model);
		} catch (Exception ex) {
			logger.error("Unable to update the following entity: " + model, ex);
			return false;
		}
		return true;
	}

	/**
	 * This method removes a model, given the model to be removed
	 * 
	 * @param model
	 *            the model to be removed
	 * @return the removed model, if the given model is removed successfully,
	 *         otherwise it will return <b>null</b>
	 */
	public T remove(T model) {
		try {
			entityManager.remove(model);
		} catch (Exception ex) {
			logger.error("Unable to remove the following entity: " + model, ex);
			return null;
		}
		return model;
	}

	/**
	 * This method finds a list of saved models
	 * 
	 * @return list of models if the selection is successful, otherwise it will
	 *         return <b>null</b>
	 */
	public List<T> getAll() {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(classType);
			Root<T> from = cq.from(classType);
			CriteriaQuery<T> all = cq.select(from);
			TypedQuery<T> allQuery = entityManager.createQuery(all);
			return allQuery.getResultList();
		} catch (Exception ex) {
			logger.error("Unable to get all models", ex);
			return null;
		}
	}

	/**
	 * This method finds a model, given the model's id
	 * 
	 * @param id
	 *            the model's id
	 * @return the model mapped to the given id, or <b>null</b> if model doesn't
	 *         exist
	 */
	public T find(E id) {
		return entityManager.find(classType, id);
	}
}
