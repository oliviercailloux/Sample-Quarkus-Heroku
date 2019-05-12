package io.github.oliviercailloux.y2019.jetty;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;

/**
 * From
 * https://github.com/eclipse-ee4j/jersey/blob/master/examples/helloworld-cdi2-se/src/main/java/org/glassfish/jersey/examples/helloworld/cdi2se/ApplicationScopedCounter.java
 */
@ApplicationScoped
public class ApplicationScopedCounter {

	private final AtomicInteger counter = new AtomicInteger();

	public int getNumber() {
		return counter.incrementAndGet();
	}
}
