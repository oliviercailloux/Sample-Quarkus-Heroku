package io.github.oliviercailloux.jetty.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MoreCollectors;

import io.github.oliviercailloux.jetty.ItemServlet;

@RequestScoped
public class ServletHelper {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ServletHelper.class);

	@Inject
	private ServletContext context;

	public ServletOutputStream configureAndGetOutputStream(HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
		resp.setContentType(MediaType.TEXT_PLAIN);
		resp.setLocale(Locale.ENGLISH);
		return resp.getOutputStream();
	}

	public URI getRedirectURI() {
		checkNotNull(context);
		final Map<String, ? extends ServletRegistration> servletRegistrations = context.getServletRegistrations();
		LOGGER.info("Regs: {}.", servletRegistrations);
		final String myServletEntry = servletRegistrations.keySet().stream()
				.filter(s -> s.startsWith(ItemServlet.class.getCanonicalName())).collect(MoreCollectors.onlyElement());
		final ServletRegistration servletRegistration = context.getServletRegistration(myServletEntry);
		checkNotNull(servletRegistration);
		Collection<String> mappings = servletRegistration.getMappings();
		assert (mappings.size() == 1);
		final String urlMapping = mappings.iterator().next();
		assert (urlMapping.charAt(0) == '/');
		final String relative = urlMapping.substring(1);
		LOGGER.debug("Redirecting to {}.", relative);
		return URI.create(relative);
	}

}
