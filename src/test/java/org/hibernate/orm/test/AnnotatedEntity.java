package org.hibernate.orm.test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @author Steve Ebersole
 */
@Entity
public class AnnotatedEntity {
	@Id
	private Integer id;
	private String name;

	private AnnotatedEntity() {
		// for Hibernate use
	}

	public AnnotatedEntity(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}