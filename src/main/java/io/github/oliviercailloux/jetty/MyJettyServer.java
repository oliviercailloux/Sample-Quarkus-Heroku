package io.github.oliviercailloux.jetty;

import static com.google.common.base.Verify.verify;

import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.ats.jta.utils.JNDIManager;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;
import com.google.common.base.VerifyException;
import io.github.oliviercailloux.jee.TransactionalConnectionProvider;
import javax.naming.InitialContext;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
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
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.h2.jdbcx.JdbcDataSource;
import org.jboss.weld.environment.servlet.EnhancedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJettyServer {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MyJettyServer.class);

	public static void main(String[] args) throws Exception {
		final String envPort = System.getenv("PORT");
		final int port = envPort != null ? Integer.parseInt(envPort) : 8080;

		final MyJettyServer jetty = new MyJettyServer(port);
		jetty.setConnectors();
		jetty.setHandlers();

		/**
		 * Must registrer Narayana in JNDI, so that Hibernate finds it (see also
		 * https://groups.google.com/g/narayana-users/c/lnWEBPbFzpw).
		 */
		JNDIManager.bindJTAImplementation();
		/**
		 * Below: adapted from QuickstartApplication,
		 * https://github.com/jbosstm/quickstart/tree/master/jta-and-hibernate-standalone
		 */
		final JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:");
		dataSource.setUser(TransactionalConnectionProvider.USERNAME);
		dataSource.setPassword(TransactionalConnectionProvider.PASSWORD);
		new InitialContext().bind(TransactionalConnectionProvider.DATASOURCE_JNDI, dataSource);
		LOGGER.info("DS bound.");

		/*
		 * The implementation of the JDBCAccess interface to use should be set in the
		 * ObjectStoreEnvironmentBean.jdbcUserDbAccessClassName property variable.
		 *
		 * ObjectStoreEnvironmentBean.objectStoreType per object?
		 *
		 */
		BeanPopulator.getDefaultInstance(ObjectStoreEnvironmentBean.class)
				.setObjectStoreType("com.arjuna.ats.internal.arjuna.objectstore.jdbc.JDBCStore");
		/*
		 * Found this through https://github.com/search?q=DataSourceJDBCAccess&type=code
		 */
		BeanPopulator.getDefaultInstance(ObjectStoreEnvironmentBean.class).setJdbcAccess(
				"com.arjuna.ats.internal.arjuna.objectstore.jdbc.accessors.DataSourceJDBCAccess;datasourceName="
						+ TransactionalConnectionProvider.DATASOURCE_JNDI);
		/* Do these need to be set as well? */
//		BeanPopulator.getNamedInstance(ObjectStoreEnvironmentBean.class, "communicationStore")
//				.setObjectStoreDir("target/tx-object-store");
//		BeanPopulator.getNamedInstance(ObjectStoreEnvironmentBean.class, "stateStore")
//				.setObjectStoreDir("target/tx-object-store");

		jetty.start();
		LOGGER.info("Started.");

		try {
			jetty.verifyResponses();
		} catch (VerifyException | InternalServerErrorException | NotFoundException e) {
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
		final ServletHolder jerseyHolder = new ServletHolder(ServletContainer.class);
		final String appName = MyJaxRsApp.class.getCanonicalName();
		jerseyHolder.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, appName);
//		jerseyHolder.setInitParameter(ServletProperties.PROVIDER_WEB_APP, "true");
		servletHandler.addServlet(jerseyHolder, "/*");

		handlers.addHandler(resourceHandler);
		handlers.addHandler(servletHandler);
		server.setHandler(handlers);
		LOGGER.debug("Set handler: {}.", server.getHandler());

		/**
		 * Using the approach recommended at
		 * https://github.com/eclipse/jetty.project/issues/5326#issuecomment-699506325.
		 */
		servletHandler
				.addBean(new ServletContextHandler.Initializer(servletHandler, new CdiServletContainerInitializer()));
		servletHandler.addBean(new ServletContextHandler.Initializer(servletHandler, new EnhancedListener()));
		LOGGER.info("Initialized servlet handler: {}.", servletHandler);
	}

	public void start() throws Exception {
		server.start();
	}

	public void verifyResponses() {
		final Client client = ClientBuilder.newClient();
		final UriBuilder uri = UriBuilder.fromUri("http://localhost").port(port);

		final WebTarget root = client.target(uri);
		LOGGER.info("Requesting (static).");
		final String resultRoot = root.request(MediaType.TEXT_PLAIN).get(String.class);
		verify(resultRoot.equals("Hello, world.\n"), resultRoot);
		LOGGER.info("Verified (static).");

		final WebTarget nonExistent = root.path("nonExistent");
		LOGGER.info("Requesting (N/A).");
		try (Response resultNonExistent = nonExistent.request().get()) {
			verify(resultNonExistent.getStatus() == Response.Status.NOT_FOUND.getStatusCode(),
					String.valueOf(resultNonExistent.getStatus()));
		}
		LOGGER.info("Verified (N/A).");

		final WebTarget servlet = client.target(uri).path("api").path("item");
		LOGGER.info("Requesting (post).");
		final String post1 = servlet.request().post(null, String.class);
		LOGGER.info("Post 1: {}.", post1);
		final String post2 = servlet.request().post(null, String.class);
		LOGGER.info("Post 2: {}.", post2);
		final String resultServlet = servlet.request(MediaType.TEXT_PLAIN).get(String.class);
		verify(resultServlet.matches("MyItem dated.*\nMyItem dated.*"), resultServlet);

		client.close();
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}
}
