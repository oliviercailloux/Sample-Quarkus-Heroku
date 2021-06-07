package io.github.oliviercailloux.sample_quarkus_heroku;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class ItemsTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemsTests.class);

	@Test
	public void testHelloEndpoint() {
		final Client client = ClientBuilder.newClient();
		final UriBuilder uri = UriBuilder.fromUri("http://localhost").port(8080);

		final WebTarget root = client.target(uri);
		LOGGER.info("Requesting (static).");
		final String resultRoot = root.request(MediaType.TEXT_PLAIN).get(String.class);
		assertTrue(resultRoot.equals("Hello, world.\n"), resultRoot);
		LOGGER.info("Verified (static).");

		final WebTarget nonExistent = root.path("nonExistent");
		LOGGER.info("Requesting (N/A).");
		try (Response resultNonExistent = nonExistent.request().get()) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resultNonExistent.getStatus());
		}
		LOGGER.info("Verified (N/A).");

		final WebTarget servlet = client.target(uri).path("v0").path("items");
		LOGGER.info("Requesting (post).");
		final String post1 = servlet.request().post(null, String.class);
		LOGGER.info("Post 1: {}.", post1);
		final String post2 = servlet.request().post(null, String.class);
		LOGGER.info("Post 2: {}.", post2);
		final String resultServlet = servlet.request(MediaType.TEXT_PLAIN).get(String.class);
		assertTrue(resultServlet.matches("MyItem dated.*\nMyItem dated.*"), resultServlet);

		client.close();
		given().when().get("/items").then().statusCode(200);
	}

}