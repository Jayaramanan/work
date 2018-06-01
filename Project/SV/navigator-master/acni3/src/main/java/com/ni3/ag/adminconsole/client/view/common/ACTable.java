/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.ni3.ag.adminconsole.client.view.appconf.SettingsBooleanCheckBox;
import com.ni3.ag.adminconsole.client.view.common.clipboard.CopyAction;
import com.ni3.ag.adminconsole.client.view.common.clipboard.PasteAction;
import com.ni3.ag.adminconsole.client.view.licenses.LicenseBooleanCellRenderer;

public class ACTable extends JTable implements ChangeResetable{

	private static final long serialVersionUID = -5039130646153794127L;

	public static final int CELL_SELECTION_ROW_START_INDEX = 0;
	public static final int CELL_SELECTION_ROW_END_INDEX = 1;
	public static final int CELL_SELECTION_COLUMN_START_INDEX = 2;
	public static final int CELL_SELECTION_COLUMN_END_INDEX = 3;

	private int[] selectedRows = new int[0];
	private int minimumColumnidth;

	public static String COPY = "copy";
	public static String PASTE = "paste";

	public ACTable(int minimumColumnidth){
		this.minimumColumnidth = minimumColumnidth;
		// setup of custom editors for string and number type cells
		// to overwrite existing text when start typing
		setDefaultEditor(String.class, new ACTextCellEditor());
		setDefaultEditor(Number.class, new ACNumberCellEditor());
		getTableHeader().setDefaultRenderer(new ACTableHeaderDefaultRenderer(getTableHeader().getDefaultRenderer()));
	}

	public ACTable(){
		this(-1);
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column){
		return new ACTableCellRendererWrapper(super.getCellRenderer(row, column));
	}

	public void enableCopyPaste(){
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		getInputMap().put(copy, COPY);
		getInputMap().put(paste, PASTE);
		getActionMap().put(COPY, new CopyAction(this));
		getActionMap().put(PASTE, new PasteAction(this));
	}

	public void enableToolTips(){
		MouseMotionAdapter m = new MouseMotionAdapter(){
			@Override
			public void mouseMoved(MouseEvent e){
				if (!(getModel() instanceof ACTableModel)){
					return;
				}
				Point p = e.getPoint();
				int row = rowAtPoint(p);
				int column = columnAtPoint(p);
				ACTableModel model = (ACTableModel) getModel();
				if (row < 0 || column < 0 || row >= model.getRowCount())
					return;
				int rowModelIndex = convertRowIndexToModel(row);
				int colModelIndex = convertColumnIndexToModel(column);
				String tooltip = model.getToolTip(rowModelIndex, colModelIndex);
				setToolTipText(tooltip);
			}
		};
		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setReshowDelay(0);
		addMouseMotionListener(m);

	}

	@Deprecated
	@Override
	public void setModel(TableModel dataModel){
		super.setModel(dataModel);
	}

	public void setModel(ACTableModel dataModel){
		super.setModel(dataModel);
		resizeColumns();
	}

	@Override
	public void resetChanges(){
		if (getModel() == null)
			return;
		if (getModel() instanceof ACTableModel)
			((ACTableModel) getModel()).resetChanges();
	}

	@Override
	public boolean isChanged(){
		if (getModel() == null)
			return false;
		if (getModel() instanceof ACTableModel)
			return ((ACTableModel) getModel()).isChanged();

		return false;
	}

	public int[] getSelectedCellIndexes(){
		int[] ret = new int[4];
		if (getCellSelectionEnabled()){
			// Get the min and max ranges of selected cells
			int row1 = getSelectedRow();
			int row2 = getSelectionModel().getMaxSelectionIndex();
			int col1 = getSelectedColumn();
			int col2 = getColumnModel().getSelectionModel().getMaxSelectionIndex();

			row1 = convertRowIndexToModel(row1);
			row2 = convertRowIndexToModel(row2);
			col1 = convertColumnIndexToModel(col1);
			col2 = convertColumnIndexToModel(col2);

			ret[CELL_SELECTION_ROW_START_INDEX] = Math.min(row1, row2);
			ret[CELL_SELECTION_ROW_END_INDEX] = Math.max(row1, row2);
			ret[CELL_SELECTION_COLUMN_START_INDEX] = Math.min(col1, col2);
			ret[CELL_SELECTION_COLUMN_END_INDEX] = Math.max(col1, col2);
		}
		return ret;
	}

	/**
	 * Use to regain focus when update / refresh button is pressed. !NOTE! since SINGLE_INTERVAL_SELECTION is enabled,
	 * you can not guarantee that you will get the same indexes after sorting in a table!
	 */
	public void setSelectedCellIndexes(int[] cells){
		if (getCellSelectionEnabled() && cells[CELL_SELECTION_ROW_START_INDEX] != -1
		        && cells[CELL_SELECTION_ROW_END_INDEX] != -1 && cells[CELL_SELECTION_COLUMN_END_INDEX] != -1
		        && cells[CELL_SELECTION_COLUMN_START_INDEX] != -1){
			cells[CELL_SELECTION_ROW_START_INDEX] = convertRowIndexToView(cells[CELL_SELECTION_ROW_START_INDEX]);
			cells[CELL_SELECTION_ROW_END_INDEX] = convertRowIndexToView(cells[CELL_SELECTION_ROW_END_INDEX]);
			cells[CELL_SELECTION_COLUMN_START_INDEX] = convertColumnIndexToView(cells[CELL_SELECTION_COLUMN_START_INDEX]);
			cells[CELL_SELECTION_COLUMN_END_INDEX] = convertColumnIndexToView(cells[CELL_SELECTION_COLUMN_END_INDEX]);

			getSelectionModel().setSelectionInterval(
			        Math.min(cells[CELL_SELECTION_ROW_START_INDEX], cells[CELL_SELECTION_ROW_END_INDEX]),
			        Math.max(cells[CELL_SELECTION_ROW_START_INDEX], cells[CELL_SELECTION_ROW_END_INDEX]));
			getColumnModel().getSelectionModel().setSelectionInterval(
			        Math.min(cells[CELL_SELECTION_COLUMN_START_INDEX], cells[CELL_SELECTION_COLUMN_END_INDEX]),
			        Math.max(cells[CELL_SELECTION_COLUMN_START_INDEX], cells[CELL_SELECTION_COLUMN_END_INDEX]));
		}
	}

	public boolean selectionContainsRow(int row){
		for (int i = 0; i < selectedRows.length; i++)
			if (selectedRows[i] == row)
				return true;
		return false;
	}

	private void updateSelection(){
		int[] selected = getSelectedRows();
		selectedRows = new int[selected.length];
		for (int i = 0; i < selected.length; i++)
			selectedRows[i] = selected[i];
	}

	private class ACTableCellRendererWrapper implements TableCellRenderer{
		private TableCellRenderer original;

		public ACTableCellRendererWrapper(TableCellRenderer cellRenderer){
			original = cellRenderer;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
		        int row, int column){

			Component c = original.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (row < getRowCount() && column < getColumnCount() && getModel() instanceof ACTableModel){
				ACTableModel actb = (ACTableModel) getModel();
				int zrow = convertRowIndexToModel(row);
				int zcolumn = convertColumnIndexToModel(column);

				updateSelection();

				if (!(c instanceof StrongTableCellRenderer)){
					if (selectionContainsRow(row))
						c.setBackground(Color.LIGHT_GRAY);
					else
						c.setBackground(table.getBackground());
					if (isSelected)
						c.setBackground(table.getSelectionBackground());
				} else if (selectionContainsRow(row))
					((StrongTableCellRenderer) c).setBorder(BorderFactory.createMatteBorder(2, 5, 2, 5, Color.LIGHT_GRAY));
				else
					((StrongTableCellRenderer) c).setBorder(BorderFactory.createMatteBorder(2, 5, 2, 5, table
					        .getBackground()));

				if (c instanceof LicenseBooleanCellRenderer){
					if (!isSelected && actb.isChanged(zrow, zcolumn))
						((JComponent) c).setBorder(BorderFactory.createLineBorder(Color.BLUE));
					else if (isSelected && actb.isChanged(zrow, zcolumn))
						((JComponent) c).setBorder(BorderFactory.createLineBorder(Color.BLUE));
				} else if (c instanceof SettingsBooleanCheckBox){
					((SettingsBooleanCheckBox) c).setChanged(actb.isChanged(zrow, zcolumn));
				} else if (c instanceof JCheckBox){
					if (!isSelected && actb.isChanged(zrow, zcolumn))
						((JComponent) c).setBorder(BorderFactory.createLineBorder(Color.BLUE));
				} else{
					if (!isSelected && actb.isChanged(zrow, zcolumn))
						c.setForeground(Color.BLUE);
					else if (!isSelected){
						c.setForeground(Color.BLACK);
					}
				}
			}
			return c;
		}
	}

	public Component prepareEditor(TableCellEditor editor, int row, int column){

		// int zrow = convertRowIndexToModel(row);

		updateSelection();

		return super.prepareEditor(editor, row, column);
	}

	public void resizeColumns(){
		// strategy - get max width for cells in column and
		// make that the preferred width
		TableColumnModel columnModel = getColumnModel();
		for (int col = 0; col < getModel().getColumnCount(); col++){

			int tableCol = convertColumnIndexToView(col);
			int maxwidth = 0;
			for (int row = 0; row < getModel().getRowCount(); row++){
				TableCellRenderer rend = getCellRenderer(row, col);
				Object value = getModel().getValueAt(row, col);
				Component comp = rend.getTableCellRendererComponent(this, value, false, false, row, tableCol);
				maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
			} // for row
			TableColumn column = columnModel.getColumn(col);
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null)
				headerRenderer = this.getTableHeader().getDefaultRenderer();
			Object headerValue = column.getHeaderValue();
			Component headerComp = headerRenderer.getTableCellRendererComponent(this, headerValue, false, false, 0, tableCol);
			maxwidth = Math.max(maxwidth, headerComp.getPreferredSize().width);
			column.setPreferredWidth(maxwidth);
			column.setMinWidth(minimumColumnidth);
		} // for col
	}

}
