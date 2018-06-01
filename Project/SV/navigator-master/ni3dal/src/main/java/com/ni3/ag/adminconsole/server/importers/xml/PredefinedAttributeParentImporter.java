/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.importers.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public class PredefinedAttributeParentImporter extends AbstractImporter{

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] {};
	}

	@Override
	public Object getObjectFromXML(Node attrNode){
		ObjectDefinition od = (ObjectDefinition) this.parent;
		final NodeList paNodes = attrNode.getChildNodes();
		for (int m = 0; m < paNodes.getLength(); m++){
			final Node paNode = paNodes.item(m);
			final PredefinedAttribute parent = getParentPredefined(paNode);
			if (parent != null){
				PredefinedAttribute existingParent = getPredefinedAttribute(od, parent);
				PredefinedAttribute current = getPredefinedAttribute(od, attrNode, paNode);
				if (current != null && existingParent != null){
					current.setParent(existingParent);
				}
			}
		}

		return null;
	}

	private PredefinedAttribute getPredefinedAttribute(ObjectDefinition od, PredefinedAttribute parent){
		return getPredefinedAttribute(od, parent.getObjectAttribute().getName(), parent.getValue(), parent.getLabel());
	}

	private PredefinedAttribute getPredefinedAttribute(ObjectDefinition od, Node attrNode, Node paNode){
		final NamedNodeMap aAttributes = attrNode.getAttributes();
		final String attrName = aAttributes.getNamedItem("name").getTextContent();
		final NamedNodeMap paAttributes = paNode.getAttributes();
		final String paValue = paAttributes.getNamedItem("value").getTextContent();
		final String paLabel = paAttributes.getNamedItem("label").getTextContent();
		return getPredefinedAttribute(od, attrName, paValue, paLabel);
	}

	PredefinedAttribute getPredefinedAttribute(ObjectDefinition od, String attrName, String paValue, String paLabel){
		PredefinedAttribute pa = null;
		ObjectAttribute oa = getObjectAttributeByName(od, attrName);
		if (oa != null){
			pa = getPredefinedAttributeByValueAndLabel(oa, paValue, paLabel);
		}
		return pa;
	}

	private PredefinedAttribute getParentPredefined(Node paNode){
		PredefinedAttribute parent = null;
		final NamedNodeMap attrs = paNode.getAttributes();
		Node pv = attrs.getNamedItem("parentValue");
		if (pv != null){
			String parentValue = pv.getTextContent();
			Node pl = attrs.getNamedItem("parentLabel");
			String parentLabel = pl != null ? pl.getTextContent() : null;
			Node pa = attrs.getNamedItem("parentAttribute");
			String parentAttribute = pa != null ? pa.getTextContent() : null;
			if (parentValue != null && parentLabel != null && parentAttribute != null){
				parent = new PredefinedAttribute();
				parent.setValue(parentValue);
				parent.setLabel(parentLabel);
				ObjectAttribute pAttr = new ObjectAttribute();
				pAttr.setName(parentAttribute);
				parent.setObjectAttribute(pAttr);
			}
		}
		return parent;
	}

	@Override
	protected void persist(Object o){
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		return true;
	}

}
