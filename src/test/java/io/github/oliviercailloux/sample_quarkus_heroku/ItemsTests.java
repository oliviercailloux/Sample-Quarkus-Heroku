package io.github.oliviercailloux.sample_quarkus_heroku;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.core.Is;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ItemsTests {

	@Test
	public void testGet() {
		given().when().get("/v0/items").then().statusCode(200).body(Is.is(""));
	}

	@Test
	public void testPost() {
		given().post("/v0/items");
		given().when().get("/v0/items").then().statusCode(200).body(MatchesPattern.matchesPattern("MyItem dated .*"));
		given().post("/v0/items");
		given().when().get("/v0/items").then().statusCode(200)
				.body(MatchesPattern.matchesPattern("MyItem dated .*\nMyItem dated .*"));
	}

}