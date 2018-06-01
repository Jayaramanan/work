/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.diag;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

public class FixButtonCellEditor implements TableCellEditor{

	private TaskButton current;
	private List<CellEditorListener> listeners;

	public FixButtonCellEditor(){
		listeners = new ArrayList<CellEditorListener>();
	}

	@Override
	public Object getCellEditorValue(){
		return current;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent){
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent){
		return true;
	}

	@Override
	public boolean stopCellEditing(){
		ChangeEvent ce = new ChangeEvent(current);
		synchronized (this){
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).editingStopped(ce);
		}
		return true;
	}

	@Override
	public void cancelCellEditing(){
		ChangeEvent ce = new ChangeEvent(current);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).editingCanceled(ce);
	}

	@Override
	public void addCellEditorListener(CellEditorListener l){
		listeners.add(l);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l){
		listeners.remove(l);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		JPanel panel = new JPanel();
		if (value != null){
			this.current = (TaskButton) value;
			panel.add(current);
		}
		return panel;
	}
}
