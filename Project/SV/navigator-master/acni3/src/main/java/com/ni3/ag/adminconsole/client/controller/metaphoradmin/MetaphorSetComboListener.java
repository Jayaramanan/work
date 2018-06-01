/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorTableModel;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;

public class MetaphorSetComboListener implements ActionListener{

	private NodeMetaphorController controller;

	public MetaphorSetComboListener(NodeMetaphorController controller){
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (controller.getModel().getCurrentObjectDefinition() == null){
			return;
		}
		NodeMetaphorView view = controller.getView();
		view.clearErrors();
		view.getRightPanel().stopCellEditing();

		JComboBox source = (JComboBox) e.getSource();

		String metaphorSet = (String) source.getSelectedItem();
		refreshFilteredData(metaphorSet);
		view.resetEditedFields();
	}

	public void refreshFilteredData(String metaphorSet){
		NodeMetaphorModel model = controller.getModel();
		List<Metaphor> allMetaphors = model.getCurrentObjectDefinition().getMetaphors();
		if (metaphorSet == null || metaphorSet.length() == 0){
			model.setCurrentMetaphorSet(null);
			model.setCurrentMetaphors(allMetaphors);
		} else{
			List<Metaphor> filteredMetaphors = new ArrayList<Metaphor>();
			for (Metaphor metaphor : allMetaphors){
				if (metaphor.getMetaphorSet() != null && metaphor.getMetaphorSet().equals(metaphorSet)){
					filteredMetaphors.add(metaphor);
				}
			}
			model.setCurrentMetaphorSet(metaphorSet);
			model.setCurrentMetaphors(filteredMetaphors);
		}

		NodeMetaphorTableModel tableModel = controller.getView().getRightPanel().getTableModel();
		tableModel.setData(model.getCurrentMetaphors());
		controller.getView().getRightPanel().refreshTable();
	}
}
