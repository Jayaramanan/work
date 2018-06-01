/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.client.view.useradmin.privileges.PrivilegesPanel;
import com.ni3.ag.adminconsole.domain.Group;

public class PrivilegesTreeExpansionListener extends ProgressActionListener{

	private final static Logger log = Logger.getLogger(GrantAllPrivilegesActionListener.class);

	public PrivilegesTreeExpansionListener(UserAdminController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		UserAdminController controller = (UserAdminController) getController();
		UserAdminView view = controller.getView();
		view.getUserPanel().stopCellEditing();
		view.clearErrors();

		Group group = controller.getModel().getCurrentGroup();
		if (group == null){
			return;
		}

		PrivilegesPanel privilegesPanel = view.getPrivilegesPanel();
		JTree tree = privilegesPanel.getTree();
		TreePath selectionPath = tree.getSelectionPath();
		if (selectionPath == null){
			selectionPath = new TreePath(tree.getModel().getRoot());
		}

		expand(tree, selectionPath);
		log.debug("Expanding tree from path:  " + selectionPath);

		privilegesPanel.refreshTables();
	}

	private void expand(JTree tree, TreePath parentPath){
		tree.expandPath(parentPath);
		TreeModel model = tree.getModel();

		Object startNode = parentPath.getLastPathComponent();
		int childCount = model.getChildCount(startNode);
		for (int i = 0; i < childCount; i++){
			Object node = model.getChild(startNode, i);
			if (!model.isLeaf(node)){
				TreePath path = parentPath.pathByAddingChild(node);
				expand(tree, path);
			}
		}
	}

}