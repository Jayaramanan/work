/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.swing.*;

import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.Ni3Panel;
import com.ni3.ag.navigator.client.gui.graph.painter.EdgePainter;
import com.ni3.ag.navigator.client.gui.graph.painter.NodePainter;
import com.ni3.ag.navigator.client.gui.graph.painter.impl.EdgePainterImpl;
import com.ni3.ag.navigator.client.gui.graph.painter.impl.NodePainterImpl;

@SuppressWarnings("serial")
public abstract class BasicGraphPanel extends Ni3Panel implements ComponentListener{

	public JToolBar toolbar;
	public static Boolean ShowRootNodeMenu;
	private NodePainter nodePainter;
	private EdgePainter edgePainter;

	protected GraphController graphController;

	JPopupMenu popup;

	int numMouseButtonsDown = 0;

	Node selectedNode, secondPick;
	Edge selectedEdge;

	public boolean inRubberBand;
	Rectangle rubberBandRectangle;
	boolean rubberBandSelect;

	public boolean edgeConnectionInProgress;
	public boolean copyToClipboard;

	protected boolean graphDirty;

	protected final MainPanel parent;

	int sourceX, sourceY;

	public BasicGraphPanel(final MainPanel parent){
		super(parent);
		this.parent = parent;
		this.graphController = new GraphController(parent);

		setGraphDirty(true);

		inRubberBand = false;
		edgeConnectionInProgress = false;
		rubberBandSelect = false;
		rubberBandRectangle = new Rectangle(0, 0, 0, 0);

		initSettings();

		// Enable tool tips.
		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(99999);

		selectedEdge = null;
		selectedNode = null;
		secondPick = null;

		addComponentListener(this);
	}

	@Override
	public void componentHidden(final ComponentEvent e){
	}

	@Override
	public void componentMoved(final ComponentEvent e){
	}

	@Override
	public void componentResized(final ComponentEvent e){
		forceRepaint();
	}

	@Override
	public void componentShown(final ComponentEvent e){
		forceRepaint();
	}

	public void copyToClipboard(){
		copyToClipboard = true;
	}

	/*
	 * For any kind of event assume that graph presentation has been changed
	 */
	@Override
	public void event(final int EventCode, final int SourceID, final Object source, final Object Param){
		super.event(EventCode, SourceID, source, Param);

		if (EventCode == MSG_NodeShowToolTip || EventCode == MSG_EdgeShowToolTip){
			return;
		}

		forceRepaint();
	}

	public void forceRepaint(){
		setGraphDirty(true);
		repaint();
	}

	public EdgePainter getEdgePainter(){
		return edgePainter;
	}

	public NodePainter getNodePainter(){
		return nodePainter;
	}

	private void initSettings(){
		final Color selectedColor = UserSettings.getColor("NODE_SELECTED_COLOR", Color.red);
		nodePainter = new NodePainterImpl(selectedColor);
		edgePainter = new EdgePainterImpl();
	}

	public boolean isGraphDirty(){
		return graphDirty;
	}

	public boolean isInRubber(){
		return inRubberBand || edgeConnectionInProgress;
	}

	@Override
	public void onGraphDirty(){
		forceRepaint();
	}

	@Override
	public void paint(final Graphics g2){
		renderView(g2, getSize(), false, null);
	}

	public int print(final Graphics g, final PageFormat pf, final int pageIndex, final Dimension d) throws PrinterException{
		if (pageIndex == 0){
			renderView(g, d, true, pf);
			return Printable.PAGE_EXISTS;
		} else{
			return Printable.NO_SUCH_PAGE;
		}

	}

	public abstract void renderView(Graphics g2, Dimension dim, boolean Print, PageFormat pf);

	public void setGraphDirty(final boolean graphDirty){
		this.graphDirty = graphDirty;
	}

	protected void showSiebelPopupMenu(final Point e){
		popup = new JPopupMenu();

		for (final JMenuItem i : parent.getMenuItems()){
			popup.add(i);
		}

		sourceX = e.x;
		sourceY = e.y;
		popup.show(this, e.x, e.y);
	}

	/*
	 * @author Dejan Stanojevic @param prevAt - Existing transformation,
	 * 
	 * @param X,Y - Coordinates of point(geo) that preserve same screen coordinates @param newZoom - new scale factor to
	 * be perform @return Newly Calculated AffineTransform
	 * 
	 * @version 1.0
	 */
	AffineTransform SmoothZoom(final AffineTransform prevAt, final double newZoom, final int x, final int y){
		final Point p = new Point(x, y);
		final Point pp = new Point();
		AffineTransform newAt;

		try{
			prevAt.inverseTransform(p, pp);

			final double mx = prevAt.getScaleX() * pp.x + prevAt.getTranslateX() - (newZoom * pp.x);
			final double my = prevAt.getScaleY() * pp.y + prevAt.getTranslateY() - (newZoom * pp.y);

			newAt = new AffineTransform(newZoom, 0.0, 0.0, newZoom, mx, my);
			newAt.inverseTransform(p, pp);

		} catch (final NoninvertibleTransformException e){
			e.printStackTrace();
			return prevAt;
		}

		return newAt;
	}

	@Override
	public void update(final Graphics g2){
		paint(g2);
	}

}
