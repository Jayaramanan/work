/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.format;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.appconf.FormatAttributesView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.FormatAttributesModel;

public class AttributeTableSelectionListener implements ListSelectionListener{
	private static Logger log = Logger.getLogger(AttributeTableSelectionListener.class);
	private FormatAttributesController controller;

	public AttributeTableSelectionListener(FormatAttributesController ctrl){
		controller = ctrl;
	}

	public void valueChanged(ListSelectionEvent e){
		if (e.getValueIsAdjusting())
			return;
		FormatAttributesView view = (FormatAttributesView) controller.getView();
		FormatAttributesModel model = (FormatAttributesModel) controller.getModel();

		int index = view.getSelectedRowIndex();
		log.debug("AttributeTableSelectionListener.valueChanged index == " + index);

		if (index == -1){
			model.setCurrentAttribute(null);
		} else{
			ObjectAttribute attr = view.getSelectedAttribute();
			model.setCurrentAttribute(attr);
		}

		view.clearErrors();

	}

	public boolean canSwitch(){
		return controller.canSwitch(true);
	}

}
