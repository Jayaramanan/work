/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class License implements Serializable{
	private static final long serialVersionUID = -2913372538127881483L;

	public static final String PRODUCT_DB_COLUMN_NAME = "product";

	private Integer id;
	private String product;
	private String license;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getProduct(){
		return product;
	}

	public void setProduct(String product){
		this.product = product;
	}

	public String getLicense(){
		return license;
	}

	public void setLicense(String license){
		this.license = license;
	}

	@Override
	public boolean equals(Object o){
		if (o == null)
			return false;
		if (!(o instanceof License))
			return false;
		if (o == this)
			return true;
		License l = (License) o;
		return id.equals(l.id);
	}
}
