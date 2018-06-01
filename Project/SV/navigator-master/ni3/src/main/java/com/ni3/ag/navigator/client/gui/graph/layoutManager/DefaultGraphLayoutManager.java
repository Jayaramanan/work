package com.ni3.ag.navigator.client.gui.graph.layoutManager;

import java.util.List;
import java.awt.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.GraphPanelSettings;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;

public abstract class DefaultGraphLayoutManager extends GraphLayoutManager{

	@Override
	public void action(String option, GraphObject object){
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doLayout(GraphCollection graph){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void editSettings(){
		// TODO Auto-generated method stub

	}

	@Override
	public void fromXML(NanoXML node){
		// TODO Auto-generated method stub

	}

	@Override
	public void graphChanged(GraphCollection graph){
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(GraphCollection graph){
		// TODO Auto-generated method stub

	}

	@Override
	public void moveNode(Node n, boolean ctrlPressed, double dx, double dy){
		n.setX(n.getX() + dx);
		n.setY(n.getY() + dy);

		if (ctrlPressed){
			for (Edge eg : n.outEdges){
				// TODO NETVIZ-81 if(eg.to.degree > pick.degree)
				if (isEdgeFiltered(eg))
					continue;
				if (!eg.to.isLeading() && eg.to != n){
					eg.to.setX(eg.to.getX() + dx);
					eg.to.setY(eg.to.getY() + dy);
				}
			}

			for (Edge eg : n.inEdges){
				// TODO NETVIZ-81 if(eg.from.degree >
				// pick.degree)
				if (isEdgeFiltered(eg))
					continue;
				if (!eg.from.isLeading() && eg.from != n){
					eg.from.setX(eg.from.getX() + dx);
					eg.from.setY(eg.from.getY() + dy);
				}
			}
		}
	}

	private boolean isEdgeFiltered(Edge eg){
		return SystemGlobals.MainFrame.Doc.filter.isObjectFilteredOut(eg.Obj);
	}

	@Override
	public boolean needLayout(GraphCollection graph){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showPropertyDialog(JFrame parent){
		// TODO Auto-generated method stub

	}

	@Override
	public String toXML(){
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isOptionSelected(Node n, int index){
		return false;
	}

	protected void randomPlace(GraphCollection graph){
		GraphPanelSettings graphSettings = SystemGlobals.MainFrame.Doc.getGraphVisualSettings();
		Point startPoint = new Point(graphSettings.getFirstPoint());
		Dimension dim = new Dimension(graphSettings.getCanvasSize());
		startPoint.x += 50;
		startPoint.y += 50;
		synchronized (graph.getNodes()){
			for (Node n : graph.getNodes()){
				n.setX(startPoint.x + Math.random() * (dim.width - 100));
				n.setY(startPoint.y + Math.random() * (dim.height - 100));
			}
		}
	}

	protected int getMaxCellWidth(List<Node> nodes){
		int maxWidth = 50;
		for (Node n : nodes){
			int width = n.getScaledMetaphorWidth(true);
			if (width > maxWidth){
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	protected int getMaxCellHeight(List<Node> nodes){
		int maxHeight = 50;
		int maxAdd = 0;
		for (Node n : nodes){
			int height = n.getScaledMetaphorHeight(true);
			if (height > maxHeight){
				maxHeight = height;
			}
			if (n.getChartNegativePartHeight() != 0){
				height = (int) (Math.abs(n.getChartNegativePartHeight()) * Node.DEFAULT_SCALE_FACTOR);
				if (height > maxAdd){
					maxAdd = height;
				}
			}
		}
		return maxHeight + maxAdd;
	}
}
