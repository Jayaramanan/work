/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.geoanalytics;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;

import com.ni3.ag.adminconsole.client.view.common.ACCellEditor;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.domain.GisTerritory;

public class GisTerritoryCellEditor extends ACCellEditor{
	private static final long serialVersionUID = 1L;
	private static final int PARENT_COLUMN_INDEX = 2;
	private GisTerritory empty;

	public GisTerritoryCellEditor(ACComboBox territoryCombo){
		super(territoryCombo);
		empty = new GisTerritory();
		empty.setId(-1);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		JComboBox box = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		if (isParent(column)){
			if (!containsEmpty(box))
				addItem(box);
		} else{
			removeItem(box);
		}

		return box;
	}

	private void removeItem(JComboBox box){
		box.removeItem(empty);
	}

	private void addItem(JComboBox box){
		box.addItem(empty);
	}

	private boolean containsEmpty(JComboBox box){
		for (int i = 0; i < box.getItemCount(); i++)
			if (box.getItemAt(i) == empty)
				return true;
		return false;
	}

	private boolean isParent(int column){
		return column == PARENT_COLUMN_INDEX;
	}
}
