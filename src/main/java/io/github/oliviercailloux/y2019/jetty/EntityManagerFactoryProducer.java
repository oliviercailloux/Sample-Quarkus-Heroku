package io.github.oliviercailloux.y2019.jetty;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * From
 * http://in.relation.to/2019/01/23/testing-cdi-beans-and-persistence-layer-under-java-se/
 */
@ApplicationScoped
public class EntityManagerFactoryProducer {

	@Produces
	@ApplicationScoped
	public EntityManagerFactory produceEntityManagerFactory() {
		return Persistence.createEntityManagerFactory("PU", new HashMap<>());
	}

	public void close(@Disposes EntityManagerFactory entityManagerFactory) {
		entityManagerFactory.close();
	}
}