/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

public abstract class BasicGraphPanel extends JPanel implements ComponentListener{
	private static final long serialVersionUID = 1327082146030939769L;
	private static final Logger log = Logger.getLogger(BasicGraphPanel.class);

	protected static boolean zoomedToArea;
	protected JToolBar toolbar;
	protected boolean graphDirty;

	protected boolean inRubberBand;
	protected Rectangle rubberBandRectangle;
	protected boolean rubberBandSelect;

	BasicGraphPanel(){
		graphDirty = true;
		zoomedToArea = false;

		inRubberBand = false;
		rubberBandSelect = false;
		rubberBandRectangle = new Rectangle(0, 0, 0, 0);
	}

	@Override
	public void update(Graphics g2){
		paint(g2);
	}

	@Override
	public void paint(Graphics g2){
		renderView(g2, getSize());
	}

	public abstract void renderView(Graphics g2, Dimension dim);

	public void forceRepaint(){
		graphDirty = true;
		repaint();
	}

	AffineTransform smoothZoom(AffineTransform prevAt, double newZoom, int x, int y){
		Point p = new Point(x, y);
		Point pp = new Point();
		AffineTransform newAt;

		try{
			prevAt.inverseTransform(p, pp);

			double mx = prevAt.getScaleX() * pp.x + prevAt.getTranslateX() - (newZoom * pp.x);
			double my = prevAt.getScaleY() * pp.y + prevAt.getTranslateY() - (newZoom * pp.y);

			newAt = new AffineTransform(newZoom, 0.0, 0.0, newZoom, mx, my);
			newAt.inverseTransform(p, pp);

		} catch (NoninvertibleTransformException e){
			log.error("cant perform smooth zoom", e);
			return prevAt;
		}

		return newAt;
	}

	@Override
	public void componentHidden(ComponentEvent e){
	}

	@Override
	public void componentMoved(ComponentEvent e){
	}

	@Override
	public void componentResized(ComponentEvent e){
	}

	@Override
	public void componentShown(ComponentEvent e){
		forceRepaint();
	}
}
