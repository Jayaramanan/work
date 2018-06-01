/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.license.LicenseData.LicenseStatus;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.LicenseAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DeleteLicenseValidationRule implements ACValidationRule{
	private static final Logger log = Logger.getLogger(DeleteLicenseValidationRule.class);
	private List<ErrorEntry> errors;
	private LicenseService licenseService;

	public void setLicenseService(LicenseService licenseService){
		this.licenseService = licenseService;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel mModel){
		errors = new ArrayList<ErrorEntry>();

		LicenseAdminModel model = (LicenseAdminModel) mModel;
		Object currentObject = model.getCurrentObject();
		if (currentObject == null)
			return true;
		LicenseData currLicense = (LicenseData) currentObject;
		if (!currLicense.getStatus().equals(LicenseStatus.Active))
			return true;
		if (!licenseService.canDeleteLicense(currLicense)){
			log.error("License cannot be deleted, it's modules are assigned to users");
			errors.add(new ErrorEntry(TextID.MsgCannotDeleteLicenseInUse));
		}
		log.debug("License can be deleted");

		return errors.isEmpty();
	}
}
