/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorRightPanel;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;

public class DeleteSetButtonListener extends ProgressActionListener{

	private NodeMetaphorController controller;

	public DeleteSetButtonListener(NodeMetaphorController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		NodeMetaphorModel model = controller.getModel();
		String currentMetaphorSet = model.getCurrentMetaphorSet();
		if (model.getCurrentObjectDefinition() == null || currentMetaphorSet == null){
			return;
		}
		NodeMetaphorView view = controller.getView();
		view.clearErrors();
		view.getRightPanel().stopCellEditing();

		List<String> metaphorSets = model.getMetaphorSets();
		metaphorSets.remove(currentMetaphorSet);
		deleteMetaphorSet(currentMetaphorSet);

		NodeMetaphorRightPanel rightPanel = view.getRightPanel();
		rightPanel.setMetaphorSetReferenceData(metaphorSets);
		controller.getView().setMetaphorSetDeleted(true);
	}

	public void deleteMetaphorSet(String setName){
		NodeMetaphorModel model = controller.getModel();
		List<Metaphor> allMetaphors = model.getCurrentObjectDefinition().getMetaphors();
		for (Metaphor metaphor : model.getCurrentMetaphors()){
			if (metaphor.getMetaphorSet() != null && metaphor.getMetaphorSet().equals(setName)){
				allMetaphors.remove(metaphor);
			}
		}

	}
}
