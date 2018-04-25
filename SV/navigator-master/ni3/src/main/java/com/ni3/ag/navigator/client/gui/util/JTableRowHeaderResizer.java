/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.MouseInputAdapter;

@SuppressWarnings("serial")
public class JTableRowHeaderResizer extends MouseInputAdapter implements Serializable, ContainerListener{
	private JScrollPane pane;
	private JViewport viewport;
	private JTable rowHeader;
	private Component corner;

	private boolean enabled;

	public JTableRowHeaderResizer(JScrollPane pane){
		this.pane = pane;

		this.pane.addContainerListener(this);
	}

	public void setEnabled(boolean what){
		if (enabled == what)
			return;

		enabled = what;

		if (enabled)
			addListeners();
		else
			removeListeners();
	}

	protected void addListeners(){
		if (corner != null){
			corner.addMouseListener(this);
			corner.addMouseMotionListener(this);
		}
	}

	protected void removeListeners(){
		if (corner != null){
			corner.removeMouseListener(this);
			corner.removeMouseMotionListener(this);
		}
	}

	protected void lookupComponents(){
		this.viewport = pane.getRowHeader();
		if (viewport == null)
			this.rowHeader = null;
		else
			this.rowHeader = (JTable) viewport.getView();
		this.corner = pane.getCorner(JScrollPane.UPPER_LEFT_CORNER);
	}

	public void componentAdded(ContainerEvent e){
		componentRemoved(e);
	}

	public void componentRemoved(ContainerEvent e){
		if (enabled)
			removeListeners();

		lookupComponents();

		if (enabled)
			addListeners();
	}

	private boolean active;

	private int startX, startWidth, startColumnWidth;

	private Dimension size;

	private static final int PIXELS = 10;

	public void mouseExited(MouseEvent e){
	}

	public void mouseEntered(MouseEvent e){
		mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e){
	}

	public void mousePressed(MouseEvent e){
		startX = e.getX();

		startWidth = rowHeader.getWidth();

		startColumnWidth = rowHeader.getColumnModel().getColumn(rowHeader.getColumnModel().getColumnCount() - 1).getWidth();

		if (startWidth - startX > PIXELS)
			return;

		active = true;
	}

	public void mouseReleased(MouseEvent e){
		active = false;
	}

	public void mouseDragged(MouseEvent e){
		if (!active)
			return;

		size = viewport.getPreferredSize();

		size.width = startWidth + e.getX() - startX;

		viewport.setPreferredSize(size);

		// This isn't too clean, it assumes the width bubbles up to
		rowHeader.getColumnModel().getColumn(rowHeader.getColumnModel().getColumnCount() - 1)
		        .setPreferredWidth(startColumnWidth + e.getX() - startX);

	}
}
