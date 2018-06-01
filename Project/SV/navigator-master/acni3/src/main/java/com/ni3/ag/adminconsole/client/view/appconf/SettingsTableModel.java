/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class SettingsTableModel extends ACTableModel{
	private static final long serialVersionUID = -7347691494215099012L;

	private List<?> settings;
	private static final int SECTION_COLUMN_INDEX = 0;
	private static final int PROP_COLUMN_INDEX = 1;
	public static final int VALUE_COLUMN_INDEX = 2;

	public SettingsTableModel(List<?> settings){
		this.settings = settings;

		addColumn(Translation.get(TextID.Section), true, String.class, true);
		addColumn(Translation.get(TextID.Property), true, String.class, true);
		addColumn(Translation.get(TextID.Value), true, String.class, true);
	}

	public int getRowCount(){
		return settings != null ? settings.size() : 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		Setting as = (Setting) settings.get(rowIndex);
		switch (columnIndex){
			case 0:
				return as.getSection();
			case 1:
				return as.getProp();
			case 2:
				return as.getValue();
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex){
		Setting us = (Setting) settings.get(rowIndex);
		if (columnIndex == SECTION_COLUMN_INDEX || columnIndex == PROP_COLUMN_INDEX){
			return us.isNew();
		} else if (us.getProp() != null
		        && (us.getProp().equals(Setting.LANGUAGE_PROPERTY) || us.getProp().equals(Setting.SCHEME_PROPERTY)
		                || us.getProp().equals(Setting.TAB_SWITCH_ACTION_PROPERTY)
		                || us.getProp().equals(Setting.INHERITS_GROUP_SETTINGS_PROPERTY) || us.getProp().equals(
		                Setting.HIDE_GIS_PANEL_PROPERTY))){
			return false;
		}
		return super.isCellEditable(rowIndex, columnIndex);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		aValue = validateValue(aValue);
		super.setValueAt(aValue, rowIndex, columnIndex);
		Setting as = (Setting) settings.get(rowIndex);
		switch (columnIndex){
			case 0:
				as.setSection((String) aValue);
				break;
			case 1:
				as.setProp((String) aValue);
				break;
			case 2:
				as.setValue((String) aValue);
				break;
		}
	}

	public int indexOf(Setting newSetting){
		return settings.indexOf(newSetting);
	}

	public Setting getSelected(int rowIndex){
		if (rowIndex >= 0 && rowIndex < settings.size()){
			return (Setting) settings.get(rowIndex);
		}
		return null;
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return null;
		}
		Setting us = (Setting) settings.get(row);
		if ((column == SECTION_COLUMN_INDEX || column == PROP_COLUMN_INDEX) && !us.isNew()){
			return Translation.get(TextID.ReadonlyForExistingRecords);
		} else{
			return Translation.get(TextID.ReadonlyFixedSetting);
		}
	}
}
