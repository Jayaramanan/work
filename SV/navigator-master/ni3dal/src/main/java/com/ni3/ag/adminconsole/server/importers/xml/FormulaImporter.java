/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;

public class FormulaImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(FormulaImporter.class);

	@Override
	public Object getObjectFromXML(Node node){

		ObjectAttribute oa = (ObjectAttribute) parent;
		log.debug("importing formula for attribute `" + oa.getName() + "`");
		// update
		Formula update = oa.getFormula();
		if (update == null)
			update = new Formula();

		update.setFormula(node.getTextContent());
		update.setAttribute(oa);
		oa.setFormula(update);

		return update;
	}

	@Override
	protected void persist(Object o){

	}

	@Override
	protected boolean validateObject(Object o, Node node){
		return true;
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] {};
	}

}
