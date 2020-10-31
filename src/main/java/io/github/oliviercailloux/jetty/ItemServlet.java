package io.github.oliviercailloux.jetty;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.jetty.utils.ServletHelper;

@SuppressWarnings("serial")
@RequestScoped
public class ItemServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemServlet.class);

	@Inject
	private ServletHelper servletHelper;

	@Inject
	private ItemService itemS;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.TEXT_PLAIN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setLocale(Locale.ENGLISH);

		final List<Item> allItems = itemS.getAll();

		for (Item item : allItems) {
			response.getWriter().println(item.getName());
		}
		response.getWriter().println("End.");
	}

	@Override
	@SuppressWarnings("resource")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final Item item = new Item();
		/** Ideally we’d use the client zone here. */
		final ZonedDateTime zonedTimestamp = ZonedDateTime.now(ZoneId.systemDefault());
		item.setName("MyItem dated " + zonedTimestamp);

		itemS.persist(item);

		response.sendRedirect(response.encodeRedirectURL(servletHelper.getRedirectURI().toString()));
	}
}
