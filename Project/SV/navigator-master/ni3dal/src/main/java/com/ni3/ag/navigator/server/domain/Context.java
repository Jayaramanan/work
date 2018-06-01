package com.ni3.ag.navigator.server.domain;

import java.util.List;

public class Context{

	private int id;
	private ObjectDefinition objectDefinition;
	private Attribute pkAttribute;
	private String name;
	private String tablename;
	private List<Attribute> attributes;

	public Attribute getPkAttribute(){
		return pkAttribute;
	}

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public void setPkAttribute(final Attribute attribute){
		this.pkAttribute = attribute;
	}

	public void setId(final int id){
		this.id = id;
	}

	public void setName(final String name){
		this.name = name;
	}

	public void setObjectDefinition(final ObjectDefinition objectDefinition){
		this.objectDefinition = objectDefinition;
	}

	public ObjectDefinition getObjectDefinition(){
		return objectDefinition;
	}

	public void setAttributes(List<Attribute> attributes){
		this.attributes = attributes;
	}

	public List<Attribute> getAttributes(){
		return attributes;
	}

	public String getTablename(){
		return tablename;
	}

	public void setTablename(String tablename){
		this.tablename = tablename;
	}

	@Override
	public String toString(){
		return "Context [id=" + id + ", objectDefinitionId=" + objectDefinition.getId() + ", pkAttributeId=" + pkAttribute
		        + ", name=" + name + ", tablename=" + tablename + "]";
	}
}
