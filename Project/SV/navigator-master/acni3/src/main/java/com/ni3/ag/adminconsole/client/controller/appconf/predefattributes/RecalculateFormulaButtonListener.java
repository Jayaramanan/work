/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeEditView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.shared.service.def.PredefinedAttributeService;
import com.ni3.ag.adminconsole.validation.ACException;

public class RecalculateFormulaButtonListener extends ProgressActionListener{

	public RecalculateFormulaButtonListener(PredefinedAttributeEditController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		PredefinedAttributeEditController controller = (PredefinedAttributeEditController) getController();
		PredefinedAttributeEditView view = (PredefinedAttributeEditView) controller.getView();
		PredefinedAttributeEditModel model = (PredefinedAttributeEditModel) controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		final ObjectAttribute currentAttribute = model.getCurrentAttribute();
		if (currentAttribute == null || !currentAttribute.isFormulaAttribute()){
			return;
		}

		if (view.isChanged()){
			view.renderErrors(Arrays.asList(new ErrorEntry(TextID.MsgDataChangedUpdateOrRefresh)));
			return;
		}

		PredefinedAttributeService service = ACSpringFactory.getInstance().getPredefinedAttributeService();
		try{
			service.calculateFormulaValue(currentAttribute.getId());
		} catch (ACException e1){
			view.renderErrors(e1.getErrors());
			return;
		}
	}
}