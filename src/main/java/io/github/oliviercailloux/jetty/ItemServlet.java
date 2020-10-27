package io.github.oliviercailloux.jetty;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.javaee_jpa_inject_servlets.utils.QueryHelper;
import io.github.oliviercailloux.javaee_jpa_inject_servlets.utils.ServletHelper;

@SuppressWarnings("serial")
@RequestScoped
public class ItemServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemServlet.class);

	@Inject
	EntityManager em;

	@Inject
	private ServletHelper servletHelper;

	@Inject
	private QueryHelper helper;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.TEXT_PLAIN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setLocale(Locale.ENGLISH);

		assert (!em.isJoinedToTransaction());
		final List<Item> allItems = getAll();
		assert (!em.isJoinedToTransaction());
		for (Item item : allItems) {
			assert (em.contains(item));
		}

		for (Item item : allItems) {
			response.getWriter().println(item.getName());
		}
		response.getWriter().println("End.");
	}

	@Transactional
	public List<Item> getAll() {
		return em.createQuery(helper.selectAll(Item.class)).getResultList();
	}

	@Override
	@SuppressWarnings("resource")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final Item item = new Item();
		/** Ideally we’d use the client zone here. */
		final ZonedDateTime zonedTimestamp = ZonedDateTime.now(ZoneId.systemDefault());
		item.setName("MyItem dated " + zonedTimestamp);

		final UserTransaction ut = MyJettyServer.getUserTransactionFromJndi();
		LOGGER.info("EM joined: {}.", em.isJoinedToTransaction());
		try {
			ut.begin();
		} catch (NotSupportedException | SystemException e) {
			throw new IllegalStateException(e);
		}
		LOGGER.info("Ut began.");
		LOGGER.info("EM joined: {}.", em.isJoinedToTransaction());
		em.joinTransaction();
		LOGGER.info("EM joined.");
		LOGGER.info("EM joined: {}.", em.isJoinedToTransaction());
		em.persist(item);
		try {
			ut.commit();
		} catch (SecurityException | IllegalStateException | RollbackException | HeuristicMixedException
				| HeuristicRollbackException | SystemException e) {
			throw new IllegalStateException(e);
		}
		response.sendRedirect(response.encodeRedirectURL(servletHelper.getRedirectURL()));
	}
}
