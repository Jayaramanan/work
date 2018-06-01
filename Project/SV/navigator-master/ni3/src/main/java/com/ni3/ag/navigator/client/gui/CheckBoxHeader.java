package com.ni3.ag.navigator.client.gui;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class CheckBoxHeader extends JCheckBox implements TableCellRenderer{
	private static final long serialVersionUID = -1666717276417638295L;
	protected int column;
	protected boolean mousePressed = false;

	public CheckBoxHeader(JTable table, ItemListener itemListener){
		setSelected(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		addItemListener(itemListener);
		JTableHeader header = table.getTableHeader();
		if (header != null){
			setForeground(header.getForeground());
			setBackground(header.getBackground());
			header.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					handleClickEvent(e);
					((JTableHeader) e.getSource()).repaint();
				}
			});
		}
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		setColumn(column);
		return this;
	}

	protected void setColumn(int column){
		this.column = column;
	}

	protected void handleClickEvent(MouseEvent e){
		JTableHeader header = (JTableHeader) (e.getSource());
		JTable tableView = header.getTable();
		TableColumnModel columnModel = tableView.getColumnModel();
		int viewColumn = columnModel.getColumnIndexAtX(e.getX());
		int column = tableView.convertColumnIndexToModel(viewColumn);

		if (viewColumn == this.column && e.getClickCount() == 1 && column != -1){
			doClick();
		}
	}

}