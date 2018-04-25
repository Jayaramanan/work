/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.format;


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.appconf.FormatAttributesView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.FormatAttributesModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.AttributeEditService;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;

public class UpdateButtonListener extends ProgressActionListener{

	private FormatAttributesController controller;

	public UpdateButtonListener(FormatAttributesController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		FormatAttributesView view = controller.getView();
		ObjectAttribute selectedAttribute = view.getSelectedAttribute();
		TreePath selectedPath = view.getTreeSelectionPath();
		if (!save()){
			return;
		}

		if (selectedPath != null){
			view.setTreeSelectionPath(selectedPath);
		}
		view.refreshTable();
		view.resetEditedFields();
		view.setActiveTableRow(selectedAttribute);
	}

	public boolean save(){
		FormatAttributesView view = controller.getView();
		view.stopCellEditing();
		view.clearErrors();

		boolean isChanged = view.isChanged();

		view.stopCellEditing();

		FormatAttributesModel model = (FormatAttributesModel) controller.getModel();
		ObjectDefinition current = model.getCurrentObject();
		if (current == null)
			return true;

		AttributeEditService service = ACSpringFactory.getInstance().getAttributeEditService();

		try{
			service.updateObjectDefinition(current, null, true);
		} catch (ACException e1){
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
			errors.add(new ErrorEntry(TextID.MsgPhysicalDataTypeHasChanged));
			errors.addAll(e1.getErrors());
			controller.getView().renderErrors(errors);
			return false;
		}

		alterDynamicTables(current.getSchema().getId(), current.getId());

		if (isChanged){
			SchemaAdminService s = ACSpringFactory.getInstance().getSchemaAdminService();
			s.setInvalidationRequired(DataGroup.Attributes, true);
			MainPanel2.setInvalidationNeeded(TextID.Attributes, true);
		}

		return true;
	}

	public boolean alterDynamicTables(Integer schemaId, Integer objectId){
		SchemaAdminService service = ACSpringFactory.getInstance().getSchemaAdminService();

		ErrorContainer ec = service.generateSchema(schemaId, objectId);
		if (ec != null && !ec.getErrors().isEmpty())
			controller.getView().renderErrors(ec.getErrors());

		return true;
	}

}
