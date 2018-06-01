/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AddNodeMetaphorActionListener extends ProgressActionListener{

	private ACValidationRule inMetaphorRule;

	public AddNodeMetaphorActionListener(NodeMetaphorController controller){
		super(controller);
		ACSpringFactory factory = ACSpringFactory.getInstance();
		inMetaphorRule = (ACValidationRule) factory.getBean("inMetaphorAttributeRule");
	}

	@Override
	public void performAction(ActionEvent e){
		NodeMetaphorController controller = (NodeMetaphorController) getController();
		controller.getView().clearErrors();
		if (controller.getModel().getCurrentObjectDefinition() == null){
			return;
		}
		if (!inMetaphorRule.performCheck(controller.getModel())){
			controller.getView().renderErrors(inMetaphorRule.getErrorEntries());
			return;
		}

		controller.addNewNodeMetaphor();
	}

}
