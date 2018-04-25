/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.useradmin.ScopePanel;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupScope;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;


public class UpdateGroupScopeActionListener extends ProgressActionListener{
	private UserAdminController controller;
	Logger log = Logger.getLogger(UpdateGroupScopeActionListener.class);

	public UpdateGroupScopeActionListener(UserAdminController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		log.debug("action performed");

		UserAdminView view = controller.getView();
		view.clearErrors();

		TreePath path = view.getLeftPanel().getSelectionTreePath();
		if (!save()){
			return;
		}

		controller.reloadData();
		view.resetEditedFields();

		AbstractTreeModel treeModel = view.getLeftPanel().getTreeModel();
		TreePath newPath = new TreeModelSupport().findPathByNodes(path.getPath(), treeModel);

		if (newPath != null){
			view.getLeftPanel().setSelectionTreePath(newPath);
		}
	}

	protected String correctStringValue(Object o){
		if (o instanceof String){
			String s = (String) o;
			s = s.trim();
			if (s.length() == 0)
				return null;
			else
				return s;
		}
		return null;
	}

	public boolean save(){

		UserAdminView view = controller.getView();
		ScopePanel scopePanel = view.getScopePanel();
		Group currentGroup = controller.getModel().getCurrentGroup();
		if (currentGroup == null){
			return true;
		}

		currentGroup.setNodeScope(scopePanel.isUseNodeScope() ? 'S' : 'A');
		currentGroup.setEdgeScope(scopePanel.isUseEdgeScope() ? 'S' : 'A');

		if (currentGroup.getGroupScope() == null)
			currentGroup.setGroupScope(new GroupScope());
		GroupScope groupScope = currentGroup.getGroupScope();

		String nodeScope = correctStringValue(scopePanel.getNodesScope());
		groupScope.setNodeScope(nodeScope);
		String edgeScope = correctStringValue(scopePanel.getEdgesScope());
		groupScope.setEdgeScope(edgeScope);
		groupScope.setGroup(currentGroup.getId());

		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();
		service.updateGroup(currentGroup);
		SchemaAdminService s = ACSpringFactory.getInstance().getSchemaAdminService();
		s.setInvalidationRequired(DataGroup.Users, true);
		MainPanel2.setInvalidationNeeded(TextID.Users, true);

		return true;
	}
}
