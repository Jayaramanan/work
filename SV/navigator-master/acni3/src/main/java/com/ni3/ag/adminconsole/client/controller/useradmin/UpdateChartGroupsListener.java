/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminLeftPanel;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;


public class UpdateChartGroupsListener extends ProgressActionListener{

	private UserAdminController controller;

	public UpdateChartGroupsListener(UserAdminController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		UserAdminView view = controller.getView();
		UserAdminLeftPanel leftPanel = view.getLeftPanel();
		TreePath selectedPath = leftPanel.getSelectionTreePath();

		if (!save()){
			return;
		}

		controller.reloadData();
		view.resetEditedFields();

		AbstractTreeModel treeModel = leftPanel.getTreeModel();
		TreePath newPath = new TreeModelSupport().findPathByNodes(selectedPath.getPath(), treeModel);

		if (newPath != null){
			leftPanel.setSelectionTreePath(newPath);
		}
	}

	public boolean save(){
		UserAdminView view = controller.getView();
		UserAdminModel model = controller.getModel();

		DatabaseInstance dbInstance = controller.getModel().getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return true;
		}

		view.getChartPanel().stopCellEditing();
		view.clearErrors();

		List<Schema> schemas = model.getSchemas();
		if (schemas == null){
			return true;
		}

		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();

		service.updateSchemas(schemas);

		return true;
	}
}
