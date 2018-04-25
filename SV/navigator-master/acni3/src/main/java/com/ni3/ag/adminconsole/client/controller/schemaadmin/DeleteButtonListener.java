/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import static com.ni3.ag.adminconsole.client.view.Translation.get;

import java.awt.event.ActionEvent;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.common.ACForceConfirmDialog;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class DeleteButtonListener extends ProgressActionListener{

	private SchemaAdminController controller;
	private ACValidationRule deleteSchemaRule;

	public DeleteButtonListener(SchemaAdminController controller){
		super(controller);
		this.controller = controller;
		ACSpringFactory factory = ACSpringFactory.getInstance();
		this.deleteSchemaRule = (ACValidationRule) factory.getBean("DeleteSchemaValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminModel model = controller.getModel();
		SchemaAdminView view = controller.getView();
		ObjectDefinition od = model.getCurrentObjectDefinition();
		Schema schema = model.getCurrentSchema();

		if (od == null && schema == null){
			return;
		}

		if (schema != null){
			if (!deleteSchemaRule.performCheck(model)){
				view.renderErrors(deleteSchemaRule.getErrorEntries());
				return;
			}
		}

		ACForceConfirmDialog confirmDialog = new ACForceConfirmDialog(get(TextID.Delete),
		        get(TextID.ConfirmSchemaObjectDelete), schema == null ? get(TextID.ForceDelete) : null);
		String action = confirmDialog.getSelectedAction();
		if (!action.equals(ACForceConfirmDialog.OK_ACTION))
			return;

		SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();
		try{
			if (schema != null){
				schemaAdminService.deleteSchema(schema.getId());
			} else{
				schemaAdminService.deleteObject(od.getId(), confirmDialog.isForceOption());
			}
		} catch (ACException ex){
			view.renderErrors(new ServerErrorContainerWrapper(ex));
			return;
		}

		Object[] currentPath = ObjectHolder.getInstance().getCurrentPath();
		if (currentPath == null)
			return;
		if (schema != null){
			currentPath = selectNextSchemaInDataSource(currentPath);
		} else{
			currentPath = selectNextObjectInSchema(currentPath);
		}
		view.clearErrors();
		controller.reloadData();

		schemaAdminService.setInvalidationRequired(DataGroup.Schema, true);
		MainPanel2.setInvalidationNeeded(TextID.Schemas, true);

		ObjectHolder.getInstance().setCurrentPath(currentPath);
		view.restoreSelection();
	}

	private Object[] selectNextObjectInSchema(Object[] currentPath){
		// if object selected - tree path should contain 4 objects
		if (currentPath.length != 4)
			return currentPath;
		// second should be schema
		if (!(currentPath[2] instanceof Schema))
			return currentPath;
		Schema schema = (Schema) currentPath[2];
		// if only one object in schema - after delete will select schema
		if (schema.getObjectDefinitions().size() == 1)
			return new Object[] { currentPath[0], currentPath[1], currentPath[2] };
		int index = schema.getObjectDefinitions().indexOf(currentPath[3]);
		index--;
		// if it was first one - select second object (first will be deleted)
		if (index == -1)
			index = 1;
		currentPath[3] = schema.getObjectDefinitions().get(index);
		return currentPath;
	}

	private Object[] selectNextSchemaInDataSource(Object[] currentPath){
		// if schema was selected - path size should be 3 - root+dataSource+schema
		if (currentPath.length != 3)
			return currentPath;
		SchemaAdminModel model = controller.getModel();
		List<Schema> schemaList = model.getSchemaList();
		// if delete the only schema in dataSource -> select dataSource
		if (schemaList.size() == 1)
			return new Object[] { currentPath[0], currentPath[1] };
		// else select previous schema
		int index = schemaList.indexOf(currentPath[2]);
		index--;
		// if schema was first in child nodes - select first
		if (index == -1)
			index = 1;
		currentPath[2] = schemaList.get(index);
		return currentPath;
	}

}
