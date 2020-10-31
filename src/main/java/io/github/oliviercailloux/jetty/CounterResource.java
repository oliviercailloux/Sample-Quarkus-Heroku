package io.github.oliviercailloux.jetty;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Simplification of
 * https://github.com/eclipse-ee4j/jersey/blob/master/examples/helloworld-cdi2-se/src/main/java/org/glassfish/jersey/examples/helloworld/cdi2se/CounterResource.java
 */
@Path("counter")
@RequestScoped
public class CounterResource {
	@Inject
	private ApplicationScopedCounter applicationScoped;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public int getAppCounter() {
		return applicationScoped.getNumber();
	}
}
