package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.List;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

import com.ni3.ag.adminconsole.client.view.common.ACCellEditor;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACCustomComboBoxUIFactory;
import com.ni3.ag.adminconsole.domain.DataSource;

public class DataSourceCellEditor extends ACCellEditor{
	private ACComboBox comboBox;
	private TableCellEditor defaultEditor;

	public DataSourceCellEditor(){
		super(new ACComboBox());
		comboBox = (ACComboBox) getComponent();
		comboBox.setUI(ACCustomComboBoxUIFactory.getNativeCustomComboBoxUI());
	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		boolean found = false;
		for (int i = 0; i < comboBox.getItemCount(); i++){
			if (comboBox.getItemAt(i).toString().equals(value)){
				comboBox.setSelectedIndex(i);
				found = true;
				break;
			}
		}
		if (!found)
			comboBox.setSelectedIndex(0);
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	public void setData(List<DataSource> dataSources){
		comboBox.removeAllItems();
		comboBox.addItem(DataSource.defaultDataSource);
		comboBox.addItem(DataSource.defaultPrimaryDataSource);
//		for (DataSource ds : dataSources){
//			comboBox.addItem(ds);
//		}
	}
}
