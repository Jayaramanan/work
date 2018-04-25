/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.LanguageDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.PredefinedAttribute;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.services.TranslationService;
import com.ni3.ag.navigator.shared.domain.LanguageItem;

public class TranslationServiceImpl implements TranslationService{
	private LanguageDAO languageDAO;

	public void setLanguageDAO(LanguageDAO languageDAO){
		this.languageDAO = languageDAO;
	}

	@Override
	public Schema translateSchema(Schema schema, int languageId){
		final Map<String, String> translations = fillTranslationMap(languageId);
		for (ObjectDefinition entity : schema.getDefinitions()){
			for (Attribute attribute : entity.getAttributes()){
				if (attribute.isPredefined()){
					for (PredefinedAttribute value : attribute.getValues()){
						final String trl = translations.get(value.getLabel());
						value.setLabelTrl(trl != null ? trl : value.getLabel());
					}
				}
			}
		}
		return schema;
	}

	Map<String, String> fillTranslationMap(int languageId){
		final Map<String, String> map = new HashMap<String, String>();
		final List<LanguageItem> translations = languageDAO.getTranslations(languageId);
		for (LanguageItem item : translations){
			map.put(item.getProperty(), item.getValue());
		}
		return map;
	}
}
