/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses.ac;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.licenses.ac.UserACEditionTableModel;
import com.ni3.ag.adminconsole.license.ACModuleDescription;
import com.ni3.ag.adminconsole.license.AdminConsoleModule;

public class ACUserEditionTableCellSelectionListener implements MouseListener{

	public ACUserEditionTableCellSelectionListener(){
	}

	@Override
	public void mouseClicked(MouseEvent e){
		if (e.getButton() == MouseEvent.BUTTON3 && e.getSource() instanceof ACTable){
			ACTable table = (ACTable) e.getSource();
			int viewRow = table.rowAtPoint(e.getPoint());
			int viewColumn = table.columnAtPoint(e.getPoint());
			int modelRow = table.convertRowIndexToModel(viewRow);
			int modelColumn = table.convertColumnIndexToModel(viewColumn);

			UserACEditionTableModel tModel = (UserACEditionTableModel) table.getModel();
			ACModuleDescription moduleDescription = tModel.getModuleDescription(modelColumn);
			if (moduleDescription == null)
				return;
			AdminConsoleModule module = moduleDescription.getModule();

			int currentMarkedCount = tModel.getCurrentMarkedCellCount(module.getValue());
			Integer maxNonExpiringCount = moduleDescription.getMaxNonExpiringUserCount();
			Integer usedUserCount = moduleDescription.getUsedUserCount();
			if (tModel.isCellEditable(modelRow, modelColumn)){
				if (tModel.isCellMarkedForExpiry(modelRow, modelColumn)){
					tModel.setCellMarkedForExpiry(modelRow, modelColumn, false);
					tModel.fireTableCellUpdated(modelRow, modelColumn);
				} else if ((Boolean) tModel.getValueAt(modelRow, modelColumn)
				        && usedUserCount - maxNonExpiringCount > currentMarkedCount){
					tModel.setCellMarkedForExpiry(modelRow, modelColumn, true);
					tModel.fireTableCellUpdated(modelRow, modelColumn);
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e){

	}

	@Override
	public void mouseExited(MouseEvent e){

	}

	@Override
	public void mousePressed(MouseEvent e){

	}

	@Override
	public void mouseReleased(MouseEvent e){

	}

}
