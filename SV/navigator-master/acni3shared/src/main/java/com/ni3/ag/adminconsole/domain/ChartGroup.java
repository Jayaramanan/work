/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class ChartGroup implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Group group;
	private Chart chart;

	ChartGroup(){
		super();
	}

	public ChartGroup(Group group, Chart chart){
		this.group = group;
		this.chart = chart;
	}

	public Integer getId(){
		return id;
	}

	void setId(Integer id){
		this.id = id;
	}

	public Group getGroup(){
		return group;
	}

	public void setGroup(Group group){
		this.group = group;
	}

	public Chart getChart(){
		return chart;
	}

	public void setChart(Chart chart){
		this.chart = chart;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof ChartGroup))
			return false;
		if (o == this)
			return true;
		ChartGroup gp = (ChartGroup) o;
		if (getId() == null || gp.getId() == null)
			return false;
		return getId().equals(gp.getId());
	}

    public ChartGroup clone() throws CloneNotSupportedException{
        return (ChartGroup) super.clone();
    }

    public ChartGroup clone(Chart chart, Group group) throws CloneNotSupportedException{
        ChartGroup oug = clone();
        oug.setId(null);
        oug.setChart(chart);
        oug.setGroup(group);
        return oug;
    }

}
