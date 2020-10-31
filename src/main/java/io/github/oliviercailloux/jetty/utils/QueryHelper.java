package io.github.oliviercailloux.jetty.utils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@RequestScoped
public class QueryHelper {
	@Inject
	private EntityManagerFactory emf;

	public <T> CriteriaQuery<T> selectAll(Class<T> type) {
		final CriteriaBuilder criteriaBuilder = emf.getCriteriaBuilder();
		final CriteriaQuery<T> query = criteriaBuilder.createQuery(type);
		final Root<T> from = query.from(type);
		query.select(from);
		return query;
	}

}
