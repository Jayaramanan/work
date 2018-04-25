/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class SchemaGroup implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	public static final String SCHEMA = "schema";
	public static final String GROUP = "group";

	private Schema schema;
	private Group group;
	private Integer canRead_;

	SchemaGroup(){
	}

	public SchemaGroup(Schema schema, Group group){
		setSchema(schema);
		setGroup(group);
	}

	public Schema getSchema(){
		return schema;
	}

	public void setSchema(Schema schema){
		this.schema = schema;
	}

	public Group getGroup(){
		return group;
	}

	public void setGroup(Group group){
		this.group = group;
	}

	public Boolean isCanRead(){
		return getCanRead_() != null && getCanRead_() == 1;
	}

	public void setCanRead(Boolean canRead){
		setCanRead_(canRead ? 1 : 0);
	}

	private Integer getCanRead_(){
		return canRead_;
	}

	private void setCanRead_(Integer canRead){
		canRead_ = canRead;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof SchemaGroup))
			return false;
		if (o == this)
			return true;
		SchemaGroup oug = (SchemaGroup) o;
		if (getSchema() == null || oug.getSchema() == null)
			return false;
		if (getGroup() == null || oug.getGroup() == null)
			return false;
		return getGroup().equals(oug.getGroup()) && getSchema().equals(oug.getSchema());
	}

	public SchemaGroup clone() throws CloneNotSupportedException{
		return (SchemaGroup) super.clone();
	}

	public SchemaGroup clone(Schema schema, Group group) throws CloneNotSupportedException{
		SchemaGroup oug = clone();
		oug.setSchema(schema);
		oug.setGroup(group);
		return oug;
	}
}
