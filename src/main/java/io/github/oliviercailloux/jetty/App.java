package io.github.oliviercailloux.jetty;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.server.ContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		final String envPort = System.getenv("PORT");
		final int port = envPort != null ? Integer.parseInt(envPort) : 8080;

		final JettyHttpContainer jerseyHandler = ContainerFactory.createContainer(JettyHttpContainer.class,
				new MyJaxRsApp());
		final Server server = new Server();
		final HttpConfiguration config = new HttpConfiguration();
		/**
		 * Add support for X-Forwarded headers: thanks to
		 * https://stackoverflow.com/a/28520946. Required because of
		 * https://devcenter.heroku.com/articles/http-routing.
		 */
		config.addCustomizer(new ForwardedRequestCustomizer());
		@SuppressWarnings("resource")
		final ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(config));
		/** According to the SO (link here above), the port must be set here. */
		connector.setPort(port);
		server.setConnectors(new Connector[] { connector });

		/**
		 * https://www.eclipse.org/jetty/documentation/current/embedding-jetty.html#_using_handlers
		 */
		final ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "hello.txt" });
		resource_handler.setResourceBase("src/main/webapp");

		final HandlerList handlers = new HandlerList();
		handlers.addHandler(resource_handler);
		handlers.addHandler(jerseyHandler);
		server.setHandler(handlers);
		LOGGER.info("Set handler: {}.", server.getHandler());

		server.start();
		final Client client = ClientBuilder.newClient();
		final URI uri = new URI("http", null, "localhost", port, "/", null, null);
		final WebTarget target = client.target(uri).path("counter");
		final int result = target.request(MediaType.TEXT_PLAIN).get(Integer.class);
		client.close();
		LOGGER.info("Got counter: {}.", result);

		server.join();
	}
}
