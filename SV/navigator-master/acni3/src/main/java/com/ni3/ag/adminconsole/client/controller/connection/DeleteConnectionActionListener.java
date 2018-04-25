/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.connection;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.connection.ObjectConnectionView;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class DeleteConnectionActionListener extends ProgressActionListener{

	private static final Logger log = Logger.getLogger(DeleteConnectionActionListener.class);

	public DeleteConnectionActionListener(ObjectConnectionController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		log.debug("action performed");
		ObjectConnectionController controller = (ObjectConnectionController) getController();

		ObjectConnectionView view = controller.getView();
		view.stopCellEditing();
		view.renderErrors(new ArrayList<ErrorEntry>());
		int row = view.getSelectedRowIndex();
		if (row < 0){
			return;
		}

		ObjectConnection connToDelete = view.getTableModel().getSelectedConnection(row);
		controller.deleteConnection(connToDelete);
	}

}
