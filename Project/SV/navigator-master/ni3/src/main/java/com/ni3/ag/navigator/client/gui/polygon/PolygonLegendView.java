/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.polygon;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.Ni3Document;

public class PolygonLegendView implements Ni3ItemListener{
	private static final Logger log = Logger.getLogger(PolygonLegendView.class);

	private Ni3Document doc;
	private PolygonLegendFrame polygonFrame;

	public PolygonLegendView(Ni3Document doc){
		this.doc = doc;
	}

	@Override
	public void event(int eventCode, int sourceID, Object source, Object param){
		switch (eventCode){
			case MSG_PolygonModelChanged:
				updatePolygonLegend(true);
				break;
			case MSG_SubgraphChanged:
			case MSG_ClearSubgraph:
			case MSG_FilterChanged:
			case MSG_ChartFilterChanged:
				updatePolygonLegend(false);
				break;
		}
	}

	private void updatePolygonLegend(boolean forceVisible){
		if (doc.hasPolygonNodes() || doc.hasPolylineNodes()){
			if (polygonFrame == null){
				polygonFrame = createFrame();
				polygonFrame.setVisible(true);
			}
			List<Node> polyNodes = getPolyNodes();
			polygonFrame.setData(polyNodes, doc.getPolygonModel().getPolyColors());
			if (forceVisible && !polygonFrame.isVisible()){
				polygonFrame.setVisible(true);
			}
		} else if (polygonFrame != null){
			polygonFrame.dispose();
			polygonFrame = null;
		}
	}

	private PolygonLegendFrame createFrame(){
		PolygonLegendFrame polygonFrame = new PolygonLegendFrame();
		polygonFrame.getTableModel().addTableModelListener(new TableModelListener(){
			@Override
			public void tableChanged(TableModelEvent e){
				if (e.getColumn() == PolygonNodeTableModel.COLOR_COLUMN_INDEX){
					log.debug("Polygon/polyline color changed");
					doc.firePolygonColorChanged();
				}
			}
		});
		return polygonFrame;
	}

	private List<Node> getPolyNodes(){
		List<Node> polyNodes = new ArrayList<Node>();
		for (Node node : doc.Subgraph.getDisplayedNodes()){
			if (doc.isPolygonNode(node.ID) || doc.isPolylineNode(node.ID)){
				polyNodes.add(node);
			}
		}
		return polyNodes;
	}

	@Override
	public int getListenerType(){
		return SRC_Other;
	}

}
