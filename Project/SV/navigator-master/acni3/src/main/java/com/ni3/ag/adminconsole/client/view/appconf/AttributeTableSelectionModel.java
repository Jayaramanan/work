/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ni3.ag.adminconsole.client.controller.appconf.predefattributes.AttributeTableSelectionListener;

public class AttributeTableSelectionModel extends DefaultListSelectionModel{

	private static final long serialVersionUID = 1L;

	private int oldIndex0 = -1, oldIndex1 = -1;

	private AttributeTableSelectionListener listener;

	public void addListSelectionListener(ListSelectionListener listener){
		if (listener instanceof AttributeTableSelectionListener)
			this.listener = (AttributeTableSelectionListener) listener;
		super.addListSelectionListener(listener);
	}

	public void setSelectionInterval(int index0, int index1){
		if (oldIndex0 == index0 && oldIndex1 == index1)
			super.setSelectionInterval(index0, index1);
		if (listener != null){
			if (listener.canSwitch())
				super.setSelectionInterval(index0, index1);
		} else{
			super.setSelectionInterval(index0, index1);
		}
		oldIndex0 = index0;
		oldIndex1 = index1;
		listener.valueChanged(new ListSelectionEvent(this, index0, index1, false));
	}
}
