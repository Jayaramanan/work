/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.licenses.LicenseAdminView;
import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.LicenseAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;


public class UpdateLicenseListener extends ProgressActionListener{

	private LicenseService service;
	private List<ErrorEntry> errors;
	private ACValidationRule duplicateLicenseRule;

	public UpdateLicenseListener(LicenseAdminController controller){
		super(controller);
		this.service = ACSpringFactory.getInstance().getLicenseService();
		this.duplicateLicenseRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("duplicateLicenseRule");
	}

	@Override
	public void performAction(ActionEvent e){
		save();
	}

	public boolean save(){
		LicenseAdminController controller = (LicenseAdminController) getController();
		LicenseAdminView view = (LicenseAdminView) controller.getView();
		LicenseAdminModel model = (LicenseAdminModel) controller.getModel();
		view.clearErrors();
		TreePath selectedPath = view.getTreeSelectionPath();

		Object currObj = model.getCurrentObject();
		if (currObj == null)
			return false;

		errors = new ArrayList<ErrorEntry>();
		String licenseText = view.getLicenseToUpdate();
		LicenseData currLicense = (LicenseData) currObj;
		LicenseData licenseToUpdate = currLicense.clone();

		String productName = getProductNameFromLicense(licenseText);

		if (errors.isEmpty() && !productName.equals(licenseToUpdate.getLicense().getProduct()))
			errors.add(new ErrorEntry(TextID.MsgInvalidLicense));

		model.setUpdateLicenseText(licenseText);
		if (!duplicateLicenseRule.performCheck(model)){
			errors.addAll(duplicateLicenseRule.getErrorEntries());
		}
		if (errors.isEmpty()){
			License license = licenseToUpdate.getLicense();
			license.setLicense(licenseText);
			license.setProduct(productName);
			errors.addAll(service.updateLicense(license));
		}
		if (errors.isEmpty()){
			controller.reloadData();
			restoreSelection(selectedPath);
		} else
			view.renderErrors(errors);

		DatabaseInstance dbid = model.getCurrentDatabaseInstance();
		ObjectVisibilityStore.getInstance().refreshLicenses(dbid);
		MainPanel2.adjustTabVisibilities();

		return errors.isEmpty();
	}

	private void restoreSelection(TreePath selectedPath){
		LicenseAdminView view = (LicenseAdminView) getController().getView();
		if (selectedPath != null){
			TreeModelSupport treeSupport = new TreeModelSupport();
			TreePath found = treeSupport.findPathByNodes(selectedPath.getPath(), view.getTreeModel());
			view.setTreeSelectionPath(found);
		}
	}

	private String getProductNameFromLicense(String license){
		String key = LicenseData.PRODUCT_NAME_PROPERTY + "=";
		int start = license.indexOf(key);
		int end = license.indexOf("\n", start);
		if (start == -1 || end == -1){
			errors.add(new ErrorEntry(TextID.MsgInvalidLicense));
			return "";
		}
		String productNameLine = license.substring(start, end);
		return productNameLine.replaceAll(key, "").replaceAll("\n", "");
	}

}
