/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.maps;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.MapJobView;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.impl.MapJobModel;
import com.ni3.ag.adminconsole.shared.service.def.MapJobService;

public class DeleteMapJobListener extends ProgressActionListener{

	public DeleteMapJobListener(MapJobController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		MapJobController controller = (MapJobController) getController();

		MapJobView view = controller.getView();
		MapJobModel model = controller.getModel();
		view.stopCellEditing();
		view.renderErrors(new ArrayList<ErrorEntry>());
		int row = view.getSelectedRowIndex();
		if (row < 0){
			return;
		}

		MapJob jobToDelete = view.getTableModel().getSelectedJob(row);
		model.setCurrentJob(jobToDelete);
		if (jobToDelete.getId() != null){
			MapJobService service = ACSpringFactory.getInstance().getMapJobService();
			ErrorContainer result = service.validateDeleteJob(jobToDelete);
			if (!result.getErrors().isEmpty()){
				view.renderErrors(result.getErrors());
				return;
			}
		}
		controller.deleteMapJob(jobToDelete);
	}
}