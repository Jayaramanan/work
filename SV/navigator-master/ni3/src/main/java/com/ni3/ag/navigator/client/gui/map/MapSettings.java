/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.map;

import java.awt.*;

import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class MapSettings{
	private int zoom;
	private double nodeScale;
	private double edgeScale;
	private Point mapPosition;

	private double blinkingEdgeScale;

	public MapSettings(){
		zoom = 1;
		nodeScale = 1;
		edgeScale = 1;
		blinkingEdgeScale = 1;
		mapPosition = null;
	}

	public int getZoom(){
		return zoom;
	}

	public void setZoom(int zoom){
		this.zoom = zoom;
	}

	public MapSettings copy(){
		MapSettings settings = new MapSettings();
		settings.zoom = zoom;
		settings.nodeScale = nodeScale;
		settings.edgeScale = edgeScale;
		settings.mapPosition = mapPosition;
		return settings;
	}

	public void setNodeScale(double v){
		this.nodeScale = v;
	}

	public double getNodeScale(){
		return nodeScale;
	}

	public void setEdgeScale(double v){
		this.edgeScale = v;
	}

	public double getEdgeScale(){
		return edgeScale;
	}

	public Point getMapPosition(){
		return mapPosition;
	}

	public void setMapPosition(Point mapPosition){
		this.mapPosition = mapPosition;
	}

	public double getBlinkingEdgeScale(){
		return this.blinkingEdgeScale;
	}

	public void setBlinkingEdgeScale(double blinkingEdgeScale){
		this.blinkingEdgeScale = blinkingEdgeScale;
	}

	public String toXML(){
		StringBuilder ret = new StringBuilder();

		ret.append("<MapSettings");
		ret.append(" Zoom='").append(zoom).append("'");
		ret.append(" NodeScale='").append(nodeScale).append("'");
		ret.append(" EdgeScale='").append(edgeScale).append("'");
		ret.append(" MapPositionX='").append(mapPosition.x).append("'");
		ret.append(" MapPositionY='").append(mapPosition.y).append("'");
		ret.append(" />\n");

		return ret.toString();
	}

	public void fromXML(final NanoXML xml){
		NanoXMLAttribute attr;
		while ((attr = xml.Tag.getNextAttribute()) != null){
			if ("Zoom".equals(attr.Name)){
				zoom = attr.getIntegerValue();
			} else if ("NodeScale".equals(attr.Name)){
				nodeScale = attr.getDoubleValue();
			} else if ("EdgeScale".equals(attr.Name)){
				edgeScale = attr.getDoubleValue();
			} else if ("MapPositionX".equals(attr.Name)){
				if (mapPosition == null){
					mapPosition = new Point();
				}
				mapPosition.x = attr.getIntegerValue();
			} else if ("MapPositionY".equals(attr.Name)){
				if (mapPosition == null){
					mapPosition = new Point();
				}
				mapPosition.y = attr.getIntegerValue();
			}
		}
	}
}
