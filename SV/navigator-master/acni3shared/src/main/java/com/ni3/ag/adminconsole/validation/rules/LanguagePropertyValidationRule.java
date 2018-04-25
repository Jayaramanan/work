/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class LanguagePropertyValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	LanguagePropertyValidationRule(){
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		if (model != null){
			List<Language> languages = ((LanguageModel) model).getLanguages();

			Language langToCheck = languages.get(0);
			HashSet<String> propNames = new HashSet<String>();

			for (UserLanguageProperty ulp : langToCheck.getProperties()){
				String prop = ulp.getProperty();
				if (propNames.contains(ulp.getProperty()))
					errors.add(new ErrorEntry(TextID.MsgDuplicatePropertyName, new String[] { ulp.getProperty() }));
				else
					propNames.add(ulp.getProperty());

				if (prop == null || "".equals(prop)){
					errors.add(new ErrorEntry(TextID.MsgFillAllMandatoryFields));
					break;
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
