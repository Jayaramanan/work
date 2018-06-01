/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.useradmin.ThickClientPanel;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;


public class RefreshDatasourceActionListener extends ProgressActionListener{
	public RefreshDatasourceActionListener(UserAdminController controller){
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
		ThickClientPanel tClientPanel = view.getThickClientPanel();

		User selectedUser = tClientPanel.getSelectedUser();
		TreePath path = view.getLeftPanel().getSelectionTreePath();

		controller.getView().clearErrors();
		controller.reloadData();

		AbstractTreeModel treeModel = view.getLeftPanel().getTreeModel();
		TreePath newPath = new TreeModelSupport().findPathByNodes(path.getPath(), treeModel);

		if (newPath != null){
			view.getLeftPanel().setSelectionTreePath(newPath);
			view.getThickClientPanel().setActiveTableRow(selectedUser);
		}
		view.resetEditedFields();
	}
}
