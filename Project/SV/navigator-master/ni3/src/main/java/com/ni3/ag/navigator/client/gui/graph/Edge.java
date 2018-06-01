/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph;

import java.awt.*;

import com.ni3.ag.navigator.client.domain.EdgeMetaphor;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class Edge extends GraphObject{
	private int connectionType;
	private double strength;
	private int directed;
	private int metaindex; // Index of edge metaphor definition

	public Node from;
	public Node to;
	public double len;

	public double cx, cy; // Center point of edge

	public int edgeStyle = 0; // 0 - line, 1 - ortho, 2 - angular, 4 - none

	public static final int ES_Line = 0;
	public static final int ES_Ortho = 1;
	public static final int ES_Angular = 2;
	public static final int ES_None = 4;

	public int multiEdgeIndex; // Index used to identify position of this edge inside set of edges between same nodes

	public BasicStroke eStroke; // Stroke that represent edge metaphor

	public Edge(){
		super();

		eStroke = null;

		directed = 0;

		Obj = null;

		len = 30;
		contracted = false;
		setFilteredOut(false);

		multiEdgeIndex = 0;
	}

	public Edge(Edge e){
		from = e.from;
		to = e.to;
		ID = e.ID;
		Type = e.Type;
		Obj = e.Obj;
		connectionType = e.connectionType;
		strength = e.strength;
		directed = e.directed;
		len = e.len;
		contracted = e.contracted;
		setFilteredOut(e.isFilteredOut());
		metaindex = e.metaindex;
		status = e.status;
		cx = e.cx;
		cy = e.cy;

		multiEdgeIndex = e.multiEdgeIndex;
	}

	public int getConnectionType(){
		return connectionType;
	}

	public void setConnectionType(int connectionType){
		this.connectionType = connectionType;
	}

	public int getDirected(){
		return directed;
	}

	public boolean isDirected(){
		return directed == 1;
	}

	public void setDirected(int directed){
		this.directed = directed;
	}

	public double getStrength(){
		return strength;
	}

	public void setStrength(double strength){
		this.strength = strength;
	}

	@Override
	public String toString(){
		return toXML();
	}

	public String toXML(){
		StringBuilder ret = new StringBuilder();

		ret.append("<Edge ID='").append(ID).append("' Center='");
		ret.append(cx).append(",");
		ret.append(cy).append("'");
		ret.append(" ScaleFactor='1'/>\n");

		return ret.toString();
	}

	public void fromXML(NanoXML xml){
		NanoXMLAttribute attr;

		while ((attr = xml.Tag.getNextAttribute()) != null){
			if ("ID".equals(attr.Name)){
				ID = attr.getIntegerValue();
			} else if ("Center".equals(attr.Name)){
				int pos = attr.Value.indexOf(",");
				if (pos != -1){
					try{
						cx = Double.parseDouble(attr.Value.substring(0, pos));
						cy = Double.parseDouble(attr.Value.substring(pos + 1));
					} catch (NumberFormatException e){
					}
				}
			}
		}
	}

	public boolean isActive(){
		return !(contracted || isFilteredOut());
	}

	public boolean IsContracted(){
		return from.contracted || to.contracted;
	}

	public void Contract(Node source){
		if (from == source)
			to.contract(false);
		else
			from.contract(false);

		contracted = true;
	}

	public void Expand(Node source){
		if (from == source)
			to.expand();
		else
			from.expand();

		contracted = from.contracted || to.contracted;
	}

	public boolean IsPointOnEdgeCenterPoint(double x, double y){
		if (Math.abs(cx - x) <= 3 && Math.abs(cy - y) <= 3)
			return true;

		return false;
	}

	public boolean IsPointOnEdgeLine(double x, double y){
		if (GraphGeometry.distanceToSegment(from.getX(), from.getY(), to.getX(), to.getY(), x, y) <= 3)
			return true;

		return false;
	}

	public boolean resolveMetaphor(EdgeMetaphor edgemeta){
		metaindex = edgemeta.resolveMetaphor(from.Type, to.Type, connectionType);
		boolean result = metaindex != EdgeMetaphor.INVALID_INDEX;
		if (!result)
			Ni3.showClientError(Thread.currentThread(), new RuntimeException("Cannot resolve object connection for edge "
					+ ID + "\n\tFromType=" + from.Type + "\n\tToType=" + to.Type + "\n\tConnectionType=" + connectionType));
		return result;
	}

	public void setStrength(double strength, boolean reset){
		setStrength(strength);
		if (reset){
			len = 30;
			eStroke = null;
		}
	}

	public int getMetaindex(){
		return metaindex;
	}
}
