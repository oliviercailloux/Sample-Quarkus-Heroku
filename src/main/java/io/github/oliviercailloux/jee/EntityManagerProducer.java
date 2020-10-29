package io.github.oliviercailloux.jee;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;

/**
 * From
 * http://in.relation.to/2019/01/23/testing-cdi-beans-and-persistence-layer-under-java-se/
 */
@ApplicationScoped
public class EntityManagerProducer {

	@Inject
	private EntityManagerFactory entityManagerFactory;

	@Produces
	@RequestScoped
	public EntityManager produceEntityManager() {
		return entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
	}

	public void close(@Disposes EntityManager entityManager) {
		entityManager.close();
	}
}