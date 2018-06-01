/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class DeleteNodeMetaphorActionListener extends ProgressActionListener{

	public DeleteNodeMetaphorActionListener(NodeMetaphorController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		NodeMetaphorController controller = (NodeMetaphorController) getController();

		NodeMetaphorView view = controller.getView();
		view.getRightPanel().stopCellEditing();
		view.renderErrors(new ArrayList<ErrorEntry>());

		DatabaseInstance dbInstance = controller.getModel().getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		int row = view.getRightPanel().getSelectedRowIndex();
		if (row < 0){
			return;
		}

		Metaphor nodeMetaphor = view.getRightPanel().getTableModel().getSelectedRowData(row);
		controller.deleteNodeMetaphor(nodeMetaphor);
	}

}
