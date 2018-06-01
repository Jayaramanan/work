/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.domain.Module;

public class ModuleComboCellRenderer extends DefaultListCellRenderer{
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		Module m = (Module) value;
		String sVal = "";
		if (m != null)
			sVal = m.getName() + " " + m.getVersion();
		Component c = super.getListCellRendererComponent(list, sVal, index, isSelected, cellHasFocus);
		return c;
	}
}
