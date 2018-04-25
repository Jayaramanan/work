/** 
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.view.useractivity;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivityType;

@SuppressWarnings("serial")
public class FilterComboRenderer extends DefaultListCellRenderer{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		Object toShow = null;
		if (value == null)
			toShow = " ";
		else if (value instanceof UserActivityType)
			toShow = ((UserActivityType) value).toString();
		else if (value instanceof User){
			User u = (User) value;
			toShow = u.getFirstName() + " " + u.getLastName() + " ( " + u.getUserName() + " )";
		}
		return super.getListCellRendererComponent(list, toShow, index, isSelected, cellHasFocus);
	}

}
