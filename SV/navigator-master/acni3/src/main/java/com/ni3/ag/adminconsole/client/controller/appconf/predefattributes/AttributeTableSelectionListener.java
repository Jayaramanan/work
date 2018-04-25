/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeEditView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;

public class AttributeTableSelectionListener implements ListSelectionListener{
	private static Logger log = Logger.getLogger(AttributeTableSelectionListener.class);
	private PredefinedAttributeEditController controller;

	public AttributeTableSelectionListener(PredefinedAttributeEditController ctrl){
		controller = ctrl;
	}

	public void valueChanged(ListSelectionEvent e){
		if (e.getValueIsAdjusting())
			return;
		int index = ((PredefinedAttributeEditView) controller.getView()).getSelectedAttributeModelIndex();
		log.debug("AttributeTableSelectionListener.valueChanged index == " + index);
		PredefinedAttributeEditView view = (PredefinedAttributeEditView) controller.getView();
		PredefinedAttributeEditModel model = (PredefinedAttributeEditModel) controller.getModel();

		if (index == -1){
			model.setCurrentAttribute(null);
			controller.reloadPredefinedTableModel();
			return;
		} else{
			ObjectAttribute attr = view.getSelectedAttribute();
			model.setCurrentAttribute(attr);
			view.setVisibility(attr.isFormulaAttribute(), attr.isPredefined());
			if (attr.isPredefined()){
				log.debug("Selected: " + attr);
				log.debug(attr.getObjectDefinition().getSchema().getName());
				log.debug("\t" + attr.getObjectDefinition().getName());
				log.debug("\t" + attr.getId() + "->" + attr.getLabel() + " | " + attr.getDescription());
				controller.getView().resetEditedFields();
				controller.reloadPredefinedTableModel();
			}
			if (attr.isFormulaAttribute()){
				view.setFormula(attr.getFormula());
			}

		}
		model.clearDeletedPredefinedAttributes();
		view.resetEditedFields();
		view.clearErrors();
	}

	public boolean canSwitch(){
		return controller.canSwitch(true);
	}
}
