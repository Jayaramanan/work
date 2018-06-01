/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminTreeModel;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;


public class AddObjectDefinitionButtonListener extends ProgressActionListener{

	private SchemaAdminController controller;

	public AddObjectDefinitionButtonListener(SchemaAdminController schemaAdminController){
		super(schemaAdminController);
		controller = schemaAdminController;
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}
		SchemaAdminView view = controller.getView();

		Schema parent = model.getCurrentSchema();
		if (parent == null){
			ObjectDefinition object = model.getCurrentObjectDefinition();
			if (object == null || object.getSchema() == null){
				return;
			}
			parent = object.getSchema();
		}
		TreePath path = view.getLeftPanel().getSchemaTree().getSelectionPath();

		view.clearErrors();
		SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();

		ObjectDefinition newObject;
		try{
			newObject = schemaAdminService.addObjectDefinition(parent, Translation.get(TextID.NewObject), SessionData
			        .getInstance().getUser());
		} catch (ACException e1){
			view.renderErrors(new ServerErrorContainerWrapper(e1));
			return;
		}
		controller.reloadData();

		schemaAdminService.setInvalidationRequired(DataGroup.Schema, true);
		MainPanel2.setInvalidationNeeded(TextID.Schemas, true);

		if (newObject != null){
			restoreTreeSelection(path, newObject);
		}
	}

	private void restoreTreeSelection(TreePath selectedPath, ObjectDefinition newObject){
		Object[] oldNodes = selectedPath.getPath();
		Object[] newNodes = new Object[] { oldNodes[0], oldNodes[1], oldNodes[2], newObject };

		SchemaAdminTreeModel treeModel = controller.getView().getLeftPanel().getTreeModel();
		TreePath found = new TreeModelSupport().findPathByNodes(newNodes, treeModel);
		if (found != null){
			controller.getView().getLeftPanel().getSchemaTree().setSelectionPath(found);
		}
	}
}
