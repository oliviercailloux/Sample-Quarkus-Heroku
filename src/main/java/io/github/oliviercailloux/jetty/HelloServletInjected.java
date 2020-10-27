package io.github.oliviercailloux.jetty;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@RequestScoped
public class HelloServletInjected extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(HelloServletInjected.class);

	@Inject
	BeanManager manager;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.TEXT_PLAIN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setLocale(Locale.ENGLISH);
		response.getWriter().println("Hello from " + manager.toString());

		logUT();
	}

	private void logUT() {
		final UserTransaction ut = MyJettyServer.getUserTransactionFromJndi();
		LOGGER.info("Looked up ut: {}.", ut);
	}
}
