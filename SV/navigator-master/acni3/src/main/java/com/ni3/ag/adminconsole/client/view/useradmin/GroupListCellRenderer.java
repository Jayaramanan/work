/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.domain.Group;

public class GroupListCellRenderer extends DefaultListCellRenderer{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){

		String label = (value != null) ? ((Group) value).getName() : null;

		return super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
	}
}
