/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;

public class UpdateGroupPrivilegesActionListener extends ProgressActionListener{

	private UserAdminController controller;
	private Logger log = Logger.getLogger(UpdateGroupPrivilegesActionListener.class);

	public UpdateGroupPrivilegesActionListener(UserAdminController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		UserAdminView view = controller.getView();
		TreePath selectedPath = view.getLeftPanel().getSelectionTreePath();
		int row = view.getPrivilegesPanel().getSelectedRow();
		int cell = view.getPrivilegesPanel().getSelectedColumn();

		if (!save()){
			return;
		}

		controller.reloadData();

		AbstractTreeModel treeModel = view.getLeftPanel().getTreeModel();
		TreePath newPath = new TreeModelSupport().findPathByNodes(selectedPath.getPath(), treeModel);

		if (newPath != null){
			view.getLeftPanel().setSelectionTreePath(newPath);
		}
		if (row > -1 && cell > -1){
			view.getPrivilegesPanel().setActiveTableRow(row, cell);
		}
	}

	public boolean save(){
		log.debug("action performed");
		UserAdminView view = controller.getView();
		view.clearErrors();
		view.getPrivilegesPanel().stopCellEditing();
		UserAdminModel model = controller.getModel();
		Group currentGroup = model.getCurrentGroup();
		if (currentGroup == null){
			return true;
		}

		List<Schema> schemas = model.getSchemas();
		if (schemas == null){
			return true;
		}

		if (!view.getPrivilegesPanel().isConfigLockedObjects()){
			GroupPrivilegesUpdater privilegesUpdater = new GroupPrivilegesUpdater(model.getSchemas());
			privilegesUpdater.resetLockedToUnlockedState(model.getCurrentGroup());
		}

		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();

		service.updateSchemas(schemas);

		boolean showLockedColumns = view.getPrivilegesPanel().isConfigLockedObjects();
		if (showLockedColumns != model.isCurrentShowLockedColumns()){
			service.updateConfigLockedObjectsSetting(model.getCurrentGroup().getId(), showLockedColumns);
		}

		SchemaAdminService schemaService = ACSpringFactory.getInstance().getSchemaAdminService();
		schemaService.setInvalidationRequired(DataGroup.Users, true);
		MainPanel2.setInvalidationNeeded(TextID.Users, true);
		return true;
	}

}
