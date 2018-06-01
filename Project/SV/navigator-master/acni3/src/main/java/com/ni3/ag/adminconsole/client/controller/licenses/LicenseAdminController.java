/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses;

import java.awt.Component;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.licenses.LicenseAdminView;
import com.ni3.ag.adminconsole.client.view.licenses.LicenseTreeModel;
import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.LicenseAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;
import com.ni3.ag.adminconsole.validation.ACException;


public class LicenseAdminController extends AbstractController{
	private final static Logger log = Logger.getLogger(LicenseAdminController.class);

	private LicenseAdminView view;
	private LicenseAdminModel model;
	private UpdateLicenseListener saveListener;
	private LicenseTreeSelectionListener treeSelectionListener;

	@Override
	public void clearData(){
		view.resetEditedFields();
	}

	public void initializeController(){
		loadModel();
		super.initializeController();

	}

	public void loadModel(){
		DatabaseInstance currentDB = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(currentDB);
		if (currentDB != null && currentDB.isConnected()){
			LicenseService licenseService = ACSpringFactory.getInstance().getLicenseService();
			try{
				List<LicenseData> licenses = licenseService.getLicenseData();
				Collections.sort(licenses, new Comparator<LicenseData>(){
					@Override
					public int compare(LicenseData o1, LicenseData o2){
						int res = o1.getLicense().getProduct().compareTo(o2.getLicense().getProduct());
						if (res == 0)
							res = o1.getStatus().compareTo(o2.getStatus());
						return res;
					}
				});
				model.setLicenses(licenses);

			} catch (ACException e){
				log.error("could not load License Admin model", e);
			}
		}
	}

	@Override
	public AbstractModel getModel(){
		return model;
	}

	@Override
	public Component getView(){
		return view;
	}

	public void setView(LicenseAdminView view){
		this.view = view;
	}

	@Override
	public void setView(Component c){
		view = (LicenseAdminView) c;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		saveListener = new UpdateLicenseListener(this);
		treeSelectionListener = new LicenseTreeSelectionListener(this);
		this.view.addTreeSelectionListener(treeSelectionListener);
		this.view.addUpdateLicenseListener(saveListener);
		this.view.addDeleteLicenseListener(new DeleteLicenseListener(this));
		this.view.addAddLicenseListener(new AddLicenseListener(this));
		this.view.setTreeController(this);
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		LicenseTreeModel treeModel = new LicenseTreeModel(this.model.getLicenseMap(), SessionData.getInstance()
		        .getConnectedDatabaseInstances());
		this.view.setLicenseTreeModel(treeModel);
	}

	@Override
	public void reloadCurrent(){
	}

	@Override
	public void reloadData(){
		clearData();
		loadModel();
		populateDataToView(model, view);
	}

	@Override
	public boolean save(){
		return saveListener.save();
	}

	@Override
	public void setModel(AbstractModel m){
		this.model = (LicenseAdminModel) m;
	}

	public void populateLicense(LicenseData currentLicense){
		if (currentLicense == null){
			view.showLicenseData("");
			view.setNewLicenseAreaEditable(false);
		} else{
			String text = "";
			License lic = currentLicense.getLicense();
			if (lic == null)
				return;
			String licenseText = lic.getLicense();
			for (String key : LicenseData.properties){
				int start = licenseText.indexOf(key);
				int end = licenseText.indexOf("\n", start);
				if (start != -1 && end != -1){
					String startStr = licenseText.substring(0, start);
					String endStr = licenseText.substring(end + 1, licenseText.length());
					licenseText = startStr + endStr;
				}

				Object value = currentLicense.get(key);
				if (value != null)
					text += key + "=" + value + "\n";
			}
			text += licenseText;

			view.showLicenseData(text);
			view.setNewLicenseAreaEditable(true);
		}
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getTreeModel());
			view.setSelectionTreePath(found);
			return false;
		}
		return true;
	}

	public void updateTree(boolean delete){
		LicenseTreeModel tModel = (LicenseTreeModel) view.getTreeModel();
		tModel.setLicenseMap(model.getLicenseMap());
		view.updateTreeUI();
		if (delete){
			model.setCurrentObject(null);
		}

	}

	public License addLicense(License license){
		LicenseService service = (LicenseService) ACSpringFactory.getInstance().getLicenseService();
		return service.addLicense(license);
	}

}
