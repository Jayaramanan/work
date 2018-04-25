package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class SalesforceTabSelectionTableModel extends ACTableModel{
	private List<String> tabNames;
	private Set<String> selectedTabNames;
	private static final long serialVersionUID = 1L;
	private String[] columnsNames = new String[] { "", Translation.get(TextID.ObjectName) };
	private Class<?>[] columnClasses = new Class[] { Boolean.class, String.class };

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return columnClasses[columnIndex];
	}

	public SalesforceTabSelectionTableModel(List<String> tabNames){
		this.tabNames = tabNames;
		this.selectedTabNames = new LinkedHashSet<String>(tabNames);
	}

	public void setData(List<String> tabNames){
		if (tabNames == null){
			tabNames = new ArrayList<String>();
		}
		this.tabNames = tabNames;
		this.selectedTabNames = new LinkedHashSet<String>(tabNames);
		fireTableDataChanged();
	}

	public int getColumnCount(){
		return columnsNames.length;
	}

	public int getRowCount(){
		if (tabNames == null)
			return 0;
		return tabNames.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		String tabName = tabNames.get(rowIndex);
		if (columnIndex == 0){
			return selectedTabNames.contains(tabName);
		} else if (columnIndex == 1){
			return tabName;
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
		String tabName = tabNames.get(rowIndex);
		if (columnIndex == 0){
			if ((Boolean) aValue){
				selectedTabNames.add(tabName);
			} else{
				selectedTabNames.remove(tabName);
			}
		}
	}

	public List<String> getSelectedTabNames(){
		return new ArrayList<String>(selectedTabNames);
	}
}