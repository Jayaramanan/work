/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder;
import com.ni3.ag.navigator.client.gui.datalist.DataSetTableModel;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder.SortColumn;

/**
 * @version 1.0 02/25/99
 */
@SuppressWarnings("serial")
public class SortButtonRenderer extends JButton implements TableCellRenderer{
	public static final int NONE = 0;
	public static final int DOWN = 1;
	public static final int UP = 2;

	MatrixSortOrder order;
	int pushedColumn;
	boolean bold;

	public int shift;

	JButton downButton, upButton;

	JTable table;

	public SortButtonRenderer(MatrixSortOrder order, JTable table, int shift, boolean bold){
		this.order = order;
		this.shift = shift;
		this.bold = bold;
		this.table = table;

		pushedColumn = -1;

		setMargin(new Insets(0, 0, 0, 0));
		setHorizontalTextPosition(LEFT);
		setIcon(new BlankIcon());

		downButton = new JButton();
		downButton.setMargin(new Insets(0, 0, 0, 0));
		downButton.setHorizontalTextPosition(LEFT);
		downButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
		downButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

		upButton = new JButton();
		upButton.setMargin(new Insets(0, 0, 0, 0));
		upButton.setHorizontalTextPosition(LEFT);
		upButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
		upButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		JButton button = this;
		final SortColumn sort = order.getSortColumn(null, column + shift);
		if (sort != null){
			if (sort.isAsc()){
				button = upButton;
			} else{
				button = downButton;
			}
		}

		if (bold)
			button.setBackground(new Color(255, 0, 0));

		DataSetTableModel model = (DataSetTableModel) table.getModel();

		button.setText((value == null) ? "" : value.toString());
		button.setToolTipText(model.getColumnDescription(column));
		boolean isPressed = (column == pushedColumn);
		button.getModel().setPressed(isPressed);
		button.getModel().setArmed(isPressed);

		return button;
	}

	public void setPressedColumn(int col){
		pushedColumn = col;
	}
}
