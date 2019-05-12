package io.github.oliviercailloux.y2019.jetty;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8080/v1/"),
				new MyJaxRsApp(), false);
		Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

		server.start();

		Thread.currentThread().join();
	}
}
