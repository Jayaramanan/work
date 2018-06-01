/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.NodeMetaphorService;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;


public class UpdateNodeMetaphorActionListener extends ProgressActionListener{

	private NodeMetaphorController controller;
	private ACValidationRule rule;

	private Logger log = Logger.getLogger(UpdateNodeMetaphorActionListener.class);

	public UpdateNodeMetaphorActionListener(NodeMetaphorController controller){
		super(controller);
		this.controller = controller;
		rule = (ACValidationRule) ACSpringFactory.getInstance().getBean("nodeMetaphorUniqValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		NodeMetaphorView view = controller.getView();
		NodeMetaphorModel model = controller.getModel();

		Metaphor selectedMetaphor = view.getRightPanel().getSelectedNodeMetaphor();
		String selectedMetaphorSet = model.getCurrentMetaphorSet();

		if (!save()){
			return;
		}

        controller.reloadCurrent();
        controller.reloadData();

		// if (selectedMetaphorSet != null){
		view.getRightPanel().getMetaphorSetCombo().setSelectedItem(selectedMetaphorSet);
		// }
		if (selectedMetaphor != null){
			view.getRightPanel().setActiveTableRow(selectedMetaphor);
		}
		view.resetEditedFields();
	}

	public boolean save(){
		log.debug("action performed");
		NodeMetaphorView view = controller.getView();
		NodeMetaphorModel model = controller.getModel();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return true;
		}

		view.getRightPanel().stopCellEditing();
		view.clearErrors();

		ObjectDefinition currentObject = model.getCurrentObjectDefinition();
		if (currentObject == null){
			return true;
		}

		if (!rule.performCheck(model)){
			view.renderErrors(rule.getErrorEntries());
			return false;
		}

		NodeMetaphorService service = ACSpringFactory.getInstance().getNodeMetaphorService();
		service.updateObject(currentObject);

		if (view.isChanged()){
			SchemaAdminService s = ACSpringFactory.getInstance().getSchemaAdminService();
			s.setInvalidationRequired(DataGroup.Metaphors, true);
			MainPanel2.setInvalidationNeeded(TextID.Metaphors, true);
		}

		view.setMetaphorSetCopied(false);
		view.setMetaphorSetDeleted(false);

		return true;
	}

}
