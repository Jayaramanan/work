/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.domain;

import java.io.Serializable;
import java.util.List;

import com.ni3.ag.navigator.shared.domain.UrlOperation;

public class ObjectDefinition implements Serializable{
	private static final long serialVersionUID = -9196826671139322913L;

	public static final int CONTEXT_EDGE_OBJECT_TYPE_ID = 6;

	private Integer id;

	private Schema schema;
	private Integer objectTypeId;
	private String name;
	private String description;
	private int sort;
	private List<ObjectDefinitionGroup> objectPermissions;
	private List<Attribute> attributes;
	private List<Context> contexts;
	private List<UrlOperation> urlOperations;
	private List<Metaphor> metaphors;

	public ObjectDefinition(Schema schema){
		this.schema = schema;
	}

	public ObjectDefinition(int id){
		this.id = id;
	}

	public ObjectDefinition(){

	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Schema getSchema(){
		return schema;
	}

	public void setSchema(Schema schema){
		this.schema = schema;
	}

	public void setObjectTypeId(Integer objectTypeId){
		this.objectTypeId = objectTypeId;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public boolean isEdge(){
		return (objectTypeId == 4 || objectTypeId == 6);
	}

	public boolean isNode(){
		return (objectTypeId == 2 || objectTypeId == 3);
	}

	public void setSort(int sort){
		this.sort = sort;
	}

	public void setObjectPermissions(List<ObjectDefinitionGroup> objectPermissions){
		this.objectPermissions = objectPermissions;
	}

	public void setAttributes(List<Attribute> attributes){
		this.attributes = attributes;
	}

	public void setContexts(List<Context> contexts){
		this.contexts = contexts;
	}

	public void setUrlOperations(List<UrlOperation> urlOperations){
		this.urlOperations = urlOperations;
	}

	public List<Attribute> getAttributes(){
		return attributes;
	}

	public Integer getObjectTypeId(){
		return objectTypeId;
	}

	public List<Metaphor> getMetaphors(){
		return metaphors;
	}

	public void setMetaphors(List<Metaphor> metaphors){
		this.metaphors = metaphors;
	}

	public Attribute getAttribute(Integer attrId){
		for (Attribute attribute : attributes)
			if (attribute.getId() == attrId)
				return attribute;
		return null;
	}

	public Context getContext(int contextID){
		for (Context context : contexts){
			if (context.getId() == contextID)
				return context;
		}
		return null;
	}

	public Attribute getAttribute(String name){
		for (Attribute attribute : attributes)
			if (attribute.getName().equalsIgnoreCase(name))
				return attribute;
		return null;
	}

	public List<ObjectDefinitionGroup> getObjectPermissions(){
		return objectPermissions;
	}

	public ObjectDefinition clone(){
		ObjectDefinition entity = new ObjectDefinition();
		entity.setId(getId());
		entity.setName(getName());
		entity.setDescription(getDescription());
		entity.setObjectTypeId(getObjectTypeId());
		entity.setSort(getSort());
		return entity;
	}

	public int getSort(){
		return sort;
	}

	public List<Context> getContexts(){
		return contexts;
	}

	public List<UrlOperation> getUrlOperations(){
		return urlOperations;
	}

	@Override
	public boolean equals(Object o){
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ObjectDefinition entity = (ObjectDefinition) o;

		if (id != null ? !id.equals(entity.id) : entity.id != null)
			return false;

		return true;
	}

	@Override
	public int hashCode(){
		return id != null ? id.hashCode() : 0;
	}
}
