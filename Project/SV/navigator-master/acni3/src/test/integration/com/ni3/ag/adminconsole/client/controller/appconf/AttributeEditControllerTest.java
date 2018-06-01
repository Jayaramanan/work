/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.appconf;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.appconf.attributes.AttributeEditController;
import com.ni3.ag.adminconsole.client.model.AttributeEditModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.appconf.AttributeEditView;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.validation.ACServerValidationRule;

public class AttributeEditControllerTest extends ACTestCase{
    Logger log = Logger.getLogger(AttributeEditControllerTest.class);

    public void testIsObjectDefinitionSchema(){
        ObjectDefinition od = new ObjectDefinition();
        AttributeEditController ctrl = (AttributeEditController) ACSpringFactory.getInstance().getBean("attributeEditController");
        assertTrue(ctrl.isObjectDefinitionSchema(od));
        ObjectDefinition od2 = new ObjectDefinition();
        od2.setParentObject(od);
        assertFalse(ctrl.isObjectDefinitionSchema(od2));
    }
}