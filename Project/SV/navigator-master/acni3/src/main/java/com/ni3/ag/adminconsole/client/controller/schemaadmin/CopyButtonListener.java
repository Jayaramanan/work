/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminTreeModel;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;


public class CopyButtonListener extends ProgressActionListener{

	private SchemaAdminController controller;

	private ACValidationRule schemaAdminNameRule;

	CopyButtonListener(SchemaAdminController controller){
		super(controller);
		this.controller = controller;
		this.schemaAdminNameRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("schemaAdminNameValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		SchemaAdminView view = controller.getView();
		view.clearErrors();
		TreePath selectedPath = view.getLeftPanel().getSchemaTree().getSelectionPath();
		Schema backup = model.getCurrentSchema();
		if (backup == null)
			return;
		NewSchemaRequestDialog dlg = new NewSchemaRequestDialog(Translation.get(TextID.CopySchemaTitle),
		        Translation.get(TextID.MsgEnterNameOfSchemaCopy), Translation.get(TextID.CopyOf) + " " + backup.getName());
		dlg.setVisible(true);
		String message = dlg.getNewName();
		if (message == null)
			return;

		Schema schemaDef = new Schema();
		schemaDef.setName(message);
		model.setCurrentSchema(schemaDef);

		ACSpringFactory factory = ACSpringFactory.getInstance();

		boolean ok = schemaAdminNameRule.performCheck(model);

		model.setCurrentSchema(backup);
		if (!ok){
			view.renderErrors(schemaAdminNameRule.getErrorEntries());
			return;
		}

		SchemaAdminService schemaAdminService = factory.getSchemaAdminService();
		try{
			Schema newSchema = schemaAdminService.copySchema(backup.getId(), message, SessionData.getInstance().getUser());
			DatabaseInstance oldInstance = model.getCurrentDatabaseInstance();
			controller.reloadData();
			SessionData.getInstance().setCurrentDatabaseInstance(oldInstance);
			restoreTreeSelection(selectedPath, newSchema);
		} catch (ACException e1){
			ErrorContainer ec = new ServerErrorContainerWrapper(e1);
			view.renderErrors(ec);
		}
	}

	private void restoreTreeSelection(TreePath selectedPath, Schema newSchema){
		Object[] oldNodes = selectedPath.getPath();
		Object[] newNodes = new Object[] { oldNodes[0], oldNodes[1], newSchema };

		SchemaAdminTreeModel treeModel = controller.getView().getLeftPanel().getTreeModel();
		TreePath found = new TreeModelSupport().findPathByNodes(newNodes, treeModel);
		if (found != null){
			controller.getView().getLeftPanel().getSchemaTree().setSelectionPath(found);
		}
	}

}
