package io.github.oliviercailloux.y2019.jetty;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		Server server = JettyHttpContainerFactory.createServer(URI.create("http://localhost:8080/"), new MyJaxRsApp(),
				false);

		server.start();
		server.join();
	}
}
