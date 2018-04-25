/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses;


import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.client.view.licenses.LicenseAdminView;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.LicenseAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DeleteLicenseListener extends ProgressActionListener{

	private LicenseAdminController controller;
	private ACValidationRule rule;

	public DeleteLicenseListener(LicenseAdminController controller){
		super(controller);
		this.controller = controller;
		rule = (ACValidationRule) ACSpringFactory.getInstance().getBean("deleteLicenseValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		LicenseAdminView view = (LicenseAdminView) controller.getView();
		LicenseAdminModel model = (LicenseAdminModel) controller.getModel();
		view.clearErrors();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected() || !model.isLicenseSelected()){
			return;
		}

		LicenseData currentLicense = (LicenseData) model.getCurrentObject();
		if (currentLicense == null){
			return;
		}

		if (!rule.performCheck(model)){
			view.renderErrors(rule.getErrorEntries());
			return;
		}

		Object[] path = getNewSelection(currentLicense, view.getSelectionTreePath());

		LicenseService service = ACSpringFactory.getInstance().getLicenseService();
		service.deleteLicense(currentLicense.getLicense());
		controller.reloadData();

		if (path != null){
			view.setSelectionTreePath(new TreePath(path));
		}

		ObjectVisibilityStore.getInstance().refreshLicenses(dbInstance);
		MainPanel2.adjustTabVisibilities();
	}

	private Object[] getNewSelection(LicenseData current, TreePath oldPath){
		LicenseAdminModel model = (LicenseAdminModel) controller.getModel();
		Object[] path = oldPath.getPath();
		LicenseData diffLicense = null;
		List<LicenseData> licenses = model.getLicenses();
		for (LicenseData license : licenses){
			if (!license.equals(current)){
				diffLicense = license;
				break;
			}
		}
		if (diffLicense != null){
			path[path.length - 1] = diffLicense;
		} else{
			path = Arrays.copyOf(path, path.length - 1);
		}
		return path;
	}

}
