/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class DeleteIconTableModel extends ACTableModel{

	private static final long serialVersionUID = 1L;
	private List<Icon> icons;
	private List<Boolean> booleanList;
	private String[] columnsNames = new String[] { "", Translation.get(TextID.Icon) };
	private Class[] columnClasses = new Class[] { Boolean.class, Icon.class };

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return columnClasses[columnIndex];
	}

	public DeleteIconTableModel(List<Icon> icons){
		this.icons = icons;
		booleanList = new ArrayList<Boolean>();
		for (Icon icon : icons){
			booleanList.add(Boolean.FALSE);
		}
	}

	public void setData(List<Icon> icons){
		this.icons = icons;
	}

	public int getColumnCount(){
		return columnsNames.length;
	}

	public int getRowCount(){
		if (icons == null)
			return 0;
		return icons.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		if (columnIndex == 0){
			return booleanList.get(rowIndex);
		} else if (columnIndex == 1){
			return icons.get(rowIndex);
		}
		return null;
	}

	@Override
	public String getColumnName(int column){
		return columnsNames[column];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		if (columnIndex == 0){
			return true;
		}
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		if (columnIndex == 0){
			booleanList.set(rowIndex, (Boolean) aValue);
		}
	}

	public List<Icon> getSelectedIcons(){
		List<Icon> selectedIcons = new ArrayList<Icon>();
		for (int i = 0; i < booleanList.size(); i++){
			if (booleanList.get(i)){
				selectedIcons.add(icons.get(i));
			}
		}
		return selectedIcons;
	}
}
