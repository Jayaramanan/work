/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.awt.*;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.StringValidator;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.schemaadmin.*;
import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.DatabaseVersionService;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;
import org.apache.log4j.Logger;

public class SchemaAdminController extends AbstractController{

	private SchemaAdminModel model;
	private SchemaAdminView view;
	private Logger log = Logger.getLogger(SchemaAdminController.class);
	private UpdateSchemaActionListener updateListener;

	private SchemaAdminController(){
	}

	public void setModel(SchemaAdminModel model){
		this.model = model;
	}

	@Override
	public void setModel(AbstractModel model){
		this.model = (SchemaAdminModel) model;
	}

	public void setView(Component c){
		view = (SchemaAdminView) c;
	}

	public void setView(SchemaAdminView view){
		this.view = view;
	}

	@Override
	public SchemaAdminModel getModel(){
		return model;
	}

	@Override
	public SchemaAdminView getView(){
		return view;
	}

	@Override
	public void initializeController(){
		super.initializeController();
		loadSchemaAdminModel();
		ACTree tree = (ACTree) view.getLeftPanel().getSchemaTree();
		tree.setCurrentController(this);
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		getView().getLeftPanel().addTreeSelectionListener(new SchemaAdminTreeSelectionListener(this));
		updateListener = new UpdateSchemaActionListener(this);
		getView().getRightPanel().addUpdateButtonActionListener(updateListener);
		getView().getRightPanel().addDeleteButtonActionListener(new DeleteAttributeActionListener(this));
		getView().getRightPanel().addAddButtonActionListener(new AddShemaAtributeActionListener(this));
		getView().getRightPanel().addCancelButtonActionListener(new CancelButtonActionListener(this));
		getView().getLeftPanel().addCopyButtonListener(new CopyButtonListener(this));
		getView().getLeftPanel().addDeleteButtonListener(new DeleteButtonListener(this));
		getView().getLeftPanel().addAddSchemaButtonListener(new AddSchemaButtonListener(this));
		getView().getLeftPanel().addAddObjectButtonListener(new AddObjectDefinitionButtonListener(this));
		getView().getLeftPanel().addConnectButtonListener(new ConnectButtonListener(this));
		getView().getLeftPanel().addDisconnectButtonListener(new DisconnectButtonListener(this));
		getView().getLeftPanel().addTreeMouseListener(new TreeMouseListener(this));
		getView().getLeftPanel().addImportButtonListener(new ImportButtonListener(this));
		getView().getLeftPanel().addExportButtonListener(new ExportButtonListener(this));
		getView().getLeftPanel().addUpdateCacheButtonListener(new UpdateCacheButtonListener(this));
		getView().getLeftPanel().addAddDatasourceDialogOkButtonListener(new AddDatasourceListener(this));
		getView().getLeftPanel().addDeleteDatasourceButtonListener(new DeleteDatasourceButtonListener(this));
		getView().getLeftPanel().addGenerateDBPropertiesButtonListener(new GenerateDBPropertiesActionListener(this));
		getView().getInfoPanel().addUpdateDatasourceButtonListener(new UpdateDatasourceButtonListener(this));
		getView().getInfoPanel().addRefreshButtonListener(new RefreshDatasourceButtonListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
		ObjectDefinitionRightPanel rightPanel = getView().getRightPanel();
		ObjectDefinition currentObjectDefinition = getModel().getCurrentObjectDefinition();

		String objectDefinitionName = rightPanel.getObjectName().getText();
		currentObjectDefinition.setName(StringValidator.validate(objectDefinitionName));

		ObjectType objectType = (ObjectType) rightPanel.getObjectTypeCombo().getSelectedItem();
		currentObjectDefinition.setObjectType(objectType);

		String description = rightPanel.getDescription().getText();
		currentObjectDefinition.setDescription(StringValidator.validate(description));

		String sort = rightPanel.getSort().getText();
		currentObjectDefinition.setSort(new Integer(sort));
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		setTreeModel();
		ObjectTableModel objectTableModel = new ObjectTableModel(new ArrayList<ObjectDefinition>());
		getView().setObjectTableModel(objectTableModel);
		ObjectAttributeTableModel objectAttributeTableModel = new ObjectAttributeTableModel(new ArrayList<ObjectAttribute>(), getView());
		getView().setObjectAttributeTableModel(objectAttributeTableModel);
	}

	protected void setTreeModel(){
		List<DatabaseInstance> dbInstances = SessionData.getInstance().getDatabaseInstances();
		SchemaAdminTreeModel treeModel = new SchemaAdminTreeModel(getModel().getSchemaMap(), dbInstances);
		getView().setTreeModel(treeModel);
	}

	protected void updateTreeModel(List<DatabaseInstance> dbNames){
		getView().getTreeModel().setDatabaseInstances(dbNames);
		getView().updateTree();
	}

	public void deleteAttributes(List<ObjectAttribute> attrsToDelete){
		ObjectDefinition currentObjectDefinition = getModel().getCurrentObjectDefinition();
		List<ObjectAttribute> attributes = currentObjectDefinition.getObjectAttributes();
		int firstIndex = Integer.MAX_VALUE;
		int lastIndex = -1;
		if (attrsToDelete != null){
			for (ObjectAttribute oa : attrsToDelete){
				int index = attributes.indexOf(oa);
				if (index < firstIndex)
					firstIndex = index;
				if (index > lastIndex)
					lastIndex = index;
			}
			attributes.removeAll(attrsToDelete);
			log.debug("Removed object attributes: " + attrsToDelete);
			getView().getRightPanel().getTableModel().fireTableRowsDeleted(firstIndex, lastIndex);

			if (lastIndex >= 0 && attributes.size() > 0){
				ObjectAttribute next = (firstIndex < attributes.size()) ? attributes.get(firstIndex) : attributes
						.get(firstIndex - 1);
				view.getRightPanel().setActiveTableRow(next);
			}
		}
	}

	public void addNewAtribute(){
		ObjectDefinition currentObject = getModel().getCurrentObjectDefinition();
		ObjectAttribute newAttribute = new ObjectAttribute(currentObject);
		setAttributeDefaultValues(newAttribute);
		newAttribute.setInTable(currentObject.getTableName());
		newAttribute.setInMatrix(InMatrixType.Displayed.getValue());
		currentObject.getObjectAttributes().add(newAttribute);
		int index = currentObject.getObjectAttributes().size() - 1;
		view.getRightPanel().getTableModel().fireTableRowsInserted(index, index);
		view.getRightPanel().setActiveTableRow(newAttribute);
	}

	private void setAttributeDefaultValues(ObjectAttribute newAttribute){
		newAttribute.setName("");
		newAttribute.setLabel("");
		newAttribute.setPredefined(false);
		newAttribute.setDataType(DataType.TEXT);
		newAttribute.setInFilter(false);
		newAttribute.setInLabel(false);
		newAttribute.setInSimpleSearch(false);
		newAttribute.setInAdvancedSearch(false);
		newAttribute.setInMetaphor(false);
		newAttribute.setInToolTip(false);
		newAttribute.setLabelBold(false);
		newAttribute.setLabelItalic(false);
		newAttribute.setLabelUnderline(false);
		newAttribute.setContentBold(false);
		newAttribute.setContentItalic(false);
		newAttribute.setContentUnderline(false);
		newAttribute.setCreated(new Date());
		newAttribute.setInTable("");
		newAttribute.setInExport(false);
		newAttribute.setExportLabel("");
		newAttribute.setInPrefilter(false);
		newAttribute.setAggregable(false);
		setNextSorts(newAttribute);
	}

	void setNextSorts(ObjectAttribute attribute){
		int nextSort = 1;
		int nextLabelSort = 1;
		int nextFilterSort = 1;
		int nextSearchSort = 1;
		int nextMatrixSort = 1;
		ObjectDefinition current = model.getCurrentObjectDefinition();
		if (current != null && current.getObjectAttributes() != null){
			for (ObjectAttribute attr : current.getObjectAttributes()){
				if (attr.getSort() != null && attr.getSort() >= nextSort){
					nextSort = attr.getSort() + 1;
				}
				if (attr.getLabelSort() != null && attr.getLabelSort() >= nextLabelSort){
					nextLabelSort = attr.getLabelSort() + 1;
				}
				if (attr.getFilterSort() != null && attr.getFilterSort() >= nextFilterSort){
					nextFilterSort = attr.getFilterSort() + 1;
				}
				if (attr.getSearchSort() != null && attr.getSearchSort() >= nextSearchSort){
					nextSearchSort = attr.getSearchSort() + 1;
				}
				if (attr.getMatrixSort() != null && attr.getMatrixSort() >= nextMatrixSort){
					nextMatrixSort = attr.getMatrixSort() + 1;
				}
			}
		}
		attribute.setSort(nextSort);
		attribute.setLabelSort(nextLabelSort);
		attribute.setFilterSort(nextFilterSort);
		attribute.setSearchSort(nextSearchSort);
		attribute.setMatrixSort(nextMatrixSort);
	}

	@Override
	public void reloadData(){
		loadSchemaAdminModel();
		List<DatabaseInstance> dbInstances = SessionData.getInstance().getDatabaseInstances();
		updateTreeModel(dbInstances);

	}

	protected void loadSchemaAdminModel(){
		DatabaseInstance currentDB = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(currentDB);
		if (currentDB != null && currentDB.isConnected()){
			SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();
			List<Schema> schemas = schemaAdminService.getSchemas();
			if (!ObjectVisibilityStore.getInstance().isSchemaVisible()){
				schemas = new ArrayList<Schema>();
				view.setEnabledSchemaButtons(false, false, false);
			} else
				view.setEnabledSchemaButtons(true, model.getCurrentSchema() != null,
						model.getCurrentObjectDefinition() != null);
			view.setConnected(true);
			model.setSchemaList(schemas);
		} else if (currentDB != null && !currentDB.isConnected() && view.getLeftPanel() != null){
			view.setEnabledSchemaButtons(false, false, false);
			view.setConnected(false);
		}
	}

	public void reloadObjectAttributes(){
		ObjectDefinition currentObject = model.getCurrentObjectDefinition();
		if (currentObject == null){
			return;
		}
		SchemaAdminService service = ACSpringFactory.getInstance().getSchemaAdminService();
		ObjectDefinition newObject;
		try{
			newObject = service.loadSingleObjectDefinition(currentObject.getId());
		} catch (ACException e){
			view.renderErrors(new ServerErrorContainerWrapper(e));
			return;
		}
		currentObject.setTableName(newObject.getTableName());
		currentObject.setObjectAttributes(newObject.getObjectAttributes());
		for (ObjectAttribute attr : currentObject.getObjectAttributes()){
			attr.setObjectDefinition(currentObject);
		}
		populateNewObjectDefinition(currentObject);
		view.getLeftPanel().getSchemaTree().repaint();
	}

	public void reloadObjectDefinitions(){
		Schema currentSchema = model.getCurrentSchema();
		if (currentSchema == null){
			return;
		}
		SchemaAdminService service = ACSpringFactory.getInstance().getSchemaAdminService();
		Schema newSchema;
		newSchema = service.loadSingleSchema(currentSchema.getId());

		currentSchema.setObjectDefinitions(newSchema.getObjectDefinitions());
		for (ObjectDefinition od : currentSchema.getObjectDefinitions()){
			od.setSchema(currentSchema);
		}
	}

	public void populateNewSchemaDefinition(Schema schema){
		view.getRightPanel().setObjectNameLabelString(Translation.get(TextID.SchemaName));
		view.setObjectName(schema != null ? schema.getName() : "");
		view.setSort("");
		view.setDescription(schema != null ? schema.getDescription() : "");
		view.setCreatedBy(schema != null ? schema.getCreatedBy() : new User());
		view.setCreationDate(schema != null ? schema.getCreationDate() : new Date());

		ObjectTableModel objectTableModel = view.getRightPanel().getObjectTableModel();
		objectTableModel.setData(schema != null ? schema.getObjectDefinitions() : new ArrayList<ObjectDefinition>());
		objectTableModel.fireTableDataChanged();

		view.getRightPanel().setPanelToSchemaInfo(true);
		ObjectAttributeTableModel objectAttributeTableModel = view.getRightPanel().getTableModel();
		boolean updateStructure = objectAttributeTableModel.isEdgeObjectModel();
		objectAttributeTableModel.setData(new ArrayList<ObjectAttribute>(), false);
		objectAttributeTableModel.fireTableDataChanged();
		if (updateStructure)
			objectAttributeTableModel.fireTableStructureChanged();

		view.getRightPanel().setVisibleFields(false);
		view.getRightPanel().setDescriptionEnabled(true);
	}

	public void populateNewObjectDefinition(ObjectDefinition objectDefinition){
		view.getRightPanel().setObjectNameLabelString(Translation.get(TextID.ObjectName));
		view.setObjectName(objectDefinition.getName());
		view.setSort(objectDefinition.getSort() != null ? objectDefinition.getSort().toString() : "");
		view.setDescription(objectDefinition.getDescription());
		view.setObjectType(objectDefinition.getObjectType());
		view.setCreatedBy(objectDefinition.getCreatedBy());
		view.setCreationDate(objectDefinition.getCreationDate());

		view.getRightPanel().setPanelToSchemaInfo(false);
		List<ObjectAttribute> objectAttributes = objectDefinition.getObjectAttributes();
		boolean isEdgeObject = objectDefinition.isEdge();
		ObjectAttributeTableModel objectAttributeTableModel = view.getRightPanel().getTableModel();
		boolean updateStructure = objectAttributeTableModel.isEdgeObjectModel() != isEdgeObject;
		objectAttributeTableModel.setData(objectAttributes, isEdgeObject);
		objectAttributeTableModel.fireTableDataChanged();
		if (updateStructure)
			objectAttributeTableModel.fireTableStructureChanged();
		objectAttributeTableModel.fireTableDataChanged();

		view.setPredefinedReferenceData(Formula.getPredefinedTypes());

		view.getRightPanel().setVisibleFields(true);
		view.getRightPanel().setDescriptionEnabled(true);
		view.getRightPanel().setDataSources(new ArrayList<DataSource>());
		view.setDataSourceCellEditor();
	}

	public void disconnect(DatabaseInstance dbInstance){
		SessionData.getInstance().setDatabaseInstanceConnected(dbInstance, false);
		model.getSchemaMap().remove(dbInstance);
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		log.debug("instance connected? : " + dbInstance.isConnected());
		log.debug("instance loaded? : " + model.isInstanceLoaded(dbInstance));
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			AbstractTreeModel treeModel = view.getLeftPanel().getTreeModel();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, treeModel);
			view.getLeftPanel().getSchemaTree().setSelectionPath(found);
			log.debug("Loaded data for instance: " + dbInstance);
			return false;
		}
		return true;
	}

	@Override
	public void clearData(){
		model.getSchemaMap().clear();
		model.setCurrentObjectDefinition(null);
		model.setCurrentSchema(null);
	}

	public void updateInfoView(){
		InfoPanel panel = view.getInfoPanel();
		DatabaseInstance db = model.getCurrentDatabaseInstance();
		if (db == null){
			panel.setNavigatorHost("");
			panel.setDatabaseId("");
			panel.setDatasourceNames(new ArrayList<String>());
			panel.setVersions("", "");
			panel.setMappath("");
			panel.setDocroot("");
			panel.setRasterServerUrl("");
			panel.setDeltaThreshold("");
			panel.setDeltaOutThreshold("");
			panel.showExpiringLicenceLabel(null);
			panel.setCacheRequiresRefresh(false);
		} else{
			panel.setNavigatorHost(db.getNavigatorHost());
			panel.setDatabaseId(db.getDatabaseInstanceId());
			panel.setDatasourceNames(db.getDatasourceNames());
			DatabaseVersionService versionService = ACSpringFactory.getInstance().getDatabaseVersionService();
			if (!db.isInited())
				panel.setVersions("", "");
			else
				panel.setVersions(versionService.getExpectedVersion(), versionService.getActualVersion());
			panel.setMappath(db.getMapPath());
			panel.setDocroot(db.getDocrootPath());
			panel.setRasterServerUrl(db.getRasterServerUrl());
			panel.setModulePath(db.getModulePath());
			panel.setDeltaThreshold(db.getDeltaThreshold());
			panel.setDeltaOutThreshold(db.getDeltaOutThreshold());
			ObjectVisibilityStore ovStore = ObjectVisibilityStore.getInstance();
			List<String> licenseNames = getExpiringLicenseNames(ovStore.getExpiringLicenses());
			panel.showExpiringLicenceLabel(licenseNames);
			SchemaAdminService service = ACSpringFactory.getInstance().getSchemaAdminService();
			if (db.isInited())
				panel.setCacheRequiresRefresh(service.isAnyInvalidationRequired());
		}
		panel.realignFields();
	}

	private List<String> getExpiringLicenseNames(List<LicenseData> lDataList){
		List<String> ret = new ArrayList<String>();
		if (lDataList != null)
			for (LicenseData ldata : lDataList)
				ret.add(ldata.getLicense().getProduct());
		return ret;
	}

	public void setActiveViewInfoPanel(){
		view.setActiveView(view.getInfoPanel());
	}

	public void setActiveViewObjectPanel(){
		view.setActiveView(view.getObjectPanel());
	}

	@Override
	public boolean save(){
		return updateListener.save();
	}

	@Override
	public void reloadCurrent(){
		if (model.getCurrentObjectDefinition() != null){
			reloadObjectAttributes();
		} else if (model.getCurrentSchema() != null){
			reloadObjectDefinitions();
		}
	}

	public void showDBPropertiesView(String text){
		getView().getLeftPanel().showDBPropertiesDialog(text);
	}

}