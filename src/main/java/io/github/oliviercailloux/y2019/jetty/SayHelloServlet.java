package io.github.oliviercailloux.y2019.jetty;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

@SuppressWarnings("serial")
@WebServlet("/sayHelloServlet")
public class SayHelloServlet extends HttpServlet {
	@Inject
	private Injected injected;

	/**
	 * Doesn’t work.
	 */
//	@Resource
//	private UserTransaction transaction;

	@Override
	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
//		resp.setContentType(MediaType.TEXT_PLAIN);
		resp.setLocale(Locale.ENGLISH);
		final ServletOutputStream out = resp.getOutputStream();
		out.println(String.format("Hello, world at %s.", Instant.now()));
		out.println(String.format("Injected: %s.", injected.toString()));

		InitialContext ic;
		try {
			ic = new InitialContext();
			Double myValue = (Double) ic.lookup("java:comp/env/wiggle");
			out.println(String.format("My value: %s.", myValue));
//			Integer mySpecialValueCont = (Integer) ic.lookup("java:comp/env/mySpecialValueCont");
//			out.println(String.format("Looked up cont: %s.", mySpecialValueCont));
			DataSource myDS = (DataSource) ic.lookup("java:comp/env/jdbc/myds");
			out.println(String.format("Looked up: %s.", myDS.toString()));
			UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
			out.println(String.format("Ut: %s.", ut));
//			out.println(String.format("Injected Ut: %s.", transaction));
		} catch (NamingException e) {
			throw new ServletException(e);
		}
	}
}
