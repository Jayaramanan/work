/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.PasswordValidator;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserPasswordValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;
	private PasswordValidator passwordValidator;

	public PasswordValidator getPasswordValidator(){
		return passwordValidator;
	}

	public void setPasswordValidator(PasswordValidator passwordValidator){
		this.passwordValidator = passwordValidator;
	}

	UserPasswordValidationRule(){
	}

	@Override
	public boolean performCheck(AbstractModel m){

		errors = new ArrayList<ErrorEntry>();
		UserAdminModel model = (UserAdminModel) m;
		if (model != null && model.getPasswordFormat() != null && !model.getPasswordFormat().isEmpty()){
			User user = model.getUserToChangePassword();

			if (!passwordValidator.parseFormat(model.getPasswordFormat())){
				errors.add(new ErrorEntry(TextID.MsgPasswordFormatNotCorrect));
			} else{
				if (!passwordValidator.isPasswordValid(user.getPassword())){
					errors.add(new ErrorEntry(TextID.MsgPasswordDoesntMatchComplexity));
					errors.add(new ErrorEntry(TextID.PasswordComplexityDescriptionLabel));
				}
			}
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
