package io.github.oliviercailloux.y2019.jetty;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
//		org.h2.tools.Server h2 = org.h2.tools.Server.createTcpServer("-tcpAllowOthers", "-ifNotExists").start();
		org.h2.tools.Server h2 = org.h2.tools.Server.createTcpServer("-tcpAllowOthers").start();
		Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sample-jetty", "sa", "");
		LOGGER.info("Connection Established: " + conn.getMetaData().getDatabaseProductName() + "/" + conn.getCatalog());
		LOGGER.info(h2.getURL());

		Server server = JettyHttpContainerFactory.createServer(URI.create("http://localhost:8080/"), new MyJaxRsApp(),
				false);

		server.start();
		server.join();
	}
}
