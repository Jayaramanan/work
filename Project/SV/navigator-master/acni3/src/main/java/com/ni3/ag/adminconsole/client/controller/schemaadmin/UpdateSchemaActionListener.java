/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.common.DefaultErrorRenderer;
import com.ni3.ag.adminconsole.client.view.common.StringValidator;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.schemaadmin.ObjectDefinitionLeftPanel;
import com.ni3.ag.adminconsole.client.view.schemaadmin.ObjectDefinitionRightPanel;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.PredefinedAttributeService;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class UpdateSchemaActionListener extends ProgressActionListener{
	private SchemaAdminController controller;
	private SchemaAdminView view;
	private SchemaAdminModel model;
	private ACValidationRule generateMandatoryAttributeRule, fieldRule, schemaNameRule, attributeNameValidationRule,
	        predefinedParentValidationRule, objectSortRule, attributeFormatRule, predefinedAttributeValueRule;
	private SchemaAdminService service;

	private final static Logger log = Logger.getLogger(UpdateSchemaActionListener.class);
	private static final String CIS_EDGES_TABLE_NAME = "CIS_EDGES";
	final String TABLE_NAME_PREFIX = "USR_";

	public UpdateSchemaActionListener(SchemaAdminController controller){
		super(controller);
		this.controller = controller;
		this.view = controller.getView();
		this.model = controller.getModel();
		ACSpringFactory factory = ACSpringFactory.getInstance();
		fieldRule = (ACValidationRule) factory.getBean("schemaAdminFieldValidationRule");
		schemaNameRule = (ACValidationRule) factory.getBean("schemaAdminNameValidationRule");
		generateMandatoryAttributeRule = (ACValidationRule) factory.getBean("generateMandatoryAttributeRule");
		attributeNameValidationRule = (ACValidationRule) factory.getBean("attributeNameValidationRule");
		predefinedParentValidationRule = (ACValidationRule) factory.getBean("predefinedParentValidationRule");
		objectSortRule = (ACValidationRule) factory.getBean("uniqueObjectSortValidationRule");
		attributeFormatRule = (ACValidationRule) factory.getBean("attributeFormatRule");
		predefinedAttributeValueRule = (ACValidationRule) factory.getBean("predefinedAttributeValueRule");
		service = ACSpringFactory.getInstance().getSchemaAdminService();
	}

	@Override
	public void performAction(ActionEvent e){
		ObjectAttribute selectedAttribute = view.getRightPanel().getSelectedAttribute();
		ObjectDefinition selectedObject = view.getRightPanel().getSelectedObject();
		TreePath selectedPath = view.getLeftPanel().getSchemaTree().getSelectionPath();

		ObjectDefinition currObject = model.getCurrentObjectDefinition();

		if (!save(e.getActionCommand())){
			return;
		}

		view.resetEditedFields();
		if (currObject != null){
			controller.reloadObjectAttributes();
		} else{
			controller.reloadData();
		}

		if (selectedPath != null){
			ObjectDefinitionLeftPanel panel = view.getLeftPanel();
			TreePath found = new TreeModelSupport().findPathByNodes(selectedPath.getPath(), panel.getTreeModel());
			panel.getSchemaTree().setSelectionPath(found);
			if (currObject != null){
				view.getRightPanel().setActiveTableRow(selectedAttribute);
			} else{
				view.getRightPanel().setActiveObjectTableRow(selectedObject);
			}
		}
	}

	public boolean save(){
		return save(TextID.Update.toString());
	}

	private boolean save(String actionCommand){
		view.getRightPanel().stopEditing();
		view.clearErrors();

		boolean isChanged = view.isChanged();
		ObjectDefinition object = model.getCurrentObjectDefinition();
		Schema schema = null;
		if (object != null){
			schema = object.getSchema();
		} else if (model.getCurrentSchema() != null){
			schema = model.getCurrentSchema();
		}

		boolean ok = true;
		boolean ignoreUserData = actionCommand.equals(TextID.UpdateLiveData.toString());
		if (!objectSortRule.performCheck(model)){
			view.renderErrors(objectSortRule.getErrorEntries());
			return false;
		}
		if (object != null){
			String currentName = object.getName();
			controller.populateDataToModel(model, view);
			ok = updateObjectDefinition(object, ignoreUserData, false);
			if (!ok)
				object.setName(currentName);
		} else if (model.getCurrentSchema() != null){
			String currentName = schema.getName();
			ok = updateSchema(schema, ignoreUserData);
			if (!ok)
				schema.setName(currentName);
		}

		if (ok){
			ok = alterDynamicTables(schema.getId(), object != null ? object.getId() : 0, false);
		}
		if (isChanged){
			service.setInvalidationRequired(DataGroup.Schema, true);
			MainPanel2.setInvalidationNeeded(TextID.Schemas, true);
		}
		if (ok)
			model.clearAllAttributesToDelete();
		return ok;
	}

	public boolean updateSchema(Schema schema, boolean ignoreUserData){
		ObjectDefinitionRightPanel panel = view.getRightPanel();
		schema.setName(StringValidator.validate(panel.getObjectName().getText()));
		schema.setDescription(StringValidator.validate(panel.getDescription().getText()));

		if (!schemaNameRule.performCheck(controller.getModel())){
			view.renderErrors(schemaNameRule.getErrorEntries());
			return false;
		}

		service.updateSchema(schema);

		List<ObjectDefinition> objectsToUpdate = getObjectsToUpdate(schema);

		for (ObjectDefinition od : objectsToUpdate){
			log.debug("changed object: " + od);
			model.setCurrentObjectDefinition(od);
			if (!updateObjectDefinition(od, ignoreUserData, true)){
				model.setCurrentObjectDefinition(null);
				return false;
			}
		}
		model.setCurrentObjectDefinition(null);
		return true;
	}

	PredefinedAttributeEditModel preparePredefinedAttributeModelToCheck(Schema currSchema){
		SchemaAdminService service = ACSpringFactory.getInstance().getSchemaAdminService();
		Schema s = service.reloadFullSchema(currSchema);

		PredefinedAttributeEditModel model = new PredefinedAttributeEditModel();
		int odIndex = s.getObjectDefinitions().indexOf(this.model.getCurrentObjectDefinition());
		ObjectDefinition fullObject = s.getObjectDefinitions().get(odIndex);
		model.setCurrentObject(fullObject);
		List<PredefinedAttribute> deletedPredefineds = new ArrayList<PredefinedAttribute>();
		List<ObjectAttribute> deletedAttrs = this.model.getAllAttributesToDelete();
		for (ObjectAttribute oa : deletedAttrs){
			ObjectAttribute fullOa = null;
			for (ObjectDefinition fullOd : s.getObjectDefinitions()){
				int oaIndex = fullOd.getObjectAttributes().indexOf(oa);
				if (oaIndex != -1){
					fullOa = fullOd.getObjectAttributes().get(oaIndex);
					break;
				}
			}
			if (fullOa != null)
				deletedPredefineds.addAll(fullOa.getPredefinedAttributes());
		}
		model.addDeletedPredefinedAttributes(deletedPredefineds);

		List<Schema> fullSchemas = new ArrayList<Schema>();
		fullSchemas.add(s);
		model.setFullSchemas(fullSchemas);
		return model;

	}

	private boolean updateObjectDefinition(ObjectDefinition object, boolean ignoreUserData, boolean calledFromSchema){
		if (!calledFromSchema){
			if (!fieldRule.performCheck(model)){
				view.renderErrors(fieldRule.getErrorEntries());
				return false;
			}

			if (view.getRightPanel().getTableModel().isDataTypeChanged()){
				PredefinedAttributeEditModel paModel = new PredefinedAttributeEditModel();
				PredefinedAttributeService service = ACSpringFactory.getInstance().getPredefinedAttributeService();
				for (ObjectAttribute oa : object.getObjectAttributes()){
					if (oa.isPredefined() && oa.getId() != null
					        && (oa.isDateDataType() || oa.isDecimalDataType() || oa.isIntDataType())){
						final ObjectAttribute attr = service.reloadAttribute(oa.getId());
						attr.setDataType(oa.getDataType());
						paModel.setCurrentAttribute(attr);
						if (!predefinedAttributeValueRule.performCheck(paModel)){
							view.renderErrors(predefinedAttributeValueRule.getErrorEntries());
							return false;
						}
					}
				}
			}
		}

		if (!model.getAllAttributesToDelete().isEmpty()
		        && !predefinedParentValidationRule.performCheck(preparePredefinedAttributeModelToCheck(object.getSchema()))){
			view.renderErrors(predefinedParentValidationRule.getErrorEntries());
			return false;
		}

		if (!attributeNameValidationRule.performCheck(model)){
			view.renderErrors(attributeNameValidationRule.getErrorEntries());
			return false;
		}
		generateMandatoryAttributeRule.performCheck(model);
		attributeFormatRule.performCheck(model);

		try{
			service.updateObjectDefinition(object, ignoreUserData);
		} catch (ACException e1){
			view.renderErrors(new ServerErrorContainerWrapper(e1));
			return false;
		}

		return true;
	}

	public boolean alterDynamicTables(Integer schemaId, Integer objectId, boolean deleteMetaphorColumns){
		SchemaAdminService service = ACSpringFactory.getInstance().getSchemaAdminService();
		ErrorContainer ec = service.generateSchema(schemaId, objectId);
		if (ec != null && !ec.getErrors().isEmpty()){
			String error = new DefaultErrorRenderer().getMessageText(ec.getErrors());
			JOptionPane.showMessageDialog(view, error, "", JOptionPane.WARNING_MESSAGE);
		}
		return true;
	}

	private List<ObjectDefinition> getObjectsToUpdate(Schema schema){
		ObjectDefinitionRightPanel panel = view.getRightPanel();
		List<ObjectDefinition> objectsToUpdate = panel.getObjectTableModel().getChangedObjects();
		for (ObjectDefinition od : schema.getObjectDefinitions()){
			if (!objectsToUpdate.contains(od) && isTableNameChanged(od)){
				objectsToUpdate.add(od);
			}
		}
		return objectsToUpdate;
	}

	private boolean isTableNameChanged(ObjectDefinition od){
		if (isEdgeTableName(od)){
			return !CIS_EDGES_TABLE_NAME.equals(od.getTableName());
		} else{
			String schemaName = od.getSchema().getName();
			schemaName = schemaName.trim().replaceAll("[ -]", "").toUpperCase();
			String objectName = od.getName().trim().replaceAll("[ -]", "").toUpperCase();
			String tableName = TABLE_NAME_PREFIX + schemaName + "_" + objectName;
			return !tableName.equals(od.getTableName());
		}
	}

	private boolean isEdgeTableName(ObjectDefinition childClone){
		if (!childClone.isEdge()){
			return false;
		}
		for (ObjectAttribute oa : childClone.getObjectAttributes()){
			if (!ObjectAttribute.isFixedEdgeAttribute(oa, true))
				return false;
		}
		return true;
	}

}