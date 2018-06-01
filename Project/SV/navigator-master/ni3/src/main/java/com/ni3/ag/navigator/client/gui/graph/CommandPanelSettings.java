/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph;

import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class CommandPanelSettings{
	private boolean showDirectedEdges;
	private boolean showEdgeThickness;
	private boolean showEdgeLabels;
	private boolean showNodeLabels;

	public boolean isShowDirectedEdges(){
		return showDirectedEdges;
	}

	public void setShowDirectedEdges(boolean showDirectedEdges){
		this.showDirectedEdges = showDirectedEdges;
	}

	public boolean isShowEdgeThickness(){
		return showEdgeThickness;
	}

	public void setShowEdgeThickness(boolean showEdgeThickness){
		this.showEdgeThickness = showEdgeThickness;
	}

	public boolean isShowEdgeLabels(){
		return showEdgeLabels;
	}

	public void setShowEdgeLabels(boolean showEdgeLabels){
		this.showEdgeLabels = showEdgeLabels;
	}

	public boolean isShowNodeLabels(){
		return showNodeLabels;
	}

	public void setShowNodeLabels(boolean showNodeLabels){
		this.showNodeLabels = showNodeLabels;
	}

	public CommandPanelSettings copy(){
		CommandPanelSettings copy = new CommandPanelSettings();
		copy.showDirectedEdges = showDirectedEdges;
		copy.showEdgeThickness = showEdgeThickness;
		copy.showEdgeLabels = showEdgeLabels;
		copy.showNodeLabels = showNodeLabels;
		return copy;
	}

	public String toXML(){
		StringBuilder ret = new StringBuilder(1024);

		ret.append("<CommandPanel");
		ret.append(" directedGraph='").append(showDirectedEdges).append("'");
		ret.append(" EdgeThickness='").append(showEdgeThickness).append("'");
		ret.append(" EdgeLabels='").append(showEdgeLabels).append("'");
		ret.append(" showLabels='").append(showNodeLabels).append("'");
		ret.append(" />\n");

		return ret.toString();
	}

	public void fromXML(NanoXML xml){
		NanoXMLAttribute attr;
		while ((attr = xml.Tag.getNextAttribute()) != null){
			if ("directedGraph".equals(attr.Name)){
				showDirectedEdges = attr.getBooleanValue();
			} else if ("EdgeThickness".equals(attr.Name)){
				showEdgeThickness = attr.getBooleanValue();
			} else if ("EdgeLabels".equals(attr.Name)){
				showEdgeLabels = attr.getBooleanValue();
			} else if ("showLabels".equals(attr.Name)){
				showNodeLabels = attr.getBooleanValue();
			}
		}
	}
}