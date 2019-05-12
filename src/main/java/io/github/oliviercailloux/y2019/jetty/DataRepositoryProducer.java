package io.github.oliviercailloux.y2019.jetty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * This bean is used to hold the entity manager factory and to produce the
 * conversation scoped entity manager for injection
 */
@ApplicationScoped
public class DataRepositoryProducer {

	private static EntityManagerFactory factory;

	@Produces
	public EntityManagerFactory getEntityManagerFactory() {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory("pu");
		}
		return factory;
	}

	@Produces
	@DataRepository
	@ConversationScoped
	public EntityManager produceEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

}
