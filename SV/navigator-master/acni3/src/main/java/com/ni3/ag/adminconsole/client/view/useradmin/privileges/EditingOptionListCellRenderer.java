/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.privileges;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.EditingOption;

public class EditingOptionListCellRenderer extends DefaultListCellRenderer{
	private static final long serialVersionUID = 3362213004588488760L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		Object toShow = value;
		if (value instanceof EditingOption)
			toShow = Translation.get(((EditingOption) value).getLabelID());
		return super.getListCellRendererComponent(list, toShow, index, isSelected, cellHasFocus);
	}

}
