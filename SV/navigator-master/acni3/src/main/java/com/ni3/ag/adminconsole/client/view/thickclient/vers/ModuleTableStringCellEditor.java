/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.awt.Component;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.ni3.ag.adminconsole.client.view.common.ACCellEditor;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACTextCellEditor;
import com.ni3.ag.adminconsole.client.view.common.ACTextField;
import com.ni3.ag.adminconsole.client.view.common.treetable.AbstractCellEditor;
import com.ni3.ag.adminconsole.domain.Module;

public class ModuleTableStringCellEditor extends AbstractCellEditor implements TableCellEditor{
	private ACCellEditor nameCellEditor;
	private ACCellEditor pathCellEditor;
	private ModuleParamsCellEditor paramsCellEditor;

	private DefaultCellEditor otherCellEditor;

	private int activeIndex = -1;

	public ModuleTableStringCellEditor(List<String> paths){
		makePathEditor(paths);
		makeNameEditor();
		ACTextField field = new ACTextField();
		field.setDocument(new JTextFieldLimit(-1));
		otherCellEditor = new ACTextCellEditor(field);
		paramsCellEditor = new ModuleParamsCellEditor();
	}

	private void makeNameEditor(){
		ACComboBox box = new ACComboBox();
		String[] names = Module.NAMES;
		Arrays.sort(names);
		for (String name : names)
			box.addItem(name);
		nameCellEditor = new ACCellEditor(box);
	}

	private void makePathEditor(List<String> paths){
		ACComboBox box = new ACComboBox();
		if (paths != null){
			for (String path : paths)
				box.addItem(path);
		}
		pathCellEditor = new ACCellEditor(box);
	}

	@Override
	public Object getCellEditorValue(){
		switch (activeIndex){
			case ModuleTableModel.PARAMS_COLUMN_INDEX:
				return paramsCellEditor.getCellEditorValue();
			case ModuleTableModel.NAME_COLUMN_INDEX:
				return nameCellEditor.getCellEditorValue();
			case ModuleTableModel.PATH_COLUMN_INDEX:
				return pathCellEditor.getCellEditorValue();
			default:
				return otherCellEditor.getCellEditorValue();
		}
	}

	@Override
	public boolean isCellEditable(EventObject anEvent){
		switch (activeIndex){
			case ModuleTableModel.NAME_COLUMN_INDEX:
				return nameCellEditor.isCellEditable(anEvent);
			case ModuleTableModel.PATH_COLUMN_INDEX:
				return pathCellEditor.isCellEditable(anEvent);
			case ModuleTableModel.PARAMS_COLUMN_INDEX:
				return paramsCellEditor.isCellEditable(anEvent);
			default:
				return otherCellEditor.isCellEditable(anEvent);
		}
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent){
		switch (activeIndex){
			case ModuleTableModel.NAME_COLUMN_INDEX:
				return nameCellEditor.shouldSelectCell(anEvent);
			case ModuleTableModel.PATH_COLUMN_INDEX:
				return pathCellEditor.shouldSelectCell(anEvent);
			case ModuleTableModel.PARAMS_COLUMN_INDEX:
				return paramsCellEditor.shouldSelectCell(anEvent);
			default:
				return otherCellEditor.shouldSelectCell(anEvent);
		}
	}

	@Override
	public boolean stopCellEditing(){
		switch (activeIndex){
			case ModuleTableModel.PARAMS_COLUMN_INDEX:
				return paramsCellEditor.stopCellEditing();
			case ModuleTableModel.NAME_COLUMN_INDEX:
				return nameCellEditor.stopCellEditing();
			case ModuleTableModel.PATH_COLUMN_INDEX:
				return pathCellEditor.stopCellEditing();
			default:
				return otherCellEditor.stopCellEditing();
		}
	}

	@Override
	public void cancelCellEditing(){
		switch (activeIndex){
			case ModuleTableModel.PARAMS_COLUMN_INDEX:
				paramsCellEditor.cancelCellEditing();
				break;
			case ModuleTableModel.NAME_COLUMN_INDEX:
				nameCellEditor.cancelCellEditing();
				break;
			case ModuleTableModel.PATH_COLUMN_INDEX:
				pathCellEditor.cancelCellEditing();
				break;
			default:
				otherCellEditor.cancelCellEditing();
				break;
		}
	}

	@Override
	public void addCellEditorListener(CellEditorListener l){
		switch (activeIndex){
			case ModuleTableModel.PARAMS_COLUMN_INDEX:
				paramsCellEditor.addCellEditorListener(l);
				break;
			case ModuleTableModel.NAME_COLUMN_INDEX:
				nameCellEditor.addCellEditorListener(l);
				break;
			case ModuleTableModel.PATH_COLUMN_INDEX:
				pathCellEditor.addCellEditorListener(l);
				break;
			default:
				otherCellEditor.addCellEditorListener(l);
				break;
		}
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l){
		switch (activeIndex){
			case ModuleTableModel.PARAMS_COLUMN_INDEX:
				paramsCellEditor.removeCellEditorListener(l);
				break;
			case ModuleTableModel.NAME_COLUMN_INDEX:
				nameCellEditor.removeCellEditorListener(l);
				break;
			case ModuleTableModel.PATH_COLUMN_INDEX:
				pathCellEditor.removeCellEditorListener(l);
				break;
			default:
				otherCellEditor.removeCellEditorListener(l);
				break;
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(column);
		activeIndex = tc.getModelIndex();
		switch (activeIndex){
			case ModuleTableModel.PARAMS_COLUMN_INDEX:
				return paramsCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
			case ModuleTableModel.NAME_COLUMN_INDEX:
				return nameCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
			case ModuleTableModel.PATH_COLUMN_INDEX:
				return pathCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
			default: {
				ACTextField field = (ACTextField) otherCellEditor.getTableCellEditorComponent(table, value, isSelected, row,
				        column);
				JTextFieldLimit doc = (JTextFieldLimit) field.getDocument();
				if (activeIndex == ModuleTableModel.VERSION_COLUMN_INDEX)
					doc.setLimit(50);
				else
					doc.setLimit(-1);
				return field;
			}
		}
	}

	@SuppressWarnings("serial")
	public class JTextFieldLimit extends PlainDocument{
		private int limit;

		JTextFieldLimit(int limit){
			super();
			this.limit = limit;
		}

		public void setLimit(int i){
			limit = i;
		}

		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException{
			if (str == null)
				return;

			if (limit < 0 || ((getLength() + str.length()) <= limit)){
				super.insertString(offset, str, attr);
			}
		}
	}
}
