/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.attributes;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.appconf.AttributeEditView;
import com.ni3.ag.adminconsole.client.view.common.DefaultErrorRenderer;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.AttributeEditModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.AttributeEditService;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UpdateButtonListener extends ProgressActionListener{

	private AttributeEditController controller;
	private ACValidationRule duplicateSortValidationRule;
	private ACValidationRule mandatoryFieldValidationRule;
	private ACValidationRule generateMandatoryAttributeRule;
	private ACValidationRule checkObjectContextRule;

	public UpdateButtonListener(AttributeEditController attributeEditController){
		super(attributeEditController);
		controller = attributeEditController;
		ACSpringFactory factory = ACSpringFactory.getInstance();
		duplicateSortValidationRule = (ACValidationRule) factory.getBean("duplicateAttributeSortValidationRule");
		mandatoryFieldValidationRule = (ACValidationRule) factory.getBean("AttributeEditMandatoryFieldValidationRule");
		generateMandatoryAttributeRule = (ACValidationRule) factory.getBean("generateMandatoryAttributeRule");
		checkObjectContextRule = (ACValidationRule) factory.getBean("checkObjectContextRule");
	}

	@Override
	public void performAction(ActionEvent e){
		AttributeEditView view = controller.getView();
		ObjectAttribute selectedAttribute = view.getSelectedAttribute();
		TreePath selectedPath = view.getObjectTree().getSelectionPath();

		String actionCommand = e.getActionCommand();
		boolean updateLiveData = actionCommand.equals(TextID.UpdateLiveData.toString());
		boolean ok = save(updateLiveData);
		if (!ok){
			return;
		}

		controller.reloadCurrent();

		if (selectedPath != null){
			view.getObjectTree().setSelectionPath(selectedPath);
		}
		controller.updateTable();
		view.refreshTable();
		view.resetEditedFields();
		view.setActiveTableRow(selectedAttribute);
	}

	public boolean save(){
		return save(false);
	}

	public boolean save(boolean updateLiveData){
		AttributeEditView view = controller.getView();
		view.stopCellEditing();
		view.clearErrors();

		boolean isChanged = view.isChanged();

		if (view.getAttributeTable().isEditing())
			view.getAttributeTable().getCellEditor().stopCellEditing();

		AttributeEditModel model = (AttributeEditModel) controller.getModel();
		ObjectDefinition current = model.getCurrentObjectDefinition();
		if (current == null)
			return true;
		if (!mandatoryFieldValidationRule.performCheck(model)){
			view.renderErrors(mandatoryFieldValidationRule.getErrorEntries());
			return false;
		}
		if (duplicateSortValidationRule.performCheck(model)){
			view.renderErrors(duplicateSortValidationRule.getErrorEntries());
			return false;
		}
		boolean isPhysDTypeChanged = view.isPhysicalDataTypeChanged();
		boolean ignoreLiveData = !isPhysDTypeChanged || updateLiveData;

		SchemaAdminModel sModel = new SchemaAdminModel();
		sModel.setCurrentObjectDefinition(current);
		generateMandatoryAttributeRule.performCheck(sModel);
		checkObjectContextRule.performCheck(sModel);

		AttributeEditService service = ACSpringFactory.getInstance().getAttributeEditService();

		try{
			service.updateObjectDefinition(current, sModel.getContextsToDelete(), ignoreLiveData);
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
		if (ec != null && !ec.getErrors().isEmpty()){
			String error = new DefaultErrorRenderer().getMessageText(ec.getErrors());
			JOptionPane.showMessageDialog(controller.getView(), error, "", JOptionPane.WARNING_MESSAGE);
		}
		return true;
	}
}
