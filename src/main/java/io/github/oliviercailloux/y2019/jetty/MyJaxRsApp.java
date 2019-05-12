package io.github.oliviercailloux.y2019.jetty;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("v1")
public class MyJaxRsApp extends Application {
	/** Empty. The server will then discover all resource classes automatically. */
}
