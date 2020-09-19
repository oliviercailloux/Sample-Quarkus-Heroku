package io.github.oliviercailloux.jetty;

import static com.google.common.base.Verify.verify;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.cdi.CdiDecoratingListener;
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

public class App {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		final String envPort = System.getenv("PORT");
		final int port = envPort != null ? Integer.parseInt(envPort) : 8080;

		final Server server = new Server();

		{
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

		{
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
			LOGGER.info("Set handler: {}.", server.getHandler());

			servletHandler.setInitParameter(CdiServletContainerInitializer.CDI_INTEGRATION_ATTRIBUTE,
					CdiDecoratingListener.MODE);
			servletHandler.addBean(new ServletContextHandler.Initializer(servletHandler, new EnhancedListener()));
			servletHandler.addBean(
					new ServletContextHandler.Initializer(servletHandler, new CdiServletContainerInitializer()));
		}

		server.start();

		{
			final Client client = ClientBuilder.newClient();
			final URI uri = new URI("http", null, "localhost", port, "/", null, null);

			final WebTarget root = client.target(uri);
			final String resultRoot = root.request(MediaType.TEXT_PLAIN).get(String.class);
			verify(resultRoot.equals("Hello, world.\n"), resultRoot);

			final WebTarget servlet = client.target(uri).path("api").path("servlet");
			final String resultServlet = servlet.request(MediaType.TEXT_PLAIN).get(String.class);
			verify(resultServlet.startsWith("Hello from Weld"), resultServlet);

			client.close();
		}

		server.join();
	}
}
