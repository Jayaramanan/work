/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorRightPanel;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;

public class AddSetButtonListener extends ProgressActionListener{

	public AddSetButtonListener(NodeMetaphorController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		NodeMetaphorController controller = (NodeMetaphorController) getController();
		NodeMetaphorModel model = controller.getModel();
		if (model.getCurrentObjectDefinition() == null){
			return;
		}
		NodeMetaphorView view = controller.getView();
		view.clearErrors();
		view.getRightPanel().stopCellEditing();
		String setName = ACOptionPane.showInputDialog(controller.getView(),
		        Translation.get(TextID.MsgEnterNameOfNewMetaphorSet), Translation.get(TextID.MetaphorSet));
		if (setName == null || setName.length() == 0){
			return;
		}
		List<String> metaphorSets = model.getMetaphorSets();
		if (!metaphorSets.contains(setName)){
			metaphorSets.add(setName);
		}

		NodeMetaphorRightPanel rightPanel = view.getRightPanel();
		rightPanel.setMetaphorSetReferenceData(metaphorSets);
		rightPanel.getMetaphorSetCombo().setSelectedItem(setName);
	}

}
