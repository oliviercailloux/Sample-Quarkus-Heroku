package io.github.oliviercailloux.sample_quarkus_heroku;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/v0")
public class MyApplication extends Application {

}