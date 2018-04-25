/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses;


import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.licenses.LicenseAdminView;
import com.ni3.ag.adminconsole.client.view.licenses.LicenseTreeModel;
import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.LicenseAdminModel;

public class AddLicenseListener extends ProgressActionListener{

	private LicenseAdminController controller;

	public AddLicenseListener(LicenseAdminController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		LicenseAdminView view = (LicenseAdminView) controller.getView();
		LicenseAdminModel model = (LicenseAdminModel) controller.getModel();
		view.clearErrors();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null)
			return;

		addNewLicense();

		ObjectVisibilityStore.getInstance().refreshLicenses(dbInstance);
		MainPanel2.adjustTabVisibilities();
	}

	private boolean addNewLicense(){
		LicenseAdminView view = (LicenseAdminView) controller.getView();

		String message = Translation.get(TextID.MsgEnterProductName);
		Object[] values = new Object[] { LicenseData.ACNi3WEB_PRODUCT, LicenseData.NAVIGATOR_PRODUCT };
		String result = (String) ACOptionPane.showInputDialog(view, message, Translation.get(TextID.NewProduct),
		        ACOptionPane.INFORMATION_MESSAGE, null, values, LicenseData.ACNi3WEB_PRODUCT);
		if (result == null || result.isEmpty()){
			return false;
		}

		License license = new License();
		license.setLicense("");
		license.setProduct(result);
		LicenseData ldata = new LicenseData();
		ldata.setLicense(license);
		ldata.setValid(false);

		TreePath currentPath = view.getSelectionTreePath();

		license = controller.addLicense(license);

		controller.loadModel();
		controller.updateTree(false);
		restoreTreeSelection(currentPath, license);
		view.clearErrors();

		return true;
	}

	private void restoreTreeSelection(TreePath selectedPath, License license){
		LicenseAdminModel model = (LicenseAdminModel) controller.getModel();
		LicenseData currentLData = null;
		for (LicenseData lData : model.getLicenses()){
			if (lData.getLicense().equals(license)){
				currentLData = lData;
				break;
			}
		}

		LicenseAdminView view = (LicenseAdminView) controller.getView();
		Object[] oldNodes = selectedPath.getPath();
		Object[] newNodes = new Object[] { oldNodes[0], oldNodes[1], currentLData };

		LicenseTreeModel treeModel = (LicenseTreeModel) view.getTreeModel();
		TreePath found = new TreeModelSupport().findPathByNodes(newNodes, treeModel);
		if (found != null){
			view.setSelectionTreePath(found);
		}
	}

}
