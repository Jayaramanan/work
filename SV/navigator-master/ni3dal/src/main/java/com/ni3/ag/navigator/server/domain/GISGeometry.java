/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.domain;

import org.postgis.PGgeometry;

public class GISGeometry{
	private int id;
	private PGgeometry geometry;

	/**
	 * @param id
	 * @param geometry
	 */
	public GISGeometry(int id, PGgeometry geometry){
		super();
		this.id = id;
		this.geometry = geometry;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public PGgeometry getGeometry(){
		return geometry;
	}

	public void setGeometry(PGgeometry geometry){
		this.geometry = geometry;
	}

}
