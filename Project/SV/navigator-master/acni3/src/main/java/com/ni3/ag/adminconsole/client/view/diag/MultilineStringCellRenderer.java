/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.diag;

import java.awt.Component;
import java.awt.FontMetrics;
import java.util.StringTokenizer;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class MultilineStringCellRenderer extends JTextArea implements TableCellRenderer{
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		if (isSelected){
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else{
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		setFont(table.getFont());

		String strVal = (String) value;
		StringTokenizer valueTokens = new StringTokenizer(value != null ? strVal : "");
		String strBuffer = "";
		String text = "";
		FontMetrics fm = getFontMetrics(getFont());
		TableColumnModel tcm = table.getColumnModel();
		int modelColumn = table.convertColumnIndexToModel(column);
		int maxStringWidth = tcm.getColumn(modelColumn).getWidth();
		while (valueTokens.hasMoreTokens()){
			String token = valueTokens.nextToken();
			int stringWidth = fm.stringWidth(strBuffer + token + " ");
			if (stringWidth > maxStringWidth){
				text += strBuffer + "\n";
				strBuffer = token + " ";
			} else{
				strBuffer += token + " ";
			}
		}
		if (!strBuffer.isEmpty())
			text += strBuffer;

		setText(text);
		return this;
	}

}
