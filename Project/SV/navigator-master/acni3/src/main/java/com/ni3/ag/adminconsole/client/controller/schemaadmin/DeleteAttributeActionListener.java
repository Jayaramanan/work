/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class DeleteAttributeActionListener extends ProgressActionListener{

	private ACValidationRule deleteRule;

	private final static Logger log = Logger.getLogger(DeleteAttributeActionListener.class);

	public DeleteAttributeActionListener(SchemaAdminController controller){
		super(controller);
		this.deleteRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("mandatoryAttributeDeleteValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminController controller = (SchemaAdminController) getController();
		log.debug(this + "action performed " + e.getActionCommand());
		SchemaAdminView view = controller.getView();
		SchemaAdminModel model = controller.getModel();
		view.getRightPanel().stopEditing();
		int rows[] = view.getRightPanel().getSelectedRowIndexes();
		if (rows.length == 0){
			return;
		}

		List<ObjectAttribute> attrsToDelete = view.getRightPanel().getTableModel().getSelectedAttributes(rows);
		model.setAttributesToDelete(attrsToDelete);

		if (!deleteRule.performCheck(model)){
			view.renderErrors(deleteRule.getErrorEntries());
			return;
		}
		model.setAttributesToDelete(null);

		ErrorContainer ec = null;
		if (!e.getActionCommand().equals(TextID.CascadeDelete.toString())){
			SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();

			ec = new ServerErrorContainerWrapper(schemaAdminService.validateAttributesDelete(attrsToDelete));
		}

		if (ec != null && ec.getErrors() != null && ec.getErrors().size() > 0){
			view.renderErrors(ec);
		} else{
			view.clearErrors();
			controller.deleteAttributes(attrsToDelete);
		}
	}

}
