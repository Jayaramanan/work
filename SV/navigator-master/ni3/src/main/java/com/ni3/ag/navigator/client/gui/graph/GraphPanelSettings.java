/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph;

import java.awt.*;
import java.awt.geom.AffineTransform;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class GraphPanelSettings{
	private int OrgX;
	private int OrgY;
	private double zoomTo;

	private double ZoomfExtent;
	private Point firstPoint = new Point();
	private Point secondPoint = new Point();
	private AffineTransform transform;
	private volatile Dimension canvasSize;
	private boolean showContractedEdgeCounter = true;

	public GraphPanelSettings(){
		reset();
	}

	public GraphPanelSettings(final GraphPanelSettings set){
		OrgX = set.OrgX;
		OrgY = set.OrgY;
		zoomTo = set.zoomTo;
		canvasSize = new Dimension(set.canvasSize);
		ZoomfExtent = set.ZoomfExtent;
		firstPoint = new Point(set.firstPoint);
		secondPoint = new Point(set.secondPoint);
		if (set.transform != null){
			transform = new AffineTransform(set.transform);
		} else{
			transform = new AffineTransform();
		}
		showContractedEdgeCounter = set.showContractedEdgeCounter;
	}

	@Override
	public GraphPanelSettings clone(){
		return new GraphPanelSettings(this);
	}

	public void fromXML(final NanoXML xml){
		NanoXMLAttribute attr;
		while ((attr = xml.Tag.getNextAttribute()) != null){
			if ("OrgX".equals(attr.Name)){
				OrgX = attr.getIntegerValue();
			} else if ("OrgY".equals(attr.Name)){
				OrgY = attr.getIntegerValue();
			} else if ("ZoomTo".equals(attr.Name)){
				zoomTo = attr.getDoubleValue();
			} else if ("Height".equals(attr.Name)){
				canvasSize.height = attr.getIntegerValue();
			} else if ("Width".equals(attr.Name)){
				canvasSize.width = attr.getIntegerValue();
			} else if ("ZoomExtent".equals(attr.Name)){
				ZoomfExtent = attr.getDoubleValue();
			} else if ("Alpha".equals(attr.Name)){
			} else if ("Npt1".equals(attr.Name)){
				firstPoint = attr.getPointValue();
			} else if ("Npt2".equals(attr.Name)){
				secondPoint = attr.getPointValue();
			} else if ("AffineTransformation".equals(attr.Name)){
				final StringTokenizerEx tok = new StringTokenizerEx(attr.Value, ",", false);

				transform = new AffineTransform();
				transform.translate(Double.valueOf(tok.nextToken()), Double.valueOf(tok.nextToken()));
				transform.scale(Double.valueOf(tok.nextToken()), Double.valueOf(tok.nextToken()));
			} else if ("showContractedEdgeCounter".equals(attr.Name)){
				String value = attr.getValue();
				showContractedEdgeCounter = "true".equals(value);
				SystemGlobals.MainFrame.Doc.dispatchEvent(Ni3ItemListener.MSG_ShowContractedEdgeCounterChanged,
						Ni3ItemListener.SRC_Doc, null, showContractedEdgeCounter);
			}
		}
	}

	public void reset(){
		OrgX = OrgY = 0;
		ZoomfExtent = zoomTo = 1.0;
		canvasSize = new Dimension();
		transform = new AffineTransform();
	}

	@Override
	public String toString(){
		return toXML();
	}

	public String toXML(){
		final StringBuilder ret = new StringBuilder();

		ret.append("<GraphPanelSettings ");
		ret.append("OrgX='").append(OrgX);
		ret.append("' OrgY='").append(OrgY);
		ret.append("' ZoomTo='").append(zoomTo);
		ret.append("' Height='").append(canvasSize.height);
		ret.append("' Width='").append(canvasSize.width);
		ret.append("' ZoomExtent='").append(ZoomfExtent);
		ret.append("' Alpha='").append(0.75f);
		ret.append("' showContractedEdgeCounter='").append(showContractedEdgeCounter);
		ret.append("' Npt1='").append(firstPoint.x).append(",").append(firstPoint.y);
		ret.append("' Npt2='").append(secondPoint.x).append(",").append(secondPoint.y).append("' ");

		if (transform == null){
			transform = new AffineTransform();
		}

		ret.append("AffineTransformation='");

		ret.append(transform.getTranslateX()).append(",");
		ret.append(transform.getTranslateY()).append(",");
		ret.append(transform.getScaleX()).append(",");
		ret.append(transform.getScaleY()).append("'/>\n");

		return ret.toString();
	}

	public boolean isZeroSize(){
		return canvasSize.width == 0 || canvasSize.height == 0;
	}

	public void setZoomTo(double v){
		zoomTo = v;
	}

	public double getZoomTo(){
		return zoomTo;
	}

	public AffineTransform getTransform(){
		return transform;
	}

	public void setCanvasSize(Dimension canvasSize){
		this.canvasSize = canvasSize;
	}

	public Dimension getCanvasSize(){
		return canvasSize;
	}

	public void setTransform(AffineTransform transform){
		this.transform = transform;
	}

	public void setFirstPoint(Point firstPoint){
		this.firstPoint = firstPoint;
	}

	public void setSecondPoint(Point secondPoint){
		this.secondPoint = secondPoint;
	}

	public Point getFirstPoint(){
		return firstPoint;
	}

	public Point getSecondPoint(){
		return secondPoint;
	}

	public Point getRandomPoint(){
		final double transformedWidth = getTransformedWidth();
		final double transformedHeight = getTransformedHeight();

		final double a = Math.random() - 0.5;
		final double b = Math.random() - 0.5;
		return new Point((int) (firstPoint.x + transformedWidth / 2 + a * transformedWidth), (int) (firstPoint.y
				+ transformedHeight / 2 + b * transformedHeight));
	}

	public double getTransformedWidth(){
		return secondPoint.x - firstPoint.x;
	}

	public double getTransformedHeight(){
		return secondPoint.y - firstPoint.y;
	}

	public Point getCenterPoint(){
		Dimension size = new Dimension(secondPoint.x - firstPoint.x, secondPoint.y - firstPoint.y);
		return new Point(firstPoint.x + size.width / 2, firstPoint.y + size.height / 2);
	}

	public void setShowContractedEdgeCounter(boolean showContractedEdgeCounter){
		this.showContractedEdgeCounter = showContractedEdgeCounter;
		SystemGlobals.MainFrame.Doc.dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_Graph, null, null);
	}

	public boolean isShowContractedEdgeCounter(){
		return showContractedEdgeCounter;
	}
}
