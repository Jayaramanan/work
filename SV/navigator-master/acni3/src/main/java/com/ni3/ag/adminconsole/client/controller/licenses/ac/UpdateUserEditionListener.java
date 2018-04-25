/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses.ac;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.licenses.ac.AdminConsoleLicenseView;
import com.ni3.ag.adminconsole.domain.User;


public class UpdateUserEditionListener extends ProgressActionListener{

	public UpdateUserEditionListener(AdminConsoleLicenseController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		AdminConsoleLicenseController controller = (AdminConsoleLicenseController) getController();
		AdminConsoleLicenseView view = controller.getView();
		User user = view.getSelectedUser();
		TreePath treeSelection = view.getTree().getSelectionPath();

		if (!controller.applyChanges()){
			return;
		}

		view.resetEditedFields();

		controller.loadModel();
		controller.refreshTableModel(false);

		if (treeSelection != null){
			TreePath found = new TreeModelSupport().findPathByNodes(treeSelection.getPath(), view.getTreeModel());
			if (found != null){
				view.getTree().setSelectionPath(found);
				view.setActiveTableRow(user);
			}
		}

	}

}