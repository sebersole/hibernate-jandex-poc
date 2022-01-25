package org.hibernate.orm.test;

/**
 * @author Steve Ebersole
 */
public class NonAnnotatedEntity {
	private Integer id;
	private String name;

	private NonAnnotatedEntity() {
		// for Hibernate use
	}

	public NonAnnotatedEntity(Integer id, String name) {
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