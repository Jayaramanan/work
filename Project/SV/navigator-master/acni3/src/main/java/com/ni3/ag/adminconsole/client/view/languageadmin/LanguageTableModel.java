/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.languageadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class LanguageTableModel extends ACTableModel{

	private static final long serialVersionUID = 1L;
	private List<Language> languages;
	private List<PropertyTranslations> translations = new ArrayList<LanguageTableModel.PropertyTranslations>();

	public LanguageTableModel(List<Language> languages){
		this.languages = languages;
		initMap();
		addColumn(Translation.get(TextID.Property), true, String.class, true);
		initColumns();
	}

	private void initColumns(){
		if (languages == null)
			return;
		for (Language l : languages)
			addColumn(l.getLanguage(), true, String.class, false);
	}

	private void initMap(){
		if (languages == null)
			return;
		Map<String, PropertyTranslations> tempMap = new HashMap<String, PropertyTranslations>();
		for (Language l : languages){
			if (l.getProperties() == null)
				continue;
			for (UserLanguageProperty ulp : l.getProperties()){
				if (!tempMap.containsKey(ulp.getProperty())){
					PropertyTranslations pt = new PropertyTranslations(ulp.getProperty());
					tempMap.put(ulp.getProperty(), pt);
					translations.add(pt);
				}
				tempMap.get(ulp.getProperty()).getTranslationsMap().put(l, ulp);
			}
		}
	}

	public int getRowCount(){
		return translations.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		if (columnIndex == 0)
			return translations.get(rowIndex).getName();
		else{
			PropertyTranslations pt = translations.get(rowIndex);
			Language l = languages.get(columnIndex - 1);
			if (!pt.getTranslationsMap().containsKey(l))
				return null;
			return pt.getTranslationsMap().get(l).getValue();
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		if (columnIndex == 0){
			return translations.get(rowIndex).isNew();
		}
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		super.setValueAt(aValue, rowIndex, columnIndex);
		PropertyTranslations pt = translations.get(rowIndex);
		if (columnIndex == 0){
			pt.setName((String) aValue);
			for (Language l : pt.getTranslationsMap().keySet())
				pt.getTranslationsMap().get(l).setProperty((String) aValue);
		} else{
			Language l = languages.get(columnIndex - 1);
			if (!pt.getTranslationsMap().containsKey(l)){
				if (l.getProperties() == null)
					l.setProperties(new ArrayList<UserLanguageProperty>());
				pt.getTranslationsMap().put(l, new UserLanguageProperty(l));
				pt.getTranslationsMap().get(l).setProperty(pt.getName());
				l.getProperties().add(pt.getTranslationsMap().get(l));
			}
			pt.getTranslationsMap().get(l).setValue((String) aValue);
		}
	}

	public int indexOf(UserLanguageProperty property){
		for (int i = 0; i < translations.size(); i++){
			PropertyTranslations pt = translations.get(i);
			if (pt.getName() == null)
				continue;
			if (pt.getName().equals(property.getProperty()))
				return i;
		}
		return -1;
	}

	public UserLanguageProperty getSelected(int rowIndex){
		PropertyTranslations pt = translations.get(rowIndex);
		if (pt.getTranslationsMap().size() <= 0)
			return null;
		Language l = pt.getTranslationsMap().keySet().iterator().next();
		return pt.getTranslationsMap().get(l);
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return null;
		}

		return Translation.get(TextID.ReadonlyForExistingRecords);
	}

	public int addNewRow(){
		PropertyTranslations pt = new PropertyTranslations(null);
		pt.setIsNew(true);
		translations.add(pt);
		return translations.size() - 1;
	}

	public int removeRow(int row){
		PropertyTranslations pt = translations.get(row);
		translations.remove(row);
		Iterator<Language> it = pt.getTranslationsMap().keySet().iterator();
		while (it.hasNext()){
			Language l = it.next();
			l.getProperties().remove(pt.getTranslationsMap().get(l));
		}
		--row;
		if (row < 0)
			row = 0;
		if (translations.isEmpty())
			return -1;
		return row;
	}

	private class PropertyTranslations{
		private String name;
		private Map<Language, UserLanguageProperty> translationsMap;
		private boolean isNew;

		public PropertyTranslations(String name){
			this.name = name;
			translationsMap = new HashMap<Language, UserLanguageProperty>();
		}

		public void setIsNew(boolean b){
			isNew = true;
		}

		public boolean isNew(){
			return isNew;
		}

		public void setName(String aValue){
			name = aValue;
		}

		public String getName(){
			return name;
		}

		public Map<Language, UserLanguageProperty> getTranslationsMap(){
			return translationsMap;
		}
	}

	public void setData(List<Language> languages){
		translations.clear();
		this.languages = languages;
		initMap();

	}
}
