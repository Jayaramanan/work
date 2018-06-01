/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ColorTableCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.StrongTableCellRenderer;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class HaloColorTableCellRenderer extends JLabel implements StrongTableCellRenderer{

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ColorTableCellRenderer.class);

	public HaloColorTableCellRenderer(){
		setOpaque(true);
		setBorder(BorderFactory.createMatteBorder(2, 0, 2, 5, getBackground()));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){

		if (value != null && value instanceof String){
			String str = (String) value;
			if (str.equalsIgnoreCase("A")){
				setBackground(table.getBackground());
				setText(Translation.get(TextID.HaloAutomatic));
			} else if (str.equalsIgnoreCase("R")){
				setBackground(table.getBackground());
				setText(Translation.get(TextID.HaloRandom));
			} else{
				String colorStr = (String) value;
				java.awt.Color c = null;
				if (colorStr != null){
					try{
						c = java.awt.Color.decode(colorStr);
					} catch (NumberFormatException ex){
						log.warn("Not valid hex color: " + colorStr);
					}
				}
				setBackground(c != null ? c : table.getBackground());
				setText("");
			}
		} else{
			setBackground(table.getBackground());
			setText("");
		}

		return this;
	}

}
