/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import org.apache.log4j.Logger;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.VariableCompletion;

public class AutoCompleteListCellRenderer extends DefaultListCellRenderer{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(AutoCompleteListCellRenderer.class);

	private static Color altBG = new Color(0xCC, 0xCC, 0xCC);
	private Font font;
	private boolean selected;
	private Color realBG;
	private Rectangle paintTextR;
	private static final String PREFIX = "<html><nobr>";

	public AutoCompleteListCellRenderer(Font font){
		paintTextR = new Rectangle();
		this.font = font;
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean hasFocus){

		super.getListCellRendererComponent(list, value, index, selected, hasFocus);
		if (font != null){
			setFont(font); // Overrides super's setFont(list.getFont()).
		}
		this.selected = selected;
		this.realBG = altBG != null && (index & 1) == 0 ? altBG : list.getBackground();

		if (value instanceof VariableCompletion){
			VariableCompletion vc = (VariableCompletion) value;
			prepareForVariableCompletion(list, vc, index, selected, hasFocus);
		} else{
			Completion c = (Completion) value;
			prepareForOtherCompletion(list, c, index, selected, hasFocus);
		}

		if (!selected && (index & 1) == 0 && altBG != null){
			setBackground(altBG);
		}
		return this;
	}

	protected void paintComponent(Graphics g){
		g.setColor(realBG);
		if (selected){ // The icon area is never in the "selection"
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		String text = getText();
		if (text != null){
			paintTextR.setBounds(0, 0, getWidth(), getHeight());
			paintTextR.x += 3; // Force a slight margin
			int space = paintTextR.height - g.getFontMetrics().getHeight();
			View v = (View) getClientProperty(BasicHTML.propertyKey);
			if (v != null){
				// HTML rendering doesn't auto-center vertically, for some
				// reason
				paintTextR.y += space / 2;
				paintTextR.height -= space;
				v.paint(g, paintTextR);
			} else{
				int textX = paintTextR.x;
				int textY = paintTextR.y;
				log.debug("font metrics > ascent: " + g.getFontMetrics().getAscent());
				g.drawString(text, textX, textY);
			}
		}

	}

	protected void prepareForOtherCompletion(JList list, Completion c, int index, boolean selected, boolean hasFocus){
		StringBuffer sb = new StringBuffer(PREFIX);
		sb.append(c.getInputText());
		setText(sb.toString());
	}

	protected void prepareForVariableCompletion(JList list, VariableCompletion vc, int index, boolean selected,
	        boolean hasFocus){

		StringBuffer sb = new StringBuffer(PREFIX);
		sb.append(vc.getName());

		if (vc.getType() != null){
			sb.append(" : ");
			if (!selected){
				sb.append("<font color='#0000FF'><b>");
			}
			sb.append(vc.getType());
			if (!selected){
				sb.append("</b></font>");
			}
		}
		sb.append("</html>");
		setText(sb.toString());

	}
}
