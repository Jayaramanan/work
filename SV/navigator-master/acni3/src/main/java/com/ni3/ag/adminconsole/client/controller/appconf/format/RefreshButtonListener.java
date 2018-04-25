/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.format;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.appconf.FormatAttributesView;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.FormatAttributesModel;

public class RefreshButtonListener extends ProgressActionListener{

	public RefreshButtonListener(FormatAttributesController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		FormatAttributesController controller = (FormatAttributesController) getController();
		FormatAttributesView view = (FormatAttributesView) controller.getView();
		FormatAttributesModel model = (FormatAttributesModel) controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		TreePath selectedPath = view.getTreeSelectionPath();
		ObjectAttribute selectedAttribute = model.getCurrentAttribute();

		if (selectedAttribute == null)
			return;

		view.stopCellEditing();
		view.clearErrors();
		controller.reloadData();

		if (selectedPath != null){
			TreeModelSupport treeSupport = new TreeModelSupport();
			TreePath found = treeSupport.findPathByNodes(selectedPath.getPath(), view.getSchemaTreeModel());
			view.setTreeSelectionPath(found);
		}
		if (selectedAttribute != null){
			view.setActiveTableRow(selectedAttribute);
		}
		view.resetEditedFields();
	}

}
