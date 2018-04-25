/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter.impl;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;

import java.awt.*;
import java.util.List;

public class ObjectPainter{

	private final int labelWrapLength;

	public ObjectPainter(){
		labelWrapLength = UserSettings.getIntAppletProperty("GraphLabelWrapLength", 0);
	}

	public int getLabelWrapLength(){
		return labelWrapLength;
	}

	protected void showLabel(Graphics2D g, GraphObject obj, Point position){
		String label = obj.lbl;
		if (label == null){
			obj.refreshLabel();
			label = obj.lbl;
		}
		if (label != null && !label.isEmpty()){
			g.setColor(Color.black);
			final FontMetrics fm = g.getFontMetrics();
			if (labelWrapLength > 0 && label.length() > labelWrapLength){
				List<String> lines = obj.getSplittedLabel(labelWrapLength);
				for (int ln = 0; ln < lines.size(); ln++){
					int xw = fm.stringWidth(lines.get(ln));
					g.drawString(lines.get(ln), position.x - (xw / 2), position.y + (ln * fm.getAscent()));
				}
			} else{
				int width = fm.stringWidth(label);
				g.drawString(label, position.x - (width / 2), position.y);
			}
		}
	}

	public double drawHalo(Graphics2D g, double x, double y, double haloR, double delta, Color[] halos){
		if (delta == 0.0)
			delta = haloR * 0.1;

		final Color oldColor = g.getColor();
		haloR += (int) (2 * delta * halos.length);
		final double maxRadius = haloR;
        for (Color halo : halos) {
            g.setColor(halo);
            g.fillOval((int) (x - haloR / 2), (int) (y - haloR / 2), (int) haloR, (int) haloR);
            haloR -= 2 * delta;
        }
		g.setColor(oldColor);

		return maxRadius;
	}
}
