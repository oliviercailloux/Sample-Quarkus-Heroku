package io.github.oliviercailloux.sample_quarkus_heroku;

import io.quarkus.runtime.StartupEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Startup {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

	@Inject
	EntityManager em;

	@Transactional
	public void loadAtStartup(@Observes StartupEvent evt) {
		LOGGER.info("Loading at startup due to {}.", evt);
		em.persist(new User("admin", "password", User.ADMIN_ROLE));
		em.persist(new User("étudiant", "password", User.STUDENT_ROLE));
		em.persist(new User("strange ©ê\u202F×æ characters", "password", User.STUDENT_ROLE));
		em.persist(new User("✓ à la mode", "a", User.STUDENT_ROLE));
		em.persist(new User("test", "123£", User.STUDENT_ROLE));
	}
}