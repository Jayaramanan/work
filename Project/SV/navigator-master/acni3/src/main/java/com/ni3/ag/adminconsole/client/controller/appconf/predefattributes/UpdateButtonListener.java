/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeEditView;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;

public class UpdateButtonListener extends ProgressActionListener{

	private PredefinedAttributeEditController controller;

	public UpdateButtonListener(PredefinedAttributeEditController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		PredefinedAttributeEditModel model = controller.getModel();
		PredefinedAttributeEditView view = controller.getView();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		TreePath selectedPath = view.getSchemaTree().getSelectionPath();

		ObjectAttribute selectedAttribute = model.getCurrentAttribute();
		if (selectedAttribute == null)
			return;
		PredefinedAttribute selectedPredefined = null;

		int pIndex = view.getSelectedPredefinedAttributeModelIndex();

		if (pIndex >= 0){
			selectedPredefined = selectedAttribute.getPredefinedAttributes().get(pIndex);
		}

		if (!controller.submitCurrentObjectAttribute()){
			return;
		}

		controller.reloadData();

		if (selectedPath != null){
			TreeModelSupport treeSupport = new TreeModelSupport();
			TreePath found = treeSupport.findPathByNodes(selectedPath.getPath(), view.getSchemaTree().getModel());
			view.getSchemaTree().setSelectionPath(found);
		}

		if (selectedAttribute != null){
			view.setActiveTableRow(selectedAttribute);
			if (selectedPredefined != null){
				view.setActiveTableRow(selectedPredefined);
			}
		}

		view.resetEditedFields();
	}

}
