package io.github.oliviercailloux.jetty;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.jetty.utils.ServletHelper;

@Path("item")
@RequestScoped
public class ItemResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemResource.class);

	@Inject
	private ServletHelper servletHelper;

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

		return Response.seeOther(servletHelper.getRedirectURI()).build();
	}
}