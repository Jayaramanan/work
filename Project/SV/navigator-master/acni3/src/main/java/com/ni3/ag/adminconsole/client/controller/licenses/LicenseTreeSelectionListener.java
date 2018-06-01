/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.licenses.LicenseAdminView;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.LicenseAdminModel;

public class LicenseTreeSelectionListener extends SchemaTreeSelectionListener{

	private LicenseAdminView view;
	private LicenseAdminModel model;
	private LicenseAdminController controller;

	public LicenseTreeSelectionListener(LicenseAdminController controller){
		this.controller = controller;
		this.view = (LicenseAdminView) controller.getView();
		this.model = (LicenseAdminModel) controller.getModel();
	}

	@Override
	public void changeValue(TreeSelectionEvent e){
		view.clearErrors();
		view.resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();

		LicenseData currentLicense = null;
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) current;
			} else if (current instanceof LicenseData){
				LicenseData license = (LicenseData) current;
				currentLicense = license;
				TreePath parentPath = currentPath.getParentPath();
				if (parentPath.getLastPathComponent() instanceof DatabaseInstance)
					currentDb = (DatabaseInstance) parentPath.getLastPathComponent();
			}

			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}

		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		model.setCurrentObject(currentLicense);

		if (currentDb != null && currentLicense == null && !controller.checkInstanceLoaded()){
			return;
		}
		if (currentLicense != null && !currentLicense.isValid()){
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
			errors.add(new ErrorEntry(TextID.MsgInvalidLicense));
			view.renderErrors(errors);
		}

		controller.populateLicense(currentLicense);

	}

}
