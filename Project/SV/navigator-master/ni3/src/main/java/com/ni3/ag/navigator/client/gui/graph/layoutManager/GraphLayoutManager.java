/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph.layoutManager;

import javax.swing.*;

import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;

public abstract class GraphLayoutManager{
	public String options[];

	public GraphLayoutManager(){
		options = null;
	}

	public abstract void initialize(GraphCollection graph);

	public abstract boolean needLayout(GraphCollection graph);

	public abstract boolean doLayout(GraphCollection graph);

	public abstract void showPropertyDialog(JFrame parent);

	public abstract String toXML();

	public abstract void fromXML(NanoXML nextX);

	public abstract void action(String option, GraphObject object);

	public abstract void editSettings();

	public abstract void graphChanged(GraphCollection graph);

	public abstract void moveNode(Node n, boolean ctrlPressed, double dx, double dy);

	public abstract boolean isOptionEnabled(Node n, int index);

	public abstract boolean isOptionSelected(Node n, int index);

    public abstract String getName();
}
