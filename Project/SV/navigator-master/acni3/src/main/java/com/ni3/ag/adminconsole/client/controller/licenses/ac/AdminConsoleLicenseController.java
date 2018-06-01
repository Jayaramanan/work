/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses.ac;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.licenses.ac.ACLicenseTreeModel;
import com.ni3.ag.adminconsole.client.view.licenses.ac.AdminConsoleLicenseView;
import com.ni3.ag.adminconsole.client.view.licenses.ac.UserACEditionTableModel;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.license.ACModuleDescription;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.AdminConsoleLicenseModel;
import com.ni3.ag.adminconsole.shared.service.def.AdminConsoleLicenseService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;


public class AdminConsoleLicenseController extends AbstractController{
	private final static Logger log = Logger.getLogger(AdminConsoleLicenseController.class);

	private AdminConsoleLicenseView view;
	private AdminConsoleLicenseModel model;
	private ACValidationRule rule;

	@Override
	public void clearData(){
		view.resetEditedFields();
	}

	public void initializeController(){
		loadModel();
		super.initializeController();
		ACTree tree = (ACTree) view.getTree();
		tree.setCurrentController(this);
		rule = (ACValidationRule) ACSpringFactory.getInstance().getBean("adminConsoleLicenseCountRule");
	}

	void loadModel(){
		DatabaseInstance currentDB = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(currentDB);
		if (currentDB != null && currentDB.isConnected()){
			AdminConsoleLicenseService licenseService = ACSpringFactory.getInstance().getACLicenseService();

			ObjectVisibilityStore instance = ObjectVisibilityStore.getInstance();
			instance.refreshLicenses(currentDB);

			List<User> users = licenseService.getAdministrators();
			model.setUsers(users);
			model.setModuleDescriptions(licenseService.getModuleDescriptions());
			log.debug("Loaded administrator count = " + users.size());
		}

	}

	@Override
	public AbstractModel getModel(){
		return model;
	}

	@Override
	public AdminConsoleLicenseView getView(){
		return view;
	}

	public void setView(AdminConsoleLicenseView view){
		this.view = view;
	}

	@Override
	public void setView(Component c){
		view = (AdminConsoleLicenseView) c;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component v){
		view.getTree().addTreeSelectionListener(new ACLicenseTreeSelectionListener(this));
		view.getRefreshButton().addActionListener(new RefreshUserEditionListener(this));
		view.getUpdateButton().addActionListener(new UpdateUserEditionListener(this));
		view.addTableCellSelectionListener(new ACUserEditionTableCellSelectionListener());
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		ACLicenseTreeModel treeModel = new ACLicenseTreeModel(SessionData.getInstance().getConnectedDatabaseInstances());
		this.view.setTreeModel(treeModel);
		UserACEditionTableModel tableModel = new UserACEditionTableModel(new ArrayList<User>(),
		        new ArrayList<ACModuleDescription>());
		this.view.setTableModel(tableModel);
	}

	public void updateTreeModel(){
		ACLicenseTreeModel tModel = (ACLicenseTreeModel) view.getTreeModel();
		tModel.setDBNames(SessionData.getInstance().getConnectedDatabaseInstances());
		view.updateTree();
	}

	@Override
	public void reloadCurrent(){
		reloadUsers();
	}

	private void reloadUsers(){
		AdminConsoleLicenseService licenseService = ACSpringFactory.getInstance().getACLicenseService();
		model.setUsers(licenseService.getAdministrators());
	}

	@Override
	public void reloadData(){
		loadModel();
		updateTreeModel();
		refreshTableModel(true);
	}

	@Override
	public boolean save(){
		return applyChanges();
	}

	@Override
	public void setModel(AbstractModel m){
		this.model = (AdminConsoleLicenseModel) m;
	}

	public void refreshTableModel(boolean updateColumns){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();

		if (dbInstance != null && dbInstance.isConnected() && model.isInstanceLoaded(dbInstance)){
			List<User> users = model.getUsers();
			view.getTableModel().setData(users, model.getModuleDescriptions(), updateColumns);
		} else
			view.getTableModel().setData(new ArrayList<User>(), new ArrayList<ACModuleDescription>(), updateColumns);

		view.getTableModel().updateUsedLicenseCount();
		if (updateColumns){

			view.getTableModel().fireTableStructureChanged();
			view.updateTableHeaders();
		} else{
			view.getTableModel().fireTableDataChanged();
		}
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getTreeModel());
			view.getTree().setSelectionPath(found);
			return false;
		}
		return true;
	}

	public boolean applyChanges(){
		log.debug("applying changes");
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return true;
		}

		view.stopCellEditing();
		view.clearErrors();

		if (!rule.performCheck(model)){
			view.renderErrors(rule.getErrorEntries());
			return false;
		}

		AdminConsoleLicenseService service = ACSpringFactory.getInstance().getACLicenseService();
		service.updateUsers(model.getUsers());
		service.checkLicenseModules();

		return true;
	}
}
