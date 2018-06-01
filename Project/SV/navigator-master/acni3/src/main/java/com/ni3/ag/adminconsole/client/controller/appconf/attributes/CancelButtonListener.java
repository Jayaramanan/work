/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.attributes;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.appconf.AttributeEditView;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.model.impl.AttributeEditModel;
import com.ni3.ag.adminconsole.shared.service.def.AttributeEditService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;


public class CancelButtonListener extends ProgressActionListener{

	public CancelButtonListener(AbstractController controller){
		super(controller);
	}

	public void performAction(ActionEvent e){
		AttributeEditController controller = (AttributeEditController) getController();
		AttributeEditModel model = (AttributeEditModel) controller.getModel();
		ObjectDefinition current = model.getCurrentObjectDefinition();
		if (current == null)
			return;

		AttributeEditView view = controller.getView();
		view.stopCellEditing();
		view.clearErrors();

		ObjectAttribute selectedAttribute = view.getSelectedAttribute();
		TreePath treeSelection = view.getObjectTree().getSelectionPath();

		try{
			AttributeEditService schemaAdminService = ACSpringFactory.getInstance().getAttributeEditService();
			ObjectDefinition od = schemaAdminService.reloadObjectDefinition(current.getId());
			current.setObjectAttributes(od.getObjectAttributes());
			controller.updateTable();
			controller.getView().clearErrors();

			if (treeSelection != null){
				TreeModelSupport treeSupport = new TreeModelSupport();
				TreePath found = treeSupport.findPathByNodes(treeSelection.getPath(), view.getObjectTree().getModel());
				if (found != null){
					view.getObjectTree().setSelectionPath(found);
					view.setActiveTableRow(selectedAttribute);
				}
			}
			view.resetEditedFields();
		} catch (ACException ex){
			controller.getView().renderErrors(new ServerErrorContainerWrapper(ex));
		}
	}
}
