/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.model;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.shared.domain.Cluster;
import com.ni3.ag.navigator.shared.domain.GeoObjectSource;
import com.ni3.ag.navigator.shared.domain.GeoTerritory;
import com.ni3.ag.navigator.shared.domain.GisTerritory;

public class GeoAnalyticsModel{
	private GisTerritory layer;
	private Entity entity;
	private Attribute attribute;
	private GeoObjectSource source;
	private List<GeoTerritory> territories;
	private List<Cluster> clusters;
	private Color startColor;
	private Color endColor;

	private Map<Integer, List<GeoTerritory>> allTerritoryMap;
	private Set<GeoTerritory> filteredOutTerritories;

	public GeoAnalyticsModel(){
		allTerritoryMap = new HashMap<Integer, List<GeoTerritory>>();
		filteredOutTerritories = new HashSet<GeoTerritory>();
	}

	public List<GeoTerritory> getTerritories(){
		return territories;
	}

	public void setTerritories(List<GeoTerritory> territories){
		this.territories = territories;
	}

	public List<Cluster> getClusters(){
		return clusters;
	}

	public void setClusters(List<Cluster> clusters){
		this.clusters = clusters;
	}

	public Map<Integer, List<GeoTerritory>> getAllTerritoryMap(){
		return allTerritoryMap;
	}

	public List<GeoTerritory> getAllGeoTerritories(Integer gisTerritoryId){
		return allTerritoryMap.get(gisTerritoryId);
	}

	public void setAllTerritoryMap(Map<Integer, List<GeoTerritory>> territoryMap){
		this.allTerritoryMap = territoryMap;
	}

	public Set<GeoTerritory> getFilteredOutTerritories(){
		return filteredOutTerritories;
	}

	public void setFilteredOutTerritories(Set<GeoTerritory> filteredOutTerritories){
		this.filteredOutTerritories.clear();
		this.filteredOutTerritories.addAll(filteredOutTerritories);
	}

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

	public Color getStartColor(){
		return startColor;
	}

	public void setStartColor(Color baseColor){
		this.startColor = baseColor;
	}

	public Color getEndColor(){
		return endColor;
	}

	public void setEndColor(Color endColor){
		this.endColor = endColor;
	}

}
