package io.github.oliviercailloux.y2019.jetty;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("db")
public class DbResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(DbResource.class);

	@Inject
	EntityManager em;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String call() {
		LOGGER.info("EM: {}.", em);
		return em.toString();
	}
}
