/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.validation.ACException;

public class AttributeRightsExporter extends XMLSchemaExporter<Element, AttributeGroup>{
	private final static Logger log = Logger.getLogger(AttributeRightsExporter.class);

	@Override
	protected void makeObjectExport(Element target, AttributeGroup ag) throws ACException{
		log.debug("exporting attribute rights for `" + ag.getObjectAttribute().getName() + "`");
		Element attributeRightElem = document.createElement("attributeRight");
		attributeRightElem.setAttribute("name", ag.getObjectAttribute().getName());
		attributeRightElem.setAttribute("canRead", ag.isCanRead() ? "1" : "0");
		if (ag.getEditingOptionLocked() != null)
			attributeRightElem.setAttribute("editingLocked", ag.getEditingOptionLocked().name());
		if (ag.getEditingOption() != null)
			attributeRightElem.setAttribute("editingUnlocked", ag.getEditingOption().name());
		target.appendChild(attributeRightElem);

		List<PredefinedAttribute> paList = ag.getObjectAttribute().getPredefinedAttributes();
		if (paList != null)
			exportPredefinedRights(attributeRightElem, ag.getGroup(), paList);

	}

	private void exportPredefinedRights(Element target, Group group, List<PredefinedAttribute> paList){
		boolean[] markedPAs = new boolean[paList.size()];
		for (int i = 0; i < paList.size(); i++){
			PredefinedAttribute pa = paList.get(i);
			List<GroupPrefilter> gpList = pa.getPredefAttributeGroups();
			for (GroupPrefilter gp : gpList)
				if (group.equals(gp.getGroup())){
					makePredefinedRightElement(false, target, paList.get(i).getLabel());
					markedPAs[i] = true;
					break;
				}
		}
		for (int i = 0; i < markedPAs.length; i++)
			if (!markedPAs[i])
				makePredefinedRightElement(true, target, paList.get(i).getLabel());
	}

	private void makePredefinedRightElement(boolean canRead, Element target, String label){
		log.debug("exporting predefined attribute rights for `" + label + "`");
		Element predefRightElem = document.createElement("predefinedAttributeRight");
		predefRightElem.setAttribute("canRead", canRead ? "1" : "0");
		predefRightElem.setAttribute("label", label);
		target.appendChild(predefRightElem);
	}

}
