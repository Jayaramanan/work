/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorRightPanel;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;

public class CancelNodeMetaphorActionListener extends ProgressActionListener{

	public CancelNodeMetaphorActionListener(NodeMetaphorController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		NodeMetaphorController controller = (NodeMetaphorController) getController();
		NodeMetaphorView view = controller.getView();
		NodeMetaphorModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		NodeMetaphorRightPanel rightPanel = view.getRightPanel();

		Metaphor selectedMetaphor = rightPanel.getSelectedNodeMetaphor();

		String selectedMetaphorSet = model.getCurrentMetaphorSet();
		view.getRightPanel().stopCellEditing();
		view.clearErrors();
		controller.reloadCurrent();
		controller.reloadData();

		if (selectedMetaphorSet != null){
			rightPanel.getMetaphorSetCombo().setSelectedItem(selectedMetaphorSet);
		}
		if (selectedMetaphor != null){
			rightPanel.setActiveTableRow(selectedMetaphor);
		}

		view.resetEditedFields();
	}

}
