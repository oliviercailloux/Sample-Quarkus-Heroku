package io.github.oliviercailloux.sample_quarkus_heroku;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	public Item() {
		name = "";
	}

	public int getId() {
		return id;
	}

	/**
	 * Returns the item name.
	 *
	 * @return not {@code null}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the item name.
	 *
	 * @param name {@code null} strings are converted to empty strings.
	 */
	public void setName(String name) {
		this.name = name == null ? "" : name;
	}

}
