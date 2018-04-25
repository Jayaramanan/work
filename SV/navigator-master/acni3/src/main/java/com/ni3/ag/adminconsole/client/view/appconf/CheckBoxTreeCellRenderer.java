/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.util.SettingsUtil;

public class CheckBoxTreeCellRenderer extends ACTreeCellRenderer{
	private static final Logger log = Logger.getLogger(CheckBoxTreeCellRenderer.class);
	private static final long serialVersionUID = 1L;

	private final JCheckBox nodeRenderer = new JCheckBox();

	private Color selectionForeground, selectionBackground, textForeground, textBackground;

	private boolean colorsSet = false;

	protected JCheckBox getNodeRenderer(){
		return nodeRenderer;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
	        boolean leaf, int row, boolean hasFocus){
		Component renderer = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		if (!colorsSet){
			selectionForeground = UIManager.getColor("Tree.selectionForeground");
			selectionBackground = UIManager.getColor("Tree.selectionBackground");
			textForeground = tree.getForeground();
			textBackground = tree.getBackground();
			colorsSet = true;
		}

		if ((value != null) && (value instanceof Setting)){
			if (selected){
				nodeRenderer.setForeground(selectionForeground);
				nodeRenderer.setBackground(selectionBackground);
			} else{
				nodeRenderer.setForeground(textForeground);
				nodeRenderer.setBackground(textBackground);
			}

			nodeRenderer.setEnabled(tree.isEnabled());
			nodeRenderer.setFocusPainted(false);
			Setting as = (Setting) value;
			nodeRenderer.setText(getRenderTextForPropertyName(as.getProp()));
			nodeRenderer.setSelected("1".equals(as.getValue()) || "true".equalsIgnoreCase(as.getValue()));

			renderer = nodeRenderer;
		}
		return renderer;
	}

	private String getRenderTextForPropertyName(String prop){
		TextID id = SettingsUtil.getLabelIdByName(prop);
		if (id == null){
			log.warn("cannot resolve TextID for property with name `" + prop + "`");
			return prop;
		}
		return Translation.get(id);
	}
}
