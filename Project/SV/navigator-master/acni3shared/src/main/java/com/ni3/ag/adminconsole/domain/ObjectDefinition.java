/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ObjectDefinition implements Serializable, Cloneable, Comparable<ObjectDefinition>{
	private static final long serialVersionUID = 1L;

	public static final int OBJECT_MAX_NAME_LENGTH = 25;

	// constant for Criteria in DAO objects - please adjust accordingly is the field name is changed
	public static final String SCHEMA = "schema";
	public static final String IN_METAPHOR_FILTER = "inMetaphor";
	public static final String NOT_OBJECT_TYPE_FILTER = "notObjectType";
	public static final String WITH_OBJECT_TYPE_FILTER = "withObjectType";
	public static final String OBJECT_TYPE_PARAM = "objTypeID";
	public static final String OBJECT_TYPE_PROPERTY = "objectType_";
	public static final String OBJECT_NAME_DB_COLUMN = "name";
	public static final String CHILD_OF_FILTER = "childOf";
	public static final String CREATED_BY = "createdBy";
	public static final String ID = "id";

	private Integer id;

	private Schema schema;
	private Integer objectType_;
	private String name;
	private String description;
	private Date creationDate;
	private User createdBy;
	private String tableName;
	private Integer sort;
	private List<ObjectAttribute> objectAttributes;
	private List<ObjectConnection> objectConnections;
	private List<ObjectGroup> objectGroups;
	private List<Metaphor> metaphors;
	private Context context;

	public ObjectDefinition(){
		setSort(new Integer(0));
		setDescription("");
	}

	public List<ObjectAttribute> getObjectAttributes(){
		return objectAttributes;
	}

	public void setObjectAttributes(List<ObjectAttribute> objectAttributes){
		this.objectAttributes = objectAttributes;
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

	public Integer getObjectType_(){
		return objectType_;
	}

	public void setObjectType_(Integer objectType){
		objectType_ = objectType;
	}

	public ObjectType getObjectType(){
		return objectType_ != null ? ObjectType.fromInt(objectType_) : null;
	}

	public void setObjectType(ObjectType objectType){
		this.objectType_ = objectType != null ? objectType.toInt() : null;
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

	public Date getCreationDate(){
		return creationDate;
	}

	public void setCreationDate(Date creation){
		creationDate = creation;
	}

	public User getCreatedBy(){
		return createdBy;
	}

	public void setCreatedBy(User createdBy){
		this.createdBy = createdBy;
	}

	public String getTableName(){
		return tableName;
	}

	public void setTableName(String tableName){
		this.tableName = tableName;
	}

	public Integer getSort(){
		return sort;
	}

	public void setSort(Integer sort){
		this.sort = sort;
	}

	public List<ObjectConnection> getObjectConnections(){
		return objectConnections;
	}

	public void setObjectConnections(List<ObjectConnection> objectConnections){
		this.objectConnections = objectConnections;
	}

	public List<ObjectGroup> getObjectGroups(){
		return objectGroups;
	}

	public void setObjectGroups(List<ObjectGroup> objectGroups){
		this.objectGroups = objectGroups;
	}

	public List<Metaphor> getMetaphors(){
		return metaphors;
	}

	public void setMetaphors(List<Metaphor> metaphors){
		this.metaphors = metaphors;
	}

	public Context getContext(){
		return context;
	}

	public void setContext(Context context){
		this.context = context;
	}

	public boolean hasContextAttributes(){
		for (ObjectAttribute attribute : getObjectAttributes()){
			if (attribute.isInContext()){
				return true;
			}
		}
		return false;
	}

	public boolean isNode(){
		return getObjectType() == ObjectType.NODE;
	}

	public boolean isEdge(){
		return getObjectType() == ObjectType.EDGE || getObjectType() == ObjectType.CONTEXT_EDGE;
	}

	public boolean isContextEdge(){
		return getObjectType() == ObjectType.CONTEXT_EDGE;
	}

	/**
	 * This method should return "name" as the first characters to allow key selection in drop-downs See AC-1459
	 */
	@Override
	public String toString(){
		return name + " [ObjectDefinition] id = " + id;
	}

	public int compareTo(ObjectDefinition o){
		if (this.getName() == null && (o == null || o.getName() == null)){
			return 0;
		} else if (this.getName() == null){
			return -1;
		} else if (o == null || o.getName() == null){
			return 1;
		}
		return getName().compareTo(o.getName());
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof ObjectDefinition)){
			return false;
		}
		if (getId() == null || ((ObjectDefinition) obj).getId() == null){
			return false;
		}
		return getId().intValue() == ((ObjectDefinition) obj).getId().intValue();
	}

	public ObjectDefinition clone() throws CloneNotSupportedException{
		return (ObjectDefinition) super.clone();
	}

	public ObjectDefinition clone(Integer id, Schema schema, List<ObjectAttribute> objectAttributes,
	        List<ObjectConnection> objectConnections, List<ObjectGroup> objectGroups, List<Metaphor> metaphors,
	        Context context, User user) throws CloneNotSupportedException{
		ObjectDefinition od = clone();
		od.setId(id);
		od.setSchema(schema);
		od.setObjectAttributes(objectAttributes);
		od.setObjectConnections(objectConnections);
		od.setObjectGroups(objectGroups);
		od.setMetaphors(metaphors);
		od.setCreatedBy(user);
		return od;
	}
}
