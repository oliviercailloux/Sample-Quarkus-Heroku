package io.github.oliviercailloux.sample_quarkus_heroku;

import java.security.Principal;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/me")
@RequestScoped
public class UserResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

	@Context
	SecurityContext securityContext;

	@GET
	@PermitAll
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public String me() {
		/*
		 * Note that injecting the principal does not yield the same thing as obtaining
		 * it from the injected security context; e.g., when no current user, the
		 * injected principal gives an empty string for its name while the security
		 * context gives (per spec) a null principal.
		 */
		final Principal principal = securityContext.getUserPrincipal();
		LOGGER.info(
				"Security context: {}, class {}, security context principal: {}, class {}, security context principal name: {}.",
				securityContext, securityContext.getClass(), principal, principal == null ? null : principal.getClass(),
				principal == null ? null : principal.getName());
		if (principal == null) {
			return null;
		}
		return principal.getName();
	}

}
