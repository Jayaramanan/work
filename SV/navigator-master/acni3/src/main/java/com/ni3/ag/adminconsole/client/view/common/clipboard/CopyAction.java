/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common.clipboard;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.session.SessionData;

public class CopyAction extends AbstractAction{

	private JTable table;
	private Logger log = Logger.getLogger(CopyAction.class);

	public CopyAction(JTable table){
		this.table = table;
	}

	public void actionPerformed(ActionEvent ae){
		if (!(table.getModel() instanceof AbstractTableModel)){
			return;
		}
		AbstractTableModel model = (AbstractTableModel) table.getModel();

		int[] rows = table.getSelectedRows();
		int[] columns = table.getSelectedColumns();
		if (rows.length == 0 || columns.length == 0){
			return;
		}

		ClipboardObject clipboardObject = new ClipboardObject();

		for (int i = 0; i < columns.length; i++){
			copyColumn(model, columns[i], rows, clipboardObject);
		}

		clipboardObject.setDatabaseInstance(SessionData.getInstance().getCurrentDatabaseInstance());
		log.debug("Copied cells " + rows.length + " x " + columns.length);
		java.awt.datatransfer.Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		TransferableObject to = new TransferableObject(clipboardObject);
		systemClipboard.setContents(to, null);
	}

	protected void copyColumn(AbstractTableModel model, int column, int[] rows, ClipboardObject clipboard){
		int modelColumn = table.convertColumnIndexToModel(column);
		if (modelColumn < 0){
			return;
		}

		List<Object> columnData = new ArrayList<Object>();
		for (int k = 0; k < rows.length; k++){
			int row = rows[k];
			int modelRow = table.convertRowIndexToModel(row);
			if (modelRow < 0){
				return;
			}

			Object value = model.getValueAt(modelRow, modelColumn);
			columnData.add(value);
		}
		clipboard.addColumn(columnData, model.getColumnClass(modelColumn));
	}
}
