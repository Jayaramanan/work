/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.appconf.AttributeTableModel;
import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeEditView;
import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeTableModel;
import com.ni3.ag.adminconsole.client.view.appconf.SchemaTreeModel;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.shared.service.def.PredefinedAttributeService;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class PredefinedAttributeEditController extends AbstractController{
	private PredefinedAttributeEditView view;
	private PredefinedAttributeEditModel model;
	private List<ACValidationRule> validationRules;
	private HaloCellEditorListener haloEditorListener;

	private PredefinedAttributeEditController(){
	}

	@Override
	public PredefinedAttributeEditModel getModel(){
		return model;
	}

	@Override
	public PredefinedAttributeEditView getView(){
		return view;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (PredefinedAttributeEditModel) m;
	}

	@Override
	public void setView(Component c){
		view = (PredefinedAttributeEditView) c;
	}

	public void setModel(PredefinedAttributeEditModel m){
		model = m;
	}

	public void setView(PredefinedAttributeEditView v){
		view = v;
	}

	public void setValidationRules(List<ACValidationRule> validationRules){
		this.validationRules = validationRules;
	}

	@Override
	public void initializeController(){
		loadModel();
		super.initializeController();
		ACTree tree = (ACTree) view.getSchemaTree();
		tree.setCurrentController(this);
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		this.view.getSchemaTree().addTreeSelectionListener(new ObjectTreeSelectionListener(this));
		ListSelectionModel selectionModel = this.view.getObjectAttributeListSelectionModel();
		selectionModel.addListSelectionListener(new AttributeTableSelectionListener(this));
		haloEditorListener = new HaloCellEditorListener(this);
		this.view.addHaloCellEditorListener(haloEditorListener);
		this.view.getRefreshButton().addActionListener(new RefreshButtonListener(this));
		this.view.getAddButton().addActionListener(new AddButtonListener(this));
		this.view.getDeleteButton().addActionListener(new DeleteButtonListener(this));
		this.view.getUpdateButton().addActionListener(new UpdateButtonListener(this));
		this.view.addGradientButtonListener(new GradientButtonListener(this));
		this.view.addRecalculateButtonListener(new RecalculateFormulaButtonListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		updateTreeModel();
		getView().setAttributeTableModel(new AttributeTableModel());
		getView().setPredefinedAttributeTableModel(new PredefinedAttributeTableModel());
		reloadAttributeTableModel();
		reloadPredefinedTableModel();
	}

	private void updateTreeModel(){
		SchemaTreeModel schemaTreeModel = new SchemaTreeModel(getModel().getFullSchemaMap(), SessionData.getInstance()
				.getConnectedDatabaseInstances());
		getView().getSchemaTree().setModel(schemaTreeModel);
	}

	public void addPredefinedAttribute(){
		ObjectAttribute oa = model.getCurrentAttribute();
		if (oa == null)
			return;
		PredefinedAttribute pa = new PredefinedAttribute();
		initWithDefaultValues(pa, oa);
		model.addPredefinedAttribute(pa);
		int index = view.getPredefinedAttributeTableModel().getRowCount() - 1;
		view.getPredefinedAttributeTableModel().fireTableRowsInserted(index, index);
		view.setActiveTableRow(pa);
	}

	private void initWithDefaultValues(PredefinedAttribute pa, ObjectAttribute oa){
		pa.setObjectAttribute(oa);
		pa.setToUse(Boolean.TRUE);
	}

	public void deletePredefinedAttribute(int index){
		ObjectAttribute oa = model.getCurrentAttribute();
		if (oa == null || oa.getPredefinedAttributes() == null)
			return;
		PredefinedAttribute deleted = oa.getPredefinedAttributes().get(index);

		PredefinedAttributeService service = ACSpringFactory.getInstance().getPredefinedAttributeService();
		ErrorContainer ec = service.checkReferencesFromMetaphors(deleted);
		if (!ec.getErrors().isEmpty()){
			view.renderErrors(ec.getErrors());
			return;
		}

		Integer newValue = null;
		if (deleted.getId() != null){
			boolean used = service.isUsedInUserTable(deleted);
			if (used){
				List<Object> values = fillValues(oa, deleted);
				Object res = JOptionPane.showInputDialog(view,
						Translation.get(TextID.UpdateReferencesToPredefinedAttribute), Translation
								.get(TextID.DeletePredefinedAttribute), JOptionPane.QUESTION_MESSAGE, null,
						values.toArray(), values.get(0));
				if (res == null){
					return;
				} else if (res instanceof PredefinedAttribute){
					newValue = ((PredefinedAttribute) res).getId();
				}
			}
		}

		model.addDeletedPredefinedAttribute(deleted, newValue);
		model.deletePredefinedAttribute(deleted);
		removePredefinedReferences(deleted);
		List<PredefinedAttribute> pAttributes = oa.getPredefinedAttributes();
		view.getPredefinedAttributeTableModel().fireTableRowsDeleted(index, index);
		if (index >= 0 && pAttributes.size() > 0){
			PredefinedAttribute next = (index < pAttributes.size()) ? pAttributes.get(index) : pAttributes.get(index - 1);
			view.setActiveTableRow(next);
		}
	}

	private List<Object> fillValues(ObjectAttribute oa, PredefinedAttribute deleted){
		final List<Object> values = new ArrayList<Object>();
		final String noValue = Translation.get(TextID.NoValue);
		values.add(noValue);
		for (PredefinedAttribute pa : oa.getPredefinedAttributes()){
			if (!deleted.getId().equals(pa.getId())){
				values.add(pa);
			}
		}
		return values;
	}

	void removePredefinedReferences(PredefinedAttribute pa){
		List<PredefinedAttribute> tablePas = model.getCurrentPredefinedAttributes();
		for (PredefinedAttribute tablePa : tablePas){
			if (tablePa.getChildren() != null)
				tablePa.getChildren().remove(pa);
			if (pa.equals(tablePa.getParent()))
				tablePa.setParent(null);
		}
	}

	public void updatePredefinedAttributeTable(){
		fillTranslations();
		reloadPredefinedTableModel();
	}

	public void reloadAttributeTableModel(){
		view.setAutoCompleteItems(getAllAttributesForObject(model.getCurrentObject()));
		AttributeTableModel tableModel = view.getAttributeTableModel();
		tableModel.setCurrentObject(model.getCurrentObject());
		view.refreshAttributeTable();
	}

	private List<ObjectAttribute> getAllAttributesForObject(ObjectDefinition currentObject){
		if (currentObject == null || currentObject.getSchema() == null)
			return new ArrayList<ObjectAttribute>();
		List<Schema> schemas = model.getFullSchemas();
		if (schemas == null)
			return new ArrayList<ObjectAttribute>();
		for (Schema sch : schemas){
			if (sch.equals(currentObject.getSchema())){
				for (ObjectDefinition od : sch.getObjectDefinitions()){
					if (od.equals(currentObject))
						return od.getObjectAttributes();
				}
			}
		}
		return new ArrayList<ObjectAttribute>();
	}

	public void reloadPredefinedTableModel(){
		PredefinedAttributeTableModel tableModel = view.getPredefinedAttributeTableModel();
		ObjectAttribute oa = model.getCurrentAttribute();
		if (oa != null){
			PredefinedAttributeService service = ACSpringFactory.getInstance().getPredefinedAttributeService();
			List<PredefinedAttribute> paList = service.getAllPredefinedAttributes(oa.getObjectDefinition());
			model.setCurrentPredefinedAttributes(paList);
			oa.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
			for (PredefinedAttribute pa : model.getCurrentPredefinedAttributes())
				if (oa.equals(pa.getObjectAttribute())){
					oa.getPredefinedAttributes().add(pa);
				}
			tableModel.setData(oa, model.getCurrentPredefinedAttributes());
		}
		view.refreshPredefinedTable();
	}

	public void reloadPredefinedAttributes(){
		ObjectAttribute currentAttribute = model.getCurrentAttribute();
		if (currentAttribute == null){
			return;
		}
		PredefinedAttributeService service = ACSpringFactory.getInstance().getPredefinedAttributeService();
		ObjectAttribute newAttribute = null;
		newAttribute = service.reloadAttribute(currentAttribute.getId());
		currentAttribute.setPredefinedAttributes(newAttribute.getPredefinedAttributes());
	}

	public boolean submitCurrentObjectAttribute(){
		ObjectAttribute oa = model.getCurrentAttribute();
		if (oa == null)
			return true;

		view.stopCellEditing();
		view.clearErrors();

		PredefinedAttributeService service = ACSpringFactory.getInstance().getPredefinedAttributeService();

		if (oa.isPredefined()){
			for (ACValidationRule rule : validationRules){
				if (!rule.performCheck(model)){
					view.renderErrors(rule.getErrorEntries());
					return false;
				}
			}

			ErrorContainer ec = new ServerErrorContainerWrapper(service.checkReferencedConnectionTypes(model
					.getDeletedPredefinedAttributes()));
			if (!ec.getErrors().isEmpty()){
				view.renderErrors(ec.getErrors());
				return false;
			}
			if (model.getCurrentPredefinedAttributes() != null){
				for (PredefinedAttribute pa : model.getCurrentPredefinedAttributes()){
					if (!pa.isNested())
						continue;
					int onScreenIndex = oa.getPredefinedAttributes().indexOf(pa);
					if (onScreenIndex > -1){
						PredefinedAttribute onScreenPa = oa.getPredefinedAttributes().get(onScreenIndex);
						onScreenPa.setHaloColor(null);
						pa.setNested(false);
					} else{
						pa.setHaloColor(null);
					}
				}
				model.getCurrentPredefinedAttributes().removeAll(oa.getPredefinedAttributes());
			}
		}

		if (oa.isFormulaAttribute()){
			updateFormula(oa);
		}

		try{
			service.updateObjectAttribute(oa, model.getCurrentPredefinedAttributes(), model
					.getDeletedPredefinedAttributesWithOptions());
			model.clearDeletedPredefinedAttributes();
		} catch (ACException ex){
			view.renderErrors(new ServerErrorContainerWrapper(ex).getErrors());
			return false;
		}

		return true;
	}

	void loadModel(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			PredefinedAttributeService service = ACSpringFactory.getInstance().getPredefinedAttributeService();
			List<Schema> schemas = service.getFullSchemas();
			model.setFullSchemas(schemas);
			model.setCurrentAttribute(null);
			model.clearDeletedPredefinedAttributes();
			SettingsService settingsService = ACSpringFactory.getInstance().getSettingsService();
			final Setting dateFormat = settingsService.getApplicationSetting(Setting.APPLET_SECTION,
					Setting.DATE_FORMAT_PROPERTY);
			model.setDateFormat(dateFormat != null ? dateFormat.getValue() : null);
		}
	}

	@Override
	public void reloadData(){
		loadModel();
		updateTreeModel();
		reloadAttributeTableModel();
		reloadPredefinedTableModel();
	}

	public void fillTranslations(){
		Language language = SessionData.getInstance().getUserLanguage();
		ObjectAttribute attribute = model.getCurrentAttribute();
		if (attribute != null && attribute.getPredefinedAttributes() != null){
			for (PredefinedAttribute pa : attribute.getPredefinedAttributes()){
				for (UserLanguageProperty property : language.getProperties()){
					if (property.getProperty().equals(pa.getLabel())){
						pa.setTranslation(property.getValue());
						break;
					}
				}
			}
		}
	}

	@Override
	public void clearData(){
		model.getFullSchemaMap().clear();
		model.clearDeletedPredefinedAttributes();
		model.setCurrentObject(null);
		model.setCurrentAttribute(null);
		view.setVisibility(false, true);
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getSchemaTree().getModel());
			if (found != null){
				view.getSchemaTree().setSelectionPath(found);
			}
			return false;
		}
		return true;
	}

	void updateFormula(ObjectAttribute attribute){
		PredefinedAttributeEditView view = getView();
		Formula f = attribute.getFormula();
		if (f == null)
			f = new Formula();
		f.setFormula(view.getFormulaText());
		f.setAttribute(attribute);
		attribute.setFormula(f);
	}

	@Override
	public boolean save(){
		return submitCurrentObjectAttribute();
	}

	@Override
	public void reloadCurrent(){
		reloadPredefinedAttributes();
	}

	void checkHalos(PredefinedAttribute pa){
		haloEditorListener.checkNestedPredefinedHalos(pa);
	}
}
