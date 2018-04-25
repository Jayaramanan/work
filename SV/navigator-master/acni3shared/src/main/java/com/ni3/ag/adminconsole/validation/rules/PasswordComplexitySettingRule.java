/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.shared.service.def.PasswordValidator;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class PasswordComplexitySettingRule implements ACValidationRule{

	private List<ErrorEntry> errors;
	private PasswordValidator passwordValidator;

	public PasswordValidator getPasswordValidator(){
		return passwordValidator;
	}

	public void setPasswordValidator(PasswordValidator passwordValidator){
		this.passwordValidator = passwordValidator;
	}

	PasswordComplexitySettingRule(){
	}

	@Override
	public boolean performCheck(AbstractModel m){

		errors = new ArrayList<ErrorEntry>();
		SettingsModel model = (SettingsModel) m;
		List<ApplicationSetting> settings = model.getApplicationSettings();
		for (Setting as : settings){
			if (as.getProp() != null && as.getProp().equals(Setting.PASSWORD_COMPLEXITY_SETTING)){
				String passFormat = as.getValue();
				if (passFormat != null && !passFormat.isEmpty() && !passwordValidator.parseFormat(passFormat)){
					errors.add(new ErrorEntry(TextID.MsgPasswordFormatNotCorrect));
				}
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
