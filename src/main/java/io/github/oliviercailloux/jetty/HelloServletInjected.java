package io.github.oliviercailloux.jetty;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@RequestScoped
public class HelloServletInjected extends HttpServlet {
	@Inject
	BeanManager manager;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		response.getWriter().println("Hello from " + manager);
	}
}
