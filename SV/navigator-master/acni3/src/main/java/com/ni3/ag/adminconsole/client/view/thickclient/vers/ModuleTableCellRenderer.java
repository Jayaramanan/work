/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ModuleTableCellRenderer extends JPanel implements TableCellRenderer{
	private static final long serialVersionUID = -1700448457520777855L;
	private TableCellRenderer defaultRenderer;

	public ModuleTableCellRenderer(TableCellRenderer defaultRenderer){
		this.defaultRenderer = defaultRenderer;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(column);
		int activeIndex = tc.getModelIndex();
		Object toShow = value;
		if (activeIndex == ModuleTableModel.PARAMS_COLUMN_INDEX){
			String s = "" + value;
			int index = s.indexOf("\n");
			if (index > 0)
				s = s.substring(0, index);
			toShow = s;
		}
		return defaultRenderer.getTableCellRendererComponent(table, toShow, isSelected, hasFocus, row, column);
	}

}