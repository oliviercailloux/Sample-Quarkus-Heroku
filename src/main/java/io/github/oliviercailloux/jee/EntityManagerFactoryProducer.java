package io.github.oliviercailloux.jee;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * From
 * http://in.relation.to/2019/01/23/testing-cdi-beans-and-persistence-layer-under-java-se/
 */
@ApplicationScoped
public class EntityManagerFactoryProducer {

	@Inject
	private BeanManager beanManager;

	@Produces
	@ApplicationScoped
	public EntityManagerFactory produceEntityManagerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put("javax.persistence.bean.manager", beanManager);
		return Persistence.createEntityManagerFactory("PU", props);
	}

	public void close(@Disposes EntityManagerFactory entityManagerFactory) {
		entityManagerFactory.close();
	}
}