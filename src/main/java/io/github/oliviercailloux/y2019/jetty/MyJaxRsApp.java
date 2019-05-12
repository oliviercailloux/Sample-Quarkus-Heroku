package io.github.oliviercailloux.y2019.jetty;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

public class MyJaxRsApp extends ResourceConfig {
	public MyJaxRsApp() {
		packages(getClass().getPackage().toString());
	}
}