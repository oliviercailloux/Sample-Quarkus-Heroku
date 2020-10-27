package io.github.oliviercailloux.javaee_jpa_inject_servlets.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
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

	public String getRedirectURL() {
		checkNotNull(context);
		final Map<String, ? extends ServletRegistration> servletRegistrations = context.getServletRegistrations();
		LOGGER.info("Reg: {}.", servletRegistrations);
//		final ServletRegistration servletRegistration = context
//				.getServletRegistration(ItemServlet.class.getCanonicalName());
		final ServletRegistration servletRegistration = servletRegistrations.values().iterator().next();
		checkNotNull(servletRegistration);
		Collection<String> mappings = servletRegistration.getMappings();
		assert (mappings.size() == 1);
		final String urlMapping = mappings.iterator().next();
		assert (urlMapping.charAt(0) == '/');
		return urlMapping.substring(1);
	}

}
