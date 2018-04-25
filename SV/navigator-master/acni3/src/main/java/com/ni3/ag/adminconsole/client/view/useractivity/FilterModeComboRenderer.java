/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useractivity;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

@SuppressWarnings("serial")
public class FilterModeComboRenderer extends DefaultListCellRenderer{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		Object toShow = " ";
		if (value instanceof TextID)
			toShow = Translation.get((TextID) value);
		return super.getListCellRendererComponent(list, toShow, index, isSelected, cellHasFocus);
	}

}
