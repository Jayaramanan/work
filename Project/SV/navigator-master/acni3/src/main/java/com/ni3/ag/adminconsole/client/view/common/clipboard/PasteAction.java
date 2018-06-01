/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACCellEditor;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class PasteAction extends AbstractAction{

	private static final long serialVersionUID = 1L;
	private JTable table;
	private Logger log = Logger.getLogger(PasteAction.class);

	public PasteAction(JTable table){
		this.table = table;
	}

	public void actionPerformed(ActionEvent ae){
		if (table.getCellEditor() != null){
			table.getCellEditor().stopCellEditing();
		}

		if (!(table.getModel() instanceof AbstractTableModel)){
			return;
		}

		java.awt.datatransfer.Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipboardContent = systemClipboard.getContents(null);
		try{
			if (clipboardContent.isDataFlavorSupported(TransferableObject.getObjectDataFlavor())){
				Object obj = clipboardContent.getTransferData(TransferableObject.getObjectDataFlavor());
				if (obj != null && obj instanceof ClipboardObject){
					ClipboardObject co = (ClipboardObject) obj;
					paste(co.getColumnClasses(), co.getValues(), co.getDatabaseInstance());
				}
			} else if (clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor)){
				Object co = clipboardContent.getTransferData(DataFlavor.stringFlavor);
				if (co != null && co instanceof String){
					pasteSystemObject((String) co);
				}
			}
		} catch (UnsupportedFlavorException e){
			log.error("error pasting data", e);
		} catch (IOException e){
			log.error("error pasting data", e);
		}

	}

	private void paste(List<Class<?>> columnClasses, List<List<Object>> pasteValues, DatabaseInstance dbInstance){
		AbstractTableModel model = (AbstractTableModel) table.getModel();
		int[] rows = table.getSelectedRows();
		int[] columns = table.getSelectedColumns();
		if (rows.length == 0 || columns.length == 0 || columnClasses.isEmpty() || pasteValues.isEmpty()){
			return;
		}

		int clipboardColCount = pasteValues.size();
		int clipboardRowCount = pasteValues.get(0).size();

		int resultColumnCount = clipboardColCount > columns.length ? clipboardColCount : columns.length;
		int resultRowCount = clipboardRowCount > rows.length ? clipboardRowCount : rows.length;

		for (int i = 0; i < resultColumnCount; i++){
			int column = i + columns[0];
			if (column >= table.getColumnCount()){
				break;
			}
			List<Object> colValues = pasteValues.get(i % clipboardColCount);
			Class<?> columnClass = columnClasses.get(i % clipboardColCount);

			pasteColumn(model, colValues, columnClass, column, rows[0], rows[0] + resultRowCount, dbInstance);
		}
		log.debug("Pasted cells from clipboard " + pasteValues.get(0).size() + " x " + pasteValues.size());
		model.fireTableRowsUpdated(0, model.getRowCount() - 1);
	}

	private void pasteSystemObject(String pasteString){
		log.debug("Paste cells as string:\n" + pasteString);
		List<List<Object>> pasteValues = new ArrayList<List<Object>>();
		List<Class<?>> columnClasses = new ArrayList<Class<?>>();
		int colCount = -1;
		String[] rows = pasteString.split("\n");
        for (String rowStr : rows) {
            String[] cells = rowStr.split("\t", -1);
            if (colCount < 0) {
                colCount = cells.length;
            }
            for (int col = 0; col < colCount; col++) {
                if (pasteValues.size() <= col) {
                    pasteValues.add(new ArrayList<Object>());
                    columnClasses.add(String.class);
                }
                String value = (cells.length <= col || cells[col].length() == 0) ? null : cells[col];
                pasteValues.get(col).add(value);
            }
        }

		paste(columnClasses, pasteValues, SessionData.getInstance().getCurrentDatabaseInstance());
	}

	public void pasteColumn(AbstractTableModel model, List<Object> values, Class<?> columnClass, int column, int rowFrom,
	        int rowTo, DatabaseInstance dbInstance){
		int modelColumn = table.convertColumnIndexToModel(column);
		if (!columnClass.equals(model.getColumnClass(modelColumn))){
			return;
		}

		TableCellEditor editor = table.getColumnModel().getColumn(column).getCellEditor();
		if (editor == null){
			editor = table.getDefaultEditor(columnClass);
		}
		if (editor != null && editor instanceof ACCellEditor && !isSameInstances(dbInstance)){
			return;
		}

		for (int r = rowFrom; r < rowTo; r++){
			Object value = values.get((r - rowFrom) % values.size());
			if (editor != null && editor instanceof ACCellEditor){
				if (value == null || !((ACCellEditor) editor).contains(value)){
					continue;
				}
			}
			if (r >= table.getRowCount()){
				break;
			}
			int modelRow = table.convertRowIndexToModel(r);
			if (!model.isCellEditable(modelRow, modelColumn)){
				continue;
			}
			model.setValueAt(value, modelRow, modelColumn);
		}
	}

	private boolean isSameInstances(DatabaseInstance clipboardInstance){
		DatabaseInstance currentDatabaseInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		return currentDatabaseInstance != null && clipboardInstance != null
		        && currentDatabaseInstance.equals(clipboardInstance);
	}
}