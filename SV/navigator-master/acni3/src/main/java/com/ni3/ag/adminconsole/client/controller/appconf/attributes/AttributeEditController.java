/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.attributes;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.appconf.AttributeEditTableModel;
import com.ni3.ag.adminconsole.client.view.appconf.AttributeEditTreeModel;
import com.ni3.ag.adminconsole.client.view.appconf.AttributeEditView;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.AttributeEditModel;
import com.ni3.ag.adminconsole.shared.service.def.AttributeEditService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

/**
 * 
 * @author user
 */
public class AttributeEditController extends AbstractController{

	private AttributeEditModel model;
	private AttributeEditView view;

	private UpdateButtonListener updateListener;
	private AdvancedViewCheckBoxListener advancedViewCheckboxListener;

	private AttributeEditController(){
	}

	public void setModel(AttributeEditModel m){
		model = m;
	}

	public void setView(AttributeEditView v){
		view = v;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (AttributeEditModel) m;
	}

	@Override
	public void setView(Component c){
		view = (AttributeEditView) c;
	}

	@Override
	public AttributeEditModel getModel(){
		return model;
	}

	@Override
	public AttributeEditView getView(){
		return view;
	}

	@Override
	public void initializeController(){
		loadAttributeData();
		super.initializeController();
		ACTree tree = (ACTree) view.getObjectTree();
		tree.setCurrentController(this);
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		getView().getObjectTree().addTreeSelectionListener(new ObjectTreeSelectionListener(this));
		getView().addCancelButtonListener(new CancelButtonListener(this));
		updateListener = new UpdateButtonListener(this);
		getView().addUpdateButtonListener(updateListener);
		advancedViewCheckboxListener = new AdvancedViewCheckBoxListener(this);
		getView().addAdvancedViewCheckboxListener(advancedViewCheckboxListener);
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		List<DatabaseInstance> dbInstances = SessionData.getInstance().getConnectedDatabaseInstances();
		AttributeEditTreeModel treeModel = new AttributeEditTreeModel(getModel().getSchemaMap(), dbInstances);
		getView().getObjectTree().setModel(treeModel);
	}

	private void loadAttributeData(){
		DatabaseInstance currentDB = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(currentDB);
		if (currentDB != null && currentDB.isConnected()){
			AttributeEditService service = ACSpringFactory.getInstance().getAttributeEditService();
			List<Schema> schemaList = service.getSchemas();
			model.setSchemaList(schemaList);
		}
	}

	void updateTable(){
		AttributeEditTableModel tm = view.getTableModel();
		tm.setValueChangeListener(new AttributeTableModelValueChangeListener(this));
		if (model.getCurrentObjectDefinition() != null){
			final List<ObjectAttribute> attributes = model.getCurrentObjectDefinition().getObjectAttributes();
			tm.setData(attributes);

			boolean hasDiffSorts = hasDifferentSorts(attributes);
			setAdvancedView(hasDiffSorts);

			advancedViewCheckboxListener.setEnabled(false);
			view.setAdvancedViewCheckboxState(hasDiffSorts);
			advancedViewCheckboxListener.setEnabled(true);
		} else{
			tm.setData(new ArrayList<ObjectAttribute>());
		}
		view.refreshTable();
	}

	@Override
	public void reloadData(){
		loadAttributeData();
		populateDataToView(model, view);
		updateTable();
	}

	public void reloadObjectAttributes(){
		ObjectDefinition currentObject = model.getCurrentObjectDefinition();
		if (currentObject == null){
			return;
		}
		AttributeEditService service = ACSpringFactory.getInstance().getAttributeEditService();
		ObjectDefinition newObject = null;
		try{
			newObject = service.reloadObjectDefinition(currentObject.getId());
		} catch (ACException e){
			view.renderErrors(new ServerErrorContainerWrapper(e));
			return;
		}
		currentObject.setObjectAttributes(newObject.getObjectAttributes());
		currentObject.setContext(newObject.getContext());
		for (ObjectAttribute attr : newObject.getObjectAttributes()){
			attr.setObjectDefinition(currentObject);
		}
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getObjectTree().getModel());
			view.getObjectTree().setSelectionPath(found);
			return false;
		}
		return true;
	}

	@Override
	public void clearData(){
		model.getSchemaMap().clear();
		model.setCurrentObjectDefinition(null);
	}

	@Override
	public boolean save(){
		return updateListener.save();
	}

	@Override
	public void reloadCurrent(){
		reloadObjectAttributes();
	}

	public void setAdvancedView(boolean advancedView){
		AttributeEditTableModel model = view.getTableModel();
		if (model.isAdvancedView() != advancedView){
			model.setAdvancedView(advancedView);
			view.setRenderers();
			view.setColumnWidths();
		}
	}

	public void resetSorts(){
		if (model.getCurrentObjectDefinition() != null){
			final List<ObjectAttribute> attributes = model.getCurrentObjectDefinition().getObjectAttributes();
			if (hasDifferentSorts(attributes)){
				resetSorts(attributes);
			}
		}
	}

	boolean hasDifferentSorts(List<ObjectAttribute> attributes){
		boolean result = false;
		for (ObjectAttribute attr : attributes){
			if (!attr.getSort().equals(attr.getSearchSort()) || !attr.getSort().equals(attr.getMatrixSort())
			        || !attr.getSort().equals(attr.getFilterSort()) || !attr.getSort().equals(attr.getLabelSort())){
				result = true;
				break;
			}
		}
		return result;
	}

	void resetSorts(List<ObjectAttribute> attributes){
		for (ObjectAttribute attr : attributes){
			attr.setSearchSort(attr.getSort());
			attr.setMatrixSort(attr.getSort());
			attr.setFilterSort(attr.getSort());
			attr.setLabelSort(attr.getSort());
		}
	}
}
