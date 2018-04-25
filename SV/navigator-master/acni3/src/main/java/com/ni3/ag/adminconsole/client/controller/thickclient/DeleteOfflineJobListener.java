/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.thickclient.ThickClientView;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.impl.ThickClientModel;

public class DeleteOfflineJobListener extends ProgressActionListener{

	public DeleteOfflineJobListener(ThickClientController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		ThickClientController controller = (ThickClientController) getController();

		ThickClientView view = controller.getView();
		ThickClientModel model = controller.getModel();
		view.stopCellEditing();
		view.renderErrors(new ArrayList<ErrorEntry>());
		int row = view.getSelectedRowIndex();
		if (row < 0){
			return;
		}

		OfflineJob jobToDelete = view.getTableModel().getSelectedJob(row);
		model.setCurrentJob(jobToDelete);

		controller.deleteOfflineJob(jobToDelete);
	}
}