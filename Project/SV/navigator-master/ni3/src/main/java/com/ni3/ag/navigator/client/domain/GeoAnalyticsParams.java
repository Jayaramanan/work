/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.shared.domain.GeoObjectSource;
import com.ni3.ag.navigator.shared.domain.GisTerritory;

public class GeoAnalyticsParams{
	private GisTerritory layer;
	private Entity entity;
	private Attribute attribute;
	private GeoObjectSource source;

	public GisTerritory getLayer(){
		return layer;
	}

	public void setLayer(GisTerritory layer){
		this.layer = layer;
	}

	public Entity getEntity(){
		return entity;
	}

	public void setEntity(Entity entity){
		this.entity = entity;
	}

	public Attribute getAttribute(){
		return attribute;
	}

	public void setAttribute(Attribute attribute){
		this.attribute = attribute;
	}

	public GeoObjectSource getSource(){
		return source;
	}

	public void setSource(GeoObjectSource source){
		this.source = source;
	}

}
