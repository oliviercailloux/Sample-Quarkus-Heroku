package io.github.oliviercailloux.jetty;

import static com.google.common.base.Verify.verify;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.cdi.CdiServletContainerInitializer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.jboss.weld.environment.servlet.EnhancedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.VerifyException;

public class MyJettyServer {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MyJettyServer.class);

	public static UserTransaction getUserTransactionFromJndi() {
		final UserTransaction ut;
		final InitialContext ic;
		try {
			ic = new InitialContext();
			ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
		} catch (NamingException e) {
			throw new IllegalStateException(e);
		}
		return ut;
	}

	public static void main(String[] args) throws Exception {
		final String envPort = System.getenv("PORT");
		final int port = envPort != null ? Integer.parseInt(envPort) : 8080;

		final MyJettyServer jetty = new MyJettyServer(port);
		jetty.setConnectors();
		jetty.setHandlers();
		jetty.registerUserTransaction();

		jetty.start();

		try {
			jetty.verifyHello();
		} catch (VerifyException e) {
			jetty.stop();
			throw e;
		}

		jetty.join();
	}

	private final int port;
	private final Server server;

	private MyJettyServer(int port) {
		this.port = port;
		server = new Server();
	}

	public void setConnectors() {
		final HttpConfiguration config = new HttpConfiguration();
		/**
		 * Add support for X-Forwarded headers: thanks to
		 * https://stackoverflow.com/a/28520946. Required because of
		 * https://devcenter.heroku.com/articles/http-routing.
		 */
		config.addCustomizer(new ForwardedRequestCustomizer());
		@SuppressWarnings("resource")
		final ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(config));
		/** According to the SO post (link here above), the port must be set here. */
		connector.setPort(port);
		server.setConnectors(new Connector[] { connector });
	}

	public void setHandlers() {
		/**
		 * https://www.eclipse.org/jetty/documentation/current/embedding-jetty.html#_using_handlers
		 */
		final HandlerList handlers = new HandlerList();

		final ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "hello.txt" });
		resourceHandler.setResourceBase("src/main/webapp");

		final ServletContextHandler servletHandler = new ServletContextHandler();
		servletHandler.setContextPath("/api");
		servletHandler.addServlet(HelloServletInjected.class, "/servlet");

		handlers.addHandler(resourceHandler);
		handlers.addHandler(servletHandler);
		server.setHandler(handlers);
		LOGGER.debug("Set handler: {}.", server.getHandler());

		/**
		 * Using the approach recommended here:
		 * https://github.com/eclipse/jetty.project/issues/5326#issuecomment-699506325.
		 */
		servletHandler
				.addBean(new ServletContextHandler.Initializer(servletHandler, new CdiServletContainerInitializer()));
		servletHandler.addBean(new ServletContextHandler.Initializer(servletHandler, new EnhancedListener()));
		LOGGER.info("Initialized servlet handler: {}.", servletHandler);
	}

	public void registerUserTransaction() throws NamingException {
//		final com.atomikos.icatch.jta.J2eeUserTransaction userTransaction = new com.atomikos.icatch.jta.J2eeUserTransaction();
		final com.atomikos.icatch.jta.UserTransactionManager userTransactionManager = new com.atomikos.icatch.jta.UserTransactionManager();
		try {
			userTransactionManager.init();
		} catch (SystemException e) {
			throw new IllegalStateException(e);
		}
		@SuppressWarnings("unused")
		final org.eclipse.jetty.plus.jndi.Transaction transactionRegistration = new org.eclipse.jetty.plus.jndi.Transaction(
				userTransactionManager);
		org.eclipse.jetty.plus.jndi.Transaction.bindToENC();

	}

	public void start() throws Exception {
		server.start();
	}

	public void verifyHello() {
		final Client client = ClientBuilder.newClient();
		final UriBuilder uri = UriBuilder.fromUri("http://localhost").port(port);

		final WebTarget root = client.target(uri);
		final String resultRoot = root.request(MediaType.TEXT_PLAIN).get(String.class);
		verify(resultRoot.equals("Hello, world.\n"), resultRoot);

		final WebTarget nonExistent = root.path("nonExistent");
		try (Response resultNonExistent = nonExistent.request().get()) {
			verify(resultNonExistent.getStatus() == Response.Status.NOT_FOUND.getStatusCode(),
					String.valueOf(resultNonExistent.getStatus()));
		}

		final WebTarget servlet = client.target(uri).path("api").path("servlet");
		final String resultServlet = servlet.request(MediaType.TEXT_PLAIN).get(String.class);
		verify(resultServlet.startsWith("Hello from Weld BeanManager"), resultServlet);

		client.close();
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}
}
