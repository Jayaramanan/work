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
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class AddSchemaButtonListener extends ProgressActionListener{

	private SchemaAdminController controller;
	private ACValidationRule schemaAdminNameRule;

	public AddSchemaButtonListener(SchemaAdminController schemaAdminController){
		super(schemaAdminController);
		controller = schemaAdminController;
		schemaAdminNameRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("schemaAdminNameValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}
		SchemaAdminView view = controller.getView();
		TreePath selectedPath = view.getLeftPanel().getSchemaTree().getSelectionPath();
		NewSchemaRequestDialog dlg = new NewSchemaRequestDialog(Translation.get(TextID.NewSchemaTitle), Translation
		        .get(TextID.MsgEnterNameOfNewSchema), Translation.get(TextID.NewSchema));
		dlg.setVisible(true);
		String message = dlg.getNewName();
		if (message == null)
			return;

		ACSpringFactory factory = ACSpringFactory.getInstance();

		Schema backup = model.getCurrentSchema();

		Schema schemaDef = new Schema();
		schemaDef.setName(message);
		model.setCurrentSchema(schemaDef);

		boolean ok = schemaAdminNameRule.performCheck(model);

		model.setCurrentSchema(backup);
		SchemaAdminService schemaAdminService = factory.getSchemaAdminService();
		if (ok){

			try{
				Schema newSchema = schemaAdminService.addSchema(message, SessionData.getInstance().getUser());
				view.clearErrors();
				controller.reloadData();

				if (model.getSchemaList() != null && model.getSchemaList().size() == 1){
					setDefaultSchemaForApplication(model.getSchemaList().get(0));
				}

				restoreTreeSelection(selectedPath, newSchema);

			} catch (ACException e1){
				ErrorContainer ec = new ServerErrorContainerWrapper(e1);
				view.renderErrors(ec.getErrors());
			}
		}

		if (!ok){
			view.renderErrors(schemaAdminNameRule.getErrorEntries());
		} else{
			schemaAdminService.setInvalidationRequired(DataGroup.Schema, true);
			MainPanel2.setInvalidationNeeded(TextID.Schemas, true);
		}
	}

	private void setDefaultSchemaForApplication(Schema schema){
		SettingsService service = ACSpringFactory.getInstance().getSettingsService();
		final String value = String.valueOf(schema.getId());
		service.updateApplicationSetting(Setting.APPLET_SECTION, Setting.SCHEME_PROPERTY, value);
	}

	private void restoreTreeSelection(TreePath selectedPath, Schema newSchema){
		Object[] oldNodes = selectedPath.getPath();
		Object[] newNodes = new Object[] { oldNodes[0], oldNodes[1], newSchema };

		SchemaAdminTreeModel treeModel = controller.getView().getLeftPanel().getTreeModel();
		TreePath found = new TreeModelSupport().findPathByNodes(newNodes, treeModel);
		controller.getView().getLeftPanel().getSchemaTree().setSelectionPath(found);
	}

}
