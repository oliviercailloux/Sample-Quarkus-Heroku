package io.github.oliviercailloux.jetty;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.oliviercailloux.javaee_jpa_inject_servlets.utils.ServletHelper;

@SuppressWarnings("serial")
@WebServlet("/postItemServlet")
public class PostItemServlet extends HttpServlet {
	@PersistenceContext
	private EntityManager em;

	@Inject
	private ItemService itemS;

	@Inject
	private ServletHelper servletHelper;
}
