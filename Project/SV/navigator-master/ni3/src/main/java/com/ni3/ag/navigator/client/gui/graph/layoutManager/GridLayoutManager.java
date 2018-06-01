package com.ni3.ag.navigator.client.gui.graph.layoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.awt.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;

public class GridLayoutManager extends DefaultGraphLayoutManager{
	public static final String NAME = "Grid";

	List<Node> grid;
	MatrixSortOrder order = null;

	class GridLayouData{
		public Point pt;
		public boolean manuallyAdjusted;

		public GridLayouData(int x, int y, boolean manuallyAdjusted){
			pt = new Point(x, y);
			this.manuallyAdjusted = manuallyAdjusted;
		}
	}

	HashMap<Integer, GridLayouData> entityPositions;

	public GridLayoutManager(){
		super();

		grid = new ArrayList<Node>();
		order = SystemGlobals.MainFrame.Doc.getMatrixSort();
		entityPositions = new HashMap<Integer, GridLayouData>();
	}

	@Override
	public void action(String option, GraphObject object){
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doLayout(GraphCollection graph){
		grid.clear();

		final List<Node> displayedNodes = graph.getDisplayedNodes();
		grid.addAll(displayedNodes);

		order = SystemGlobals.MainFrame.Doc.getMatrixSort();

		Collections.sort(grid, new NodeComparator(order));

		int maxWidth = getMaxCellWidth(displayedNodes);
		int maxHeight = getMaxCellHeight(displayedNodes);

		Dimension dim = SystemGlobals.MainFrame.Doc.getGraphVisualSettings().getCanvasSize();

		int nodesCount = displayedNodes.size();
		int gx = (int) ((dim.width / 2) - (Math.sqrt(nodesCount) * maxWidth * graph.NodeSpace / 2));
		int gy = (int) ((dim.height / 2) - (Math.sqrt(nodesCount) * maxHeight * graph.NodeSpace / 2));
		if (gx < 0)
			gx = 10;
		if (gy < 0)
			gy = 10;
		int prevEnt = -1;

		synchronized (graph.getEdges()){
			for (Edge e : graph.getEdges()){
				e.edgeStyle = Edge.ES_Line;
			}
		}

		int gxSz, gySz;
		int gridX, gridY;
		int gridPos;

		gridY = (int) Math.sqrt(grid.size());
		if (gridY != 0)
			gridX = (grid.size() / gridY) + 1;
		else
			gridX = 0;

		gxSz = (int) ((maxWidth + 10) * graph.NodeSpace);
		gySz = (int) ((maxHeight + 10) * graph.NodeSpace);

		boolean ZigZag = false;
		synchronized (graph){
			gridPos = 0;
			for (Node n : grid){
				if (prevEnt != n.Obj.getEntity().ID){
					prevEnt = n.Obj.getEntity().ID;
					GridLayouData next = entityPositions.get(prevEnt);
					if (next != null && next.manuallyAdjusted){
						gx = next.pt.x;
						gy = next.pt.y;
					} else{
						if (gridPos % gridX != 0)
							gy = gy + (int) (gridPos / gridX + 1) * gySz;
						else
							gy = gy + (int) (gridPos / gridX) * gySz;

						entityPositions.put(prevEnt, new GridLayouData(gx, gy, false));
					}

					gridPos = 0;
					ZigZag = false;
				}

				n.setX(gx + gridPos % gridX * gxSz);
				n.setY(gy + gridPos / gridX * gySz);

				if (gxSz < n.labelWidth){
					n.ZigZag = ZigZag;
					ZigZag = !ZigZag;
				} else{
					n.ZigZag = false;
					ZigZag = true;
				}

				gridPos++;
			}
		}

		return true;
	}

	@Override
	public void editSettings(){
		// TODO Auto-generated method stub

	}

	@Override
	public void fromXML(NanoXML nextX){
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
	public boolean needLayout(GraphCollection graph){
		return true;
	}

	@Override
	public void showPropertyDialog(JFrame parent){
		// TODO Auto-generated method stub

	}

	@Override
	public String toXML(){
		return null;
	}

	@Override
	public void moveNode(Node n, boolean ctrlPressed, double dx, double dy){
		if (!ctrlPressed)
			super.moveNode(n, ctrlPressed, dx, dy);
		else{
			GridLayouData dt = entityPositions.get(n.Obj.getEntity().ID);
			if (dt != null){
				dt.pt.x += dx;
				dt.pt.y += dy;
				dt.manuallyAdjusted = true;

				entityPositions.put(n.Obj.getEntity().ID, dt);
			}
		}
	}

	@Override
	public boolean isOptionEnabled(Node n, int index){
		return false;
	}

	@Override
	public String getName(){
		return NAME;
	}
}
