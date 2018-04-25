/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.gui.graph.Node;

@SuppressWarnings("serial")
public class MetaphorLegendFrame extends Ni3Dialog implements Ni3ItemListener{
	int cCol;
	final int WIDTH = 10;
	Image icon[] = new Image[100];
	int h[] = new int[100];
	String label[] = new String[100];
	String description[] = new String[100];
	MainPanel parent;
	MetaphorLegendPane pane;
	String ServerURL;

	MetaphorLegendFrame(MainPanel parent, String serverURL, String Caption, GraphCollection Subgraph){
		super();
		this.parent = parent;
		this.ServerURL = serverURL;

		Caption = null;

		parent.Doc.registerListener(this);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		pane = new MetaphorLegendPane(this);

		JScrollPane scroll = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scroll);

		// Enable tool tips.
		ToolTipManager.sharedInstance().registerComponent(pane);
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		Initialize(Caption, Subgraph);

	}

	public int getListenerType(){
		return Ni3ItemListener.SRC_MetaphorLegendFrame;
	}

	public class MetaphorLegendPane extends JComponent implements MouseMotionListener{
		MetaphorLegendFrame parent;

		public MetaphorLegendPane(MetaphorLegendFrame parent){
			this.parent = parent;
		}

		public void paint(Graphics g){
			Graphics2D g2 = (Graphics2D) g;

			Dimension d = getSize();

			g2.setColor(new Color(236, 233, 216));
			g2.fillRect(0, 0, d.width, d.height);

			int y = 20;
			int W, H;
			for (int n = 0; n < cCol; n++){
				W = icon[n].getWidth(null);
				H = icon[n].getHeight(null);

				g2.drawImage(icon[n], (int) (20), (int) (y - H / 2.0), W, H, null);

				g2.drawString(label[n], 100, y + 15);

				y += H + 5;
				h[n] = y;
			}
		}

		public String getToolTipText(MouseEvent e){
			for (int n = 0; n < cCol; n++)
				if (e.getY() < h[n])
					return description[n];

			return "";
		}

		public void mouseDragged(MouseEvent arg0){
		}

		public void mouseMoved(MouseEvent arg0){

		}
	}

	void Initialize(String Caption, GraphCollection Subgraph){
		if (Caption != null)
			setTitle(Caption);

		cCol = 0;
		boolean founded;

		if (Subgraph == null)
			return;

		for (Node n : Subgraph.getNodes()){
			founded = false;

			for (int i = 0; i < cCol; i++)
				if (n.Obj.getIcon() == icon[i]){
					founded = true;
					break;
				}

			if (!founded){
				icon[cCol] = n.Obj.getIcon();
				label[cCol] = cCol + ".";
				h[cCol] = icon[cCol].getHeight(null);
				if (cCol > 0)
					h[cCol] += h[cCol - 1];
				else
					h[cCol] += 20;
				cCol++;
			}
		}

		pane.setPreferredSize(new Dimension(WIDTH * 7 + 80, h[cCol - 1] + 50));

		this.setSize(WIDTH * 7 + 110, h[cCol - 1] + 90);
	}

	public void event(int EventCode, int SourceID, Object source, Object Param){
		switch (EventCode){
			case Ni3ItemListener.MSG_SubgraphChanged:{
				Initialize(null, (GraphCollection) Param);
			}
				break;

			case Ni3ItemListener.MSG_SchemaChanged:{
				Initialize(null, null);
			}
				break;
		}
	}
}
