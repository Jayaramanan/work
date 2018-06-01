/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses.navigator;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.licenses.navigator.UserEditionTableModel;

public class UserEditionTableCellSelectionListener implements MouseListener{

	private NavigatorLicenseController controller;

	public UserEditionTableCellSelectionListener(NavigatorLicenseController controller){
		this.controller = controller;
	}

	@Override
	public void mouseClicked(MouseEvent e){
		if (e.getButton() == MouseEvent.BUTTON3 && e.getSource() instanceof ACTable){
			ACTable table = (ACTable) e.getSource();
			int viewRow = table.rowAtPoint(e.getPoint());
			int viewColumn = table.columnAtPoint(e.getPoint());
			int modelRow = table.convertRowIndexToModel(viewRow);
			int modelColumn = table.convertColumnIndexToModel(viewColumn);

			UserEditionTableModel tModel = (UserEditionTableModel) table.getModel();
			String moduleName = tModel.getColumnModule(modelColumn);
			int currentMarkedCount = tModel.getCurrentMarkedCellCount(moduleName);
			Integer maxMarkedCount = controller.getMaximumCellsMarkedForExpiryCount(moduleName);
			if (maxMarkedCount == null)
				maxMarkedCount = 0;
			if (tModel.isCellEditable(modelRow, modelColumn)){
				if (currentMarkedCount >= maxMarkedCount && tModel.isCellMarkedForExpiry(modelRow, modelColumn)){
					tModel.markCellForExpiry(modelRow, modelColumn);
					tModel.fireTableCellUpdated(modelRow, modelColumn);
				} else if (currentMarkedCount < maxMarkedCount){
					tModel.markCellForExpiry(modelRow, modelColumn);
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
