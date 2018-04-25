/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import static com.ni3.ag.adminconsole.client.view.Translation.get;
import static com.ni3.ag.adminconsole.shared.language.TextID.MsgEnterNameOfNewGroup;
import static com.ni3.ag.adminconsole.shared.language.TextID.NewGroup;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.common.StringValidator;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminLeftPanel;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;


public class AddGroupButtonListener extends ProgressActionListener{

	private UserAdminController controller;

	private ACValidationRule userAdminGroupNameRule;

	private final static Logger log = Logger.getLogger(AddGroupButtonListener.class);

	public AddGroupButtonListener(UserAdminController controller){
		super(controller);
		this.controller = controller;
		this.userAdminGroupNameRule = ((ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "userAdminGroupNameValidationRule"));
	}

	@Override
	public void performAction(ActionEvent e){
		log.debug("action performed");
		UserAdminView view = controller.getView();
		view.clearErrors();

		UserAdminModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		String groupName = ACOptionPane.showInputDialog(view, get(MsgEnterNameOfNewGroup), get(NewGroup));
		groupName = StringValidator.validate(groupName);
		if (groupName == null){
			return;
		}

		Group backup = model.getCurrentGroup();

		Group group = new Group();
		group.setName(groupName);
		model.setCurrentGroup(group);

		TreePath currentPath = view.getLeftPanel().getSelectionTreePath();

		ACSpringFactory factory = ACSpringFactory.getInstance();

		if (userAdminGroupNameRule.performCheck(model)){
			UserAdminService service = factory.getUserAdminService();
			Group newGroup = service.addGroup(group);
			controller.reloadData();
			restoreSelection(currentPath, newGroup);
		} else{
			view.renderErrors(userAdminGroupNameRule.getErrorEntries());
			model.setCurrentGroup(backup);
		}
	}

	private void restoreSelection(TreePath currentPath, Group newGroup){
		UserAdminLeftPanel leftPanel = controller.getView().getLeftPanel();
		AbstractTreeModel treeModel = leftPanel.getTreeModel();
		Object[] oldNodes = currentPath.getPath();
		Object[] newNodes = new Object[] { oldNodes[0], oldNodes[1], newGroup };

		TreePath newPath = new TreeModelSupport().findPathByNodes(newNodes, treeModel);
		leftPanel.setSelectionTreePath(newPath);
	}

}
