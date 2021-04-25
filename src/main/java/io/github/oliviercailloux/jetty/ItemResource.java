package io.github.oliviercailloux.jetty;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("item")
@RequestScoped
public class ItemResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemResource.class);

	@Context
	private UriInfo uriInfo;

	@Inject
	private ItemService itemS;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getItems() {
		final List<Item> allItems = itemS.getAll();
		return allItems.stream().map(Item::getName).collect(Collectors.joining("\n"));
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response postItem() {
		final Item item = new Item();
		/** Ideally we’d use the client zone here. */
		final ZonedDateTime zonedTimestamp = ZonedDateTime.now(ZoneId.systemDefault());
		item.setName("MyItem dated " + zonedTimestamp);

		itemS.persist(item);

		return Response.seeOther(uriInfo.getAbsolutePath()).build();
	}
}
