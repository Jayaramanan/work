/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class PredefinedRightsImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(PredefinedRightsImporter.class);

	@Override
	public Object getObjectFromXML(Node node){
		NamedNodeMap attrs = node.getAttributes();
		Node predefLabel = attrs.getNamedItem("label");
		Node canReadAttr = attrs.getNamedItem("canRead");
		AttributeGroup ag = (AttributeGroup) parent;
		String label = predefLabel.getTextContent();
		log.debug("importing predefined attribute rights for predefined attribute `" + label + "`");
		ObjectAttribute oa = ag.getObjectAttribute();
		PredefinedAttribute pa = getPredefinedAttributeByLabel(oa, label);
		if (pa == null){
			errorContainer.addError(TextID.MsgNoPredefinedAttribute, new String[] { oa.getLabel() + "=>" + label });
			return null;
		}
		if (pa.getPredefAttributeGroups() == null)
			pa.setPredefAttributeGroups(new ArrayList<GroupPrefilter>());

		List<GroupPrefilter> gpList = pa.getPredefAttributeGroups();
		GroupPrefilter groupPrefilter = null;
		for (int i = 0; gpList != null && i < gpList.size(); i++){
			GroupPrefilter gp = gpList.get(i);
			if (gp.getGroup().equals(ag.getGroup())){
				groupPrefilter = gp;
				break;
			}
		}
		String canReadStr = canReadAttr.getTextContent();
		boolean canRead = !"0".equals(canReadStr);
		if (canRead && groupPrefilter != null){
			pa.getPredefAttributeGroups().remove(groupPrefilter);
		} else if (!canRead && groupPrefilter == null){
			groupPrefilter = new GroupPrefilter(ag.getGroup(), pa);
			pa.getPredefAttributeGroups().add(groupPrefilter);
		}

		return groupPrefilter;
	}

	private PredefinedAttribute getPredefinedAttributeByLabel(ObjectAttribute oa, String value){
		if (oa.getPredefinedAttributes() == null)
			return null;
		for (PredefinedAttribute pa : oa.getPredefinedAttributes()){
			if (pa.getLabel().equals(value))
				return pa;
		}
		return null;
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
		return new String[] { "label", "canRead" };
	}

}
