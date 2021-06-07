package io.github.oliviercailloux;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/hello")
public class GreetingResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GreetingResource.class);

	@Inject
	SantaClausService santa;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		LOGGER.info("Hello!");
		santa.createGift("ploum");
		return "Hello";
	}
}