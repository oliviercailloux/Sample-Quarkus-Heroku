package io.github.oliviercailloux.jetty;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import io.github.oliviercailloux.javaee_jpa_inject_servlets.utils.QueryHelper;

@RequestScoped
public class ItemService {
	@Inject
	private EntityManager em;

	@Inject
	private QueryHelper helper;

	@Transactional
	public List<Item> getAll() {
		return em.createQuery(helper.selectAll(Item.class)).getResultList();
	}

	@Transactional
	public void persist(Item item) {
		em.persist(item);
	}
}
