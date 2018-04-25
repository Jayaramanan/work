/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Cluster implements Comparable<Cluster>{
	private Double from;
	private Double to;
	private int objectCount;
	private Color color;
	private List<GeoTerritory> territories;
    private String description;

	public Cluster(final Double from, final Double to){
		this.from = from;
		this.to = to;
		this.territories = new ArrayList<GeoTerritory>();
		this.objectCount = 0;
	}

	public Double getFrom(){
		return from;
	}

	public void setFrom(Double from){
		this.from = from;
	}

	public Double getTo(){
		return to;
	}

	public void setTo(Double to){
		this.to = to;
	}

	public Color getColor(){
		return color;
	}

	public void setColor(Color color){
		this.color = color;
	}

	public int getObjectCount(){
		return objectCount;
	}

	public void setObjectCount(int objectCount){
		this.objectCount = objectCount;
	}

	public List<GeoTerritory> getTerritories(){
		return territories;
	}

	public void setTerritories(List<GeoTerritory> territories){
		this.territories = territories;
	}

	public void addTerritory(GeoTerritory territory){
		this.territories.add(territory);
		this.objectCount += territory.getNodeCount();
	}

	public int getTerritoryCount(){
		return territories != null ? territories.size() : 0;
	}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
	public int compareTo(Cluster o2){
		int result = 0;
		if (from > o2.from)
			result = 1;
		else if (from < o2.from){
			result = -1;
		}
		return result;
	}
}
