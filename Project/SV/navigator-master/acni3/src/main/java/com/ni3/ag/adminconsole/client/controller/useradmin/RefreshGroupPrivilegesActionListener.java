/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class RefreshGroupPrivilegesActionListener extends ProgressActionListener{

	public RefreshGroupPrivilegesActionListener(UserAdminController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		UserAdminController controller = (UserAdminController) getController();
		UserAdminView view = controller.getView();

		DatabaseInstance dbInstance = controller.getModel().getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		TreePath selectedPath = view.getLeftPanel().getSelectionTreePath();
		int row = view.getPrivilegesPanel().getSelectedRow();
		int cell = view.getPrivilegesPanel().getSelectedColumn();

		view.clearErrors();
		controller.reloadData();

		AbstractTreeModel treeModel = view.getLeftPanel().getTreeModel();
		TreePath newPath = new TreeModelSupport().findPathByNodes(selectedPath.getPath(), treeModel);

		if (newPath != null){
			view.getLeftPanel().setSelectionTreePath(newPath);
		}
		if (row > -1 && cell > -1){
			view.getPrivilegesPanel().setActiveTableRow(row, cell);
		}

		view.resetEditedFields();
	}
}
