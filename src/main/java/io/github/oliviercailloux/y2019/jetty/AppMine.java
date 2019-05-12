package io.github.oliviercailloux.y2019.jetty;

import java.net.URI;

import javax.naming.NamingException;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.jboss.weld.environment.se.Weld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppMine {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(AppMine.class);
	private Server server;

	public static void main(String[] args) throws Exception {
		new AppMine().proceed();
	}

	@SuppressWarnings("unused")
	public void proceed() throws Exception {
//		LOGGER.info("Hello World!");
//		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		context.setContextPath("/");
//
//		Server jettyServer = new Server(8080);
//		jettyServer.setHandler(context);
//
//		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
//		jerseyServlet.setInitOrder(0);
//
//		// Tells the Jersey Servlet which REST service/class to load.
//		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", EntryPoint.class.getCanonicalName());
//
//		try {
//			jettyServer.start();
//			jettyServer.join();
//		} finally {
//			jettyServer.destroy();
//		}
//		server = new Server(8080);

		URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
		server = JettyHttpContainerFactory.createServer(baseUri, new MyApplication());
		Weld weld = new Weld();
		weld.initialize();

//		setJndi();
//
//		WebAppContext context = new WebAppContext();
//
//		context.setContextPath("/");
//
//		context.setResourceBase("src/main/resources");
//
////		// Define an env entry with webapp scope.
////		// At runtime, the webapp accesses this as java:comp/env/wiggle
////		// This is equivalent to putting a web.xml entry in web.xml:
////		// <env-entry>
////		// <env-entry-name>wiggle</env-entry-name>
////		// <env-entry-value>100</env-entry-value>
////		// <env-entry-type>java.lang.Double</env-entry-type>
////		// </env-entry>
////		// Note that the last arg of "true" means that this definition for "wiggle"
////		// would override an entry of the
////		// same name in web.xml
//		new org.eclipse.jetty.plus.jndi.EnvEntry(context, "wiggle", new Double(100), true);
//
////		// Register a mock DataSource scoped to the webapp
////		// This must be linked to the webapp via an entry in web.xml:
////		// <resource-ref>
////		// <res-ref-name>jdbc/mydatasource</res-ref-name>
////		// <res-type>javax.sql.DataSource</res-type>
////		// <res-auth>Container</res-auth>
////		// </resource-ref>
////		// At runtime the webapp accesses this as java:comp/env/jdbc/mydatasource
//		org.eclipse.jetty.plus.jndi.Resource mydatasource = new org.eclipse.jetty.plus.jndi.Resource(context,
//				"jdbc/myds", new JdbcDataSource());
//
//		server.setHandler(context);
//		/**
//		 * https://www.eclipse.org/jetty/documentation/current/using-annotations.html
//		 */
//		context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");
//
////		context.addServlet(SayHelloServlet.class, "/*");
//
//		context.addEventListener(new Listener());

		server.start();
		server.join();
		weld.shutdown();
	}

	private void setJndi() throws NamingException {
		// Enable parsing of jndi-related parts of web.xml and jetty-env.xml
		org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList
				.setServerDefault(server);
		classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
				"org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
		classlist.addAfter("org.eclipse.jetty.plus.webapp.PlusConfiguration",
				"org.eclipse.jetty.annotations.AnnotationConfiguration");

//		// Register new transaction manager in JNDI
//		// At runtime, the webapp accesses this as java:comp/UserTransaction
		org.eclipse.jetty.plus.jndi.Transaction transactionMgr = new org.eclipse.jetty.plus.jndi.Transaction(
				new com.atomikos.icatch.jta.J2eeUserTransaction());
	}
}
