package io.github.oliviercailloux.jetty;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * From
 * https://github.com/eclipse-ee4j/jersey/blob/master/examples/helloworld-cdi2-se/src/main/java/org/glassfish/jersey/examples/helloworld/cdi2se/ApplicationScopedCounter.java
 */
@ApplicationScoped
public class ApplicationScopedCounter {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationScopedCounter.class);

	private final AtomicInteger counter = new AtomicInteger();

	public int getNumber() {
		return counter.get();
	}

	public int incrementNumber() {
		return counter.incrementAndGet();
	}

}
