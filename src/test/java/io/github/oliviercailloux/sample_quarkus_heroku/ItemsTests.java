package io.github.oliviercailloux.sample_quarkus_heroku;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ItemsTests {

	@Test
	public void testHelloEndpoint() {
		given().when().get("/items").then().statusCode(200);
	}

}