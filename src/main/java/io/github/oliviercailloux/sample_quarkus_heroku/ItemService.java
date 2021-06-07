package io.github.oliviercailloux.sample_quarkus_heroku;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class ItemService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);

	@Inject
	EntityManager em;

	@Inject
	QueryHelper helper;

	@Transactional
	public List<Item> getAll() {
		return em.createQuery(helper.selectAll(Item.class)).getResultList();
	}

	@Transactional
	public void persist(Item item) {
		em.persist(item);
	}
}
