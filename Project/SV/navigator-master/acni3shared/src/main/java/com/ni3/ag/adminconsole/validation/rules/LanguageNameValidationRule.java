/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class LanguageNameValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	LanguageNameValidationRule(){
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		LanguageModel langModel = (LanguageModel) model;

		List<Language> languages = langModel.getLanguages();
		Language lang = langModel.getCurrentLanguage();

		boolean exists = false;
		if (languages != null && lang != null)
			for (int i = 0; !exists && i < languages.size(); i++){
				Language l = languages.get(i);
				if (!l.equals(lang) && l.getLanguage().equalsIgnoreCase(lang.getLanguage()))
					exists = true;
			}

		if (exists){
			errors.add(new ErrorEntry(TextID.MsgDuplicateLanguage, new String[] { lang.getLanguage() }));
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
