package com.ni3.ag.adminconsole.client.view.useradmin.privileges;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;

public class BooleanPrivilegesCellRenderer extends JCheckBox implements TableCellRenderer{
	private static final long serialVersionUID = -3637817359360845249L;
	private JTextField empty;

	public BooleanPrivilegesCellRenderer(){
		setHorizontalAlignment(SwingConstants.CENTER);

		empty = new JTextField();
		empty.setBorder(BorderFactory.createEmptyBorder());
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Component c = this;
		if (row < table.getRowCount() && column < table.getColumnCount()
		        && table.getModel() instanceof GroupPrivilegesTableModel){
			GroupPrivilegesTableModel tableModel = (GroupPrivilegesTableModel) table.getModel();
			int modelRow = table.convertRowIndexToModel(row);
			int modelColumn = table.convertColumnIndexToModel(column);
			Object node = tableModel.nodeForRow(modelRow);

			boolean visible = false;
			if (node instanceof Schema || node instanceof PredefinedAttribute || node instanceof ObjectAttribute){
				visible = (modelColumn == GroupPrivilegesTableModel.CAN_READ_INDEX);
			} else if (node instanceof ObjectDefinition){
				visible = (modelColumn == GroupPrivilegesTableModel.CAN_READ_INDEX
				        || modelColumn == GroupPrivilegesTableModel.CAN_CREATE_INDEX || modelColumn == GroupPrivilegesTableModel.CAN_DELETE_INDEX);
			}

			setVisible(visible);
			if (visible){
				setSelected(value != null && (Boolean) value);
				boolean b = table.getModel().isCellEditable(modelRow, modelColumn);
				setEnabled(b);
			} else{
				c = empty;
			}
		}
		return c;
	}
}