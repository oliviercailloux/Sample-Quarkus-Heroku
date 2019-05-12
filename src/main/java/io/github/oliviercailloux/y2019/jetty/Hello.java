package io.github.oliviercailloux.y2019.jetty;

import java.time.Instant;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("hello")
public class Hello {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Hello.class.getCanonicalName());

	@Inject
	private ServletContext context;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayHello() {
//		if (context == null) {
//			LOGGER.warning("Context is null.");
//		} else {
//			LOGGER.info(String.format("Running on version: %d.%d.", context.getEffectiveMajorVersion(),
//					context.getEffectiveMinorVersion()));
//			LOGGER.info(
//					String.format("Supported version: %d.%d.", context.getMajorVersion(), context.getMinorVersion()));
//		}
		return justSayHello() + " It is " + Instant.now().toString() + ".";
	}

	String justSayHello() {
		return "Hello, world.";
	}
}
