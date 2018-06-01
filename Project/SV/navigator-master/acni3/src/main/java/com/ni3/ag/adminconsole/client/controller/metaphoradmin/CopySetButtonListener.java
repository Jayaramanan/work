/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorRightPanel;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.MetaphorData;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;

public class CopySetButtonListener extends ProgressActionListener{

	private NodeMetaphorController controller;

	public CopySetButtonListener(NodeMetaphorController controller){
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

		String setName = ACOptionPane.showInputDialog(controller.getView(),
		        Translation.get(TextID.MsgEnterNameOfNewMetaphorSet), null);
		if (setName == null || setName.length() == 0 || currentMetaphorSet.equals(setName)){
			return;
		}

		List<String> metaphorSets = model.getMetaphorSets();
		if (!metaphorSets.contains(setName)){
			metaphorSets.add(setName);
		}
		copyMetaphorSet(currentMetaphorSet, setName);

		NodeMetaphorRightPanel rightPanel = view.getRightPanel();
		rightPanel.setMetaphorSetReferenceData(metaphorSets);
		rightPanel.getMetaphorSetCombo().setSelectedItem(setName);
	}

	public void copyMetaphorSet(String setFrom, String setTo){
		NodeMetaphorModel model = controller.getModel();
		List<Metaphor> allMetaphors = model.getCurrentObjectDefinition().getMetaphors();
		int size = allMetaphors.size();
		for (int i = 0; i < size; i++){
			Metaphor metaphor = allMetaphors.get(i);
			if (metaphor.getMetaphorSet() != null && metaphor.getMetaphorSet().equals(setFrom)){
				Metaphor newMetaphor = new Metaphor();
				newMetaphor.setIcon(metaphor.getIcon());
				newMetaphor.setIconName(metaphor.getIconName());
				newMetaphor.setMetaphorSet(setTo);
				newMetaphor.setPriority(metaphor.getPriority());
				newMetaphor.setDescription(metaphor.getDescription());
				newMetaphor.setObjectDefinition(metaphor.getObjectDefinition());
				newMetaphor.setSchema(metaphor.getSchema());
				newMetaphor.setMetaphorData(new ArrayList<MetaphorData>());
				for (MetaphorData oldMD : metaphor.getMetaphorData()){
					MetaphorData newMD = new MetaphorData(newMetaphor, oldMD.getAttribute(), oldMD.getData());
					newMetaphor.getMetaphorData().add(newMD);
				}
				allMetaphors.add(newMetaphor);
			}
		}
		if (controller.getView() != null)
			controller.getView().setMetaphorSetCopied(true);
	}
}
