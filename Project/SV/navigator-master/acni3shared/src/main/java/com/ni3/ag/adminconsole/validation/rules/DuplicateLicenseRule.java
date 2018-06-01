/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.LicenseAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DuplicateLicenseRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		LicenseAdminModel model = (LicenseAdminModel) amodel;
		List<LicenseData> licenseDataList = model.getLicenses();
		if (licenseDataList == null)
			return false;
		String updateText = model.getUpdateLicenseText();
		for (LicenseData lData : licenseDataList){
			License lic = lData.getLicense();
			String text = lic.getLicense();
			if (updateText.equals(text)){
				errors.add(new ErrorEntry(TextID.MsgDuplicateLicense, new String[] { lic.getProduct() }));
				break;
			}
		}
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
