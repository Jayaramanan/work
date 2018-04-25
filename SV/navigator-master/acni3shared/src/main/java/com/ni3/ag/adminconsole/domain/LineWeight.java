/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class LineWeight implements Serializable, Comparable<LineWeight>{
	private static final long serialVersionUID = 1L;

	// constant for Criteria in DAO objects - please adjust accordingly is the field name is changed
	public static final String LABEL_DB_COLUMN = "label";

	private Integer id;
	private String label;
	private BigDecimal width;

	public LineWeight(){
	}

	public LineWeight(Integer id, String label, BigDecimal width){
		this.id = id;
		this.label = label;
		this.width = width;
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public BigDecimal getWidth(){
		return width;
	}

	public void setWidth(BigDecimal width){
		this.width = width;
	}

	public int compareTo(LineWeight o){
		if (this.getLabel() == null && (o == null || o.getLabel() == null)){
			return 0;
		} else if (this.getLabel() == null){
			return -1;
		} else if (o == null || o.getLabel() == null){
			return 1;
		}
		return getLabel().compareTo(o.getLabel());
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof LineWeight))
			return false;
		if (o == this)
			return true;
		LineWeight dt = (LineWeight) o;
		if (getId() == null || dt.getId() == null)
			return false;
		return getId().equals(dt.getId());
	}

	@Override
	public String toString(){
		return label;
	}

}
