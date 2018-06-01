/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.format;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.appconf.AttributeEditTreeModel;
import com.ni3.ag.adminconsole.client.view.appconf.FormatAttributeTableModel;
import com.ni3.ag.adminconsole.client.view.appconf.FormatAttributesView;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.FormatAttributesModel;
import com.ni3.ag.adminconsole.shared.service.def.AttributeEditService;
import com.ni3.ag.adminconsole.validation.ACException;


public class FormatAttributesController extends AbstractController{
	private FormatAttributesView view;
	private FormatAttributesModel model;
	private UpdateButtonListener updateListener;

	@Override
	public FormatAttributesModel getModel(){
		return model;
	}

	@Override
	public FormatAttributesView getView(){
		return view;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (FormatAttributesModel) m;
	}

	@Override
	public void setView(Component c){
		view = (FormatAttributesView) c;
	}

	public void setModel(FormatAttributesModel m){
		model = m;
	}

	public void setView(FormatAttributesView v){
		view = v;
	}

	@Override
	public void initializeController(){
		loadModel();
		super.initializeController();
		view.setCurrentTreeController(this);
	}

	private void loadModel(){
		DatabaseInstance currentDB = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(currentDB);
		if (currentDB != null && currentDB.isConnected()){
			AttributeEditService service = ACSpringFactory.getInstance().getAttributeEditService();
			List<Schema> schemaList = service.getSchemas();
			model.setSchemaList(schemaList);
		}
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		this.view.addListSelectionListener(new AttributeTableSelectionListener(this));
		this.view.addTreeSelectionListener(new ObjectTreeSelectionListener(this));
		this.view.addRefreshButtonListener(new RefreshButtonListener(this));
		updateListener = new UpdateButtonListener(this);
		this.view.addUpdateButtonListener(updateListener);
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getSchemaTreeModel());
			if (found != null){
				view.setTreeSelectionPath(found);
			}
			return false;
		}
		return true;
	}

	public void reloadAttributeTableModel(){
		FormatAttributeTableModel tableModel = new FormatAttributeTableModel();
		if (model.getCurrentObject() != null)
			tableModel.setData(model.getCurrentObject().getObjectAttributes());
		view.setTableModel(tableModel);
	}

	@Override
	public void reloadCurrent(){
		reloadObjectAttributes();
		reloadAttributeTableModel();
	}

	public void reloadObjectAttributes(){
		ObjectDefinition currentObject = model.getCurrentObject();
		if (currentObject == null){
			return;
		}
		AttributeEditService service = ACSpringFactory.getInstance().getAttributeEditService();
		ObjectDefinition newObject = null;
		try{
			newObject = service.reloadObjectDefinition(currentObject.getId());
		} catch (ACException e){
			view.renderErrors(e.getErrors());
			return;
		}
		currentObject.setObjectAttributes(newObject.getObjectAttributes());
	}

	@Override
	public void reloadData(){
		loadModel();
		populateDataToView(model, view);
		updateTable();
	}

	void updateTable(){
		FormatAttributeTableModel tm = view.getTableModel();
		ObjectDefinition od = model.getCurrentObject();
		if (od != null){
			tm.setData(od.getObjectAttributes());
		} else{
			tm.setData(new ArrayList<ObjectAttribute>());
		}
		view.refreshTable();
	}

	@Override
	public void clearData(){
		model.getSchemaMap().clear();
		model.setCurrentObject(null);
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		List<DatabaseInstance> dbInstances = SessionData.getInstance().getConnectedDatabaseInstances();
		AttributeEditTreeModel treeModel = new AttributeEditTreeModel(getModel().getSchemaMap(), dbInstances);
		getView().setTreeModel(treeModel);
		reloadCurrent();
	}

	@Override
	public boolean save(){
		return updateListener.save();
	}

}
