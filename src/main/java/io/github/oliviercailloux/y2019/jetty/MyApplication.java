package io.github.oliviercailloux.y2019.jetty;

import org.glassfish.jersey.server.ResourceConfig;

public class MyApplication extends ResourceConfig {
	public MyApplication() {
		packages(getClass().getPackage().toString());
	}
}