package io.github.oliviercailloux.sample_quarkus_heroku;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/items")
@RequestScoped
public class ItemResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemResource.class);

	@Context
	UriInfo uriInfo;

	@Inject
	ItemService itemS;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getItems() {
		LOGGER.info("Running GET.");
		final List<Item> allItems = itemS.getAll();
		LOGGER.info("Returning {} items.", allItems.size());
		return allItems.stream().map(Item::getName).collect(Collectors.joining("\n"));
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response postItem() {
		LOGGER.info("Running POST.");
		final Item item = new Item();
		/** Ideally we’d use the client zone here. */
		final ZonedDateTime zonedTimestamp = ZonedDateTime.now(ZoneId.systemDefault());
		item.setName("MyItem dated " + zonedTimestamp);

		itemS.persist(item);

		LOGGER.info("Redirecting.");
		return Response.seeOther(uriInfo.getAbsolutePath()).build();
	}
}
