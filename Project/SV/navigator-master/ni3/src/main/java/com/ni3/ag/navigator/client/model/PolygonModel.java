/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.awt.*;

import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class PolygonModel{
	private Set<Integer> polygonNodes;
	private Set<Integer> polylineNodes;
	private float polygonAlpha;
	private Map<Integer, Color> polyColors;

	public PolygonModel(){
		polygonNodes = new HashSet<Integer>();
		polylineNodes = new HashSet<Integer>();
		polygonAlpha = 0.75f;
		polyColors = new HashMap<Integer, Color>();
	}

	public Set<Integer> getPolygonNodes(){
		return polygonNodes;
	}

	public void addPolygonNode(Integer id){
		polygonNodes.add(id);
	}

	public void removePolygonNode(Integer id){
		polygonNodes.remove(id);
		polyColors.remove(id);
	}

	public void setPolygonNodes(Set<Integer> polygonNodes){
		this.polygonNodes = polygonNodes;
	}

	public Set<Integer> getPolylineNodes(){
		return polylineNodes;
	}

	public void addPolylineNode(Integer id){
		polylineNodes.add(id);
	}

	public void removePolylineNode(Integer id){
		polylineNodes.remove(id);
		polyColors.remove(id);
	}

	public void setPolylineNodes(Set<Integer> polylineNodes){
		this.polylineNodes = polylineNodes;
	}

	public float getPolygonAlpha(){
		return polygonAlpha;
	}

	public void setPolygonAlpha(float polygonAlpha){
		this.polygonAlpha = polygonAlpha;
	}

	public Map<Integer, Color> getPolyColors(){
		return polyColors;
	}

	public void addPolyColor(Integer id, Color color){
		polyColors.put(id, color);
	}

	public void setPolyColors(Map<Integer, Color> polyColors){
		this.polyColors = polyColors;
	}

	public PolygonModel copy(){
		PolygonModel copy = new PolygonModel();
		copy.setPolygonNodes(new HashSet<Integer>(polygonNodes));
		copy.setPolylineNodes(new HashSet<Integer>(polylineNodes));
		copy.setPolygonAlpha(polygonAlpha);
		copy.setPolyColors(new HashMap<Integer, Color>(polyColors));
		return copy;
	}

	public String toXml(){
		StringBuilder xml = new StringBuilder();
		if (!polygonNodes.isEmpty() || !polylineNodes.isEmpty()){
			xml.append("<Polygons alpha='").append(polygonAlpha).append("'>");
			for (Integer id : polygonNodes){
				String color = Utility.encodeColor(polyColors.get(id));
				xml.append("<Node id='").append(id).append("' filled='true' color='").append(color).append("'/>");
			}
			for (Integer id : polylineNodes){
				String color = Utility.encodeColor(polyColors.get(id));
				xml.append("<Node id='").append(id).append("' filled='false' color='").append(color).append("'/>");
			}
			xml.append("</Polygons>\n");
		}

		return xml.toString();
	}

	public void fromXML(NanoXML xml){
		NanoXMLAttribute attr;
		while ((attr = xml.Tag.getNextAttribute()) != null){
			if ("alpha".equals(attr.Name)){
				polygonAlpha = attr.getFloatValue();
				break;
			}
		}

		NanoXML nextX;
		while ((nextX = xml.getNextElement()) != null){
			Integer nodeId = null;
			boolean isPolygon = false;
			String colorStr = null;
			while ((attr = nextX.Tag.getNextAttribute()) != null){
				if ("id".equals(attr.Name)){
					nodeId = attr.getIntegerValue();
				} else if ("filled".equals(attr.Name)){
					isPolygon = attr.getBooleanValue();
				} else if ("color".equals(attr.Name)){
					colorStr = attr.getValue();
				}
			}
			if (nodeId != null){
				if (isPolygon){
					polygonNodes.add(nodeId);
				} else{
					polylineNodes.add(nodeId);
				}
				polyColors.put(nodeId, Utility.createColor(colorStr));
			}
		}
	}

	public void clear(){
		polygonNodes.clear();
		polylineNodes.clear();
		polyColors.clear();
	}
}
