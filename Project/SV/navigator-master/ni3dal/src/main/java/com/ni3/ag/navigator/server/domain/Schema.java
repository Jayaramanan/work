/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.domain;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class Schema{
	private static final Logger log = Logger.getLogger(Schema.class);

	private int id;
	private List<ObjectDefinition> definitions;
	private String name;
	private Date creation;

	public Schema(){
	}

	public Date getCreation(){
		return creation;
	}

	public List<ObjectDefinition> getDefinitions(){
		return definitions;
	}

	public ObjectDefinition getEntity(final int entityId){
		for (final ObjectDefinition e : definitions){
			if (e.getId() == entityId){
				return e;
			}
		}

		log.warn("Entity with id " + entityId + " is not found");
		return null;
	}

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public void setCreation(final Date creation){
		this.creation = creation;
	}

	public void setDefinitions(final List<ObjectDefinition> definitions){
		this.definitions = definitions;
	}

	public void setId(final int id){
		this.id = id;
	}

	public void setName(final String name){
		this.name = name;
	}

	@Override
	public String toString(){
		return "Schema [id=" + id + ", definitions=" + definitions + ", name=" + name + ", creation=" + creation + "]";
	}

	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Schema schema = (Schema) o;

		if (id != schema.id) return false;

		return true;
	}

	@Override
	public int hashCode(){
		return id;
	}
}
