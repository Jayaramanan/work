/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.diag;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class DiagnoseTaskStatusRenderer extends JLabel implements TableCellRenderer{

	private static final long serialVersionUID = 7826110166786651991L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		DiagnoseTaskResult result = (DiagnoseTaskResult) value;
		DiagnoseTaskStatus status = result.getStatus();
		if (status != null){
			if (DiagnoseTaskStatus.NotChecked.equals(status)){
				value = "";
			} else{
				value = status.toString();
			}
		}
		setOpaque(true);
		setText(String.valueOf(value));
		setBackground(getBackColorForStatus(status));
		setForeground(Color.black);
		if (result.getErrorDescription() != null)
			setToolTipText(result.getErrorDescription());
		else
			setToolTipText(null);

		return this;
	}

	private Color getBackColorForStatus(DiagnoseTaskStatus status){
		switch (status){
			case Ok:
				return Color.GREEN;
			case Error:
				return Color.RED;
			case Warning:
				return Color.YELLOW;
			case NotChecked:
				return null;
			default:
				return Color.BLACK;
		}
	}

}
