/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.mock;

import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.server.importers.xml.AbstractImporter;

public class ImporterMock extends AbstractImporter{

	@Override
	public Object getObjectFromXML(Node node){
		return new Object();
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
