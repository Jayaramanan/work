/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;
import com.ni3.ag.adminconsole.validation.rules.GroupDeleteValidationRule;


public class DeleteGroupButtonListener extends ProgressActionListener{
	private UserAdminController controller;

	public DeleteGroupButtonListener(UserAdminController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		UserAdminView view = controller.getView();
		view.clearErrors();

		Group currentGroup = controller.getModel().getCurrentGroup();
		String currentPanel = controller.getModel().getCurrentPanel();
		if (currentGroup == null || currentGroup == controller.getModel().getUnassignedGroup() || currentPanel != null){
			return;
		}
		int result = ACOptionPane.showConfirmDialog(view, Translation.get(TextID.ConfirmDeleteGroup),
		        Translation.get(TextID.DeleteGroup));
		if (result != ACOptionPane.YES_OPTION)
			return;

		GroupDeleteValidationRule rule = (GroupDeleteValidationRule) ACSpringFactory.getInstance().getBean(
		        "groupDeleteValidationRule");
		if (rule.performCheck(controller.getModel())){
			controller.getView().renderErrors(rule.getErrorEntries());
			return;
		}

		Object[] path = getNewSelection(currentGroup, view.getLeftPanel().getSelectionTreePath());

		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();
		service.deleteGroup(currentGroup);

		controller.reloadData();

		AbstractTreeModel treeModel = view.getLeftPanel().getTreeModel();
		TreePath newPath = new TreeModelSupport().findPathByNodes(path, treeModel);
		view.getLeftPanel().setSelectionTreePath(newPath);
	}

	private Object[] getNewSelection(Group current, TreePath oldPath){
		UserAdminModel model = (UserAdminModel) controller.getModel();
		Object[] path = oldPath.getPath();
		Group diffGroup = null;
		List<Group> groups = model.getGroups();
		for (Group group : groups){
			if (!group.equals(current)){
				diffGroup = group;
				break;
			}
		}
		if (diffGroup != null){
			path[path.length - 1] = diffGroup;
		} else{
			path = Arrays.copyOf(path, path.length - 1);
		}
		return path;
	}

}
