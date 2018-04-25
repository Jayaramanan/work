/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.validation.ACException;

public class PredefinedAttributeExporter extends XMLSchemaExporter<Element, PredefinedAttribute>{
	private final static Logger log = Logger.getLogger(PredefinedAttributeExporter.class);

	@Override
	protected void makeObjectExport(Element target, PredefinedAttribute predef) throws ACException{
		log.debug("exporting predefined attribute `" + predef.getLabel() + "`");

		Element predefElem = document.createElement("predefined");
		predefElem.setAttribute("value", predef.getValue());
		predefElem.setAttribute("label", predef.getLabel());
		if (predef.getSort() != null)
			predefElem.setAttribute("sort", predef.getSort().toString());
		predefElem.setAttribute("translation", predef.getTranslation());
		predefElem.setAttribute("toUse", predef.getToUse() ? "1" : "0");
		predefElem.setAttribute("halocolor", predef.getHaloColor());
		if (predef.getParent() != null){
			PredefinedAttribute parent = predef.getParent();
			predefElem.setAttribute("parentValue", parent.getValue());
			predefElem.setAttribute("parentLabel", parent.getLabel());
			predefElem.setAttribute("parentAttribute", parent.getObjectAttribute().getName());
		}
		target.appendChild(predefElem);

	}

}
