/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.languageadmin;

import java.awt.Component;
import java.util.List;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageTableModel;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageTreeModel;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageView;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;
import com.ni3.ag.adminconsole.shared.service.def.LanguageAdminService;

public class LanguageController extends AbstractController{
	private LanguageModel model;
	private LanguageView view;

	private Logger log = Logger.getLogger(LanguageController.class);
	private UpdatePropertyButtonListener updateListener;

	private LanguageController(){
	}

	@Override
	public void initializeController(){
		loadData();
		super.initializeController();
		ACTree tree = (ACTree) view.getLeftPanel().getTree();
		tree.setCurrentController(this);
		updateTreeModel();
		refreshTableData(true);
	}

	@Override
	public void setModel(AbstractModel m){
		model = (LanguageModel) m;
	}

	@Override
	public void setView(Component c){
		view = (LanguageView) c;
	}

	@Override
	public LanguageModel getModel(){
		return model;
	}

	@Override
	public LanguageView getView(){
		return view;
	}

	public void setModel(LanguageModel m){
		model = m;
	}

	public void setView(LanguageView v){
		view = v;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		getView().getLeftPanel().addTreeSelectionListener(new LanguageTreeSelectionListener(this));
		getView().getLeftPanel().addAddLanguageButtonListener(new AddLanguageButtonListener(this));
		getView().getLeftPanel().addDeleteLanguageButtonListener(new DeleteLanguageButtonListener(this));
		getView().addAddButtonActionListener(new AddPropertyButtonListener(this));
		getView().addDeleteButtonActionListener(new DeletePropertyButtonListener(this));
		getView().addRefreshButtonActionListener(new RefreshPropertyButtonListener(this));
		updateListener = new UpdatePropertyButtonListener(this);
		getView().addUpdateButtonActionListener(updateListener);
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
	}

	void updateTreeModel(){
		LanguageTreeModel treeModel = new LanguageTreeModel(getModel().getLanguageMap(), SessionData.getInstance()
		        .getConnectedDatabaseInstances());
		treeModel.setUpdateListener(updateListener);
		getView().getLeftPanel().setTreeModel(treeModel);
	}

	public void refreshTableData(boolean structureChanged){

		if (structureChanged){
			LanguageTableModel tmodel = new LanguageTableModel(model.getLanguages());
			getView().setTableModel(tmodel);
		} else{
			getView().getTableModel().setData(model.getLanguages());
			getView().getTableModel().fireTableDataChanged();
		}
		view.resetEditedFields();
	}

	public void loadData(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			LanguageAdminService service = ACSpringFactory.getInstance().getLanguageAdminService();
			List<Language> languages = service.getLanguages();
			log.debug("Loaded languages count = " + languages != null ? languages.size() : 0);
			model.setLanguages(languages);
			model.setCurrentLanguage(null);
		}
	}

	@Override
	public void reloadData(){
		loadData();
		updateTreeModel();
		refreshTableData(false);
	}

	private void reloadProperties(){
		List<Language> languages = model.getLanguages();
		if (languages == null){
			return;
		}
		LanguageAdminService service = ACSpringFactory.getInstance().getLanguageAdminService();
		for (Language lang : languages){
			Language newLanguage = service.reloadLanguage(lang.getId());
			lang.setProperties(newLanguage.getProperties());
			for (UserLanguageProperty prop : lang.getProperties()){
				prop.setLanguage(lang);
			}
		}
	}

	public void addNewProperty(){
		if (model.getCurrentDatabaseInstance() == null)
			return;
		if (model.getLanguages() == null || model.getLanguages().isEmpty())
			return;
		int index = view.getTrableModel().addNewRow();
		view.getTableModel().fireTableRowsInserted(index, index);
		view.setActiveTableRow(index);
	}

	public void deleteProperty(int row){
		if (model.getCurrentDatabaseInstance() == null)
			return;
		List<Language> languages = model.getLanguages();
		if (languages == null || languages.isEmpty())
			return;
		int nextRow = view.getTableModel().removeRow(row);
		view.getTableModel().fireTableRowsDeleted(row, row);
		if (nextRow != -1)
			view.setActiveTableRow(nextRow);
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getLeftPanel().getTreeModel());
			if (found != null){
				view.getLeftPanel().setSelectionTreePath(found);
			}
			return false;
		}
		return true;
	}

	@Override
	public void clearData(){
		model.getLanguageMap().clear();
		model.setCurrentLanguage(null);
	}

	@Override
	public boolean save(){
		return updateListener.save();
	}

	@Override
	public void reloadCurrent(){
		reloadProperties();
	}

}
