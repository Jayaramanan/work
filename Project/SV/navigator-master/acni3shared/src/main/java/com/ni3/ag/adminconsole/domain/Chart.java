/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.List;

public class Chart implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	public static final String SCHEMA = "schema";
	private Integer id;
	private String name;
	private String comment;
	private Schema schema;
	private List<ChartGroup> chartGroups;
	private List<ObjectChart> objectCharts;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getComment(){
		return comment;
	}

	public void setComment(String comment){
		this.comment = comment;
	}

	public Schema getSchema(){
		return schema;
	}

	public void setSchema(Schema schemaID){
		this.schema = schemaID;
	}

	public List<ChartGroup> getChartGroups(){
		return chartGroups;
	}

	public void setChartGroups(List<ChartGroup> chartGroups){
		this.chartGroups = chartGroups;
	}

	public List<ObjectChart> getObjectCharts(){
		return objectCharts;
	}

	public void setObjectCharts(List<ObjectChart> objectCharts){
		this.objectCharts = objectCharts;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (this == o){
			return true;
		}
		if (o == null || !(o instanceof Chart))
			return false;
		Chart ch = (Chart) o;
		if (ch.getId() == null || getId() == null)
			return false;
		return ch.getId().equals(getId());
	}

	public Chart clone() throws CloneNotSupportedException{
		return (Chart) super.clone();
	}

	public Chart clone(Integer id, Schema schema, List<ChartGroup> chartGroups, List<ObjectChart> objectCharts,
	        List<ChartAttribute> chartAttributes) throws CloneNotSupportedException{
		Chart c = clone();
		c.setId(id);
		c.setSchema(schema);
		c.setChartGroups(chartGroups);
		c.setObjectCharts(objectCharts);
		return c;
	}

}
