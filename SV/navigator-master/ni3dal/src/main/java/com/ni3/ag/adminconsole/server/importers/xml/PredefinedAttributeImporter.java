/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class PredefinedAttributeImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(PredefinedAttributeImporter.class);

	private ACValidationRule predefAttributeValidationRule;

	public ACValidationRule getPredefAttributeValidationRule(){
		return predefAttributeValidationRule;
	}

	public void setPredefAttributeValidationRule(ACValidationRule predefAttributeValidationRule){
		this.predefAttributeValidationRule = predefAttributeValidationRule;
	}

	@Override
	public Object getObjectFromXML(Node node){
		NamedNodeMap attrs = node.getAttributes();

		String value = attrs.getNamedItem("value").getTextContent();
		String label = attrs.getNamedItem("label").getTextContent();
		log.debug("importing predefined attribute `" + label + "`");
		ObjectAttribute oa = (ObjectAttribute) parent;

		PredefinedAttribute update = getPredefinedAttributeByValueAndLabel(oa, value, label);
		if (update == null){
			update = new PredefinedAttribute();
			update.setValue(value);
			if (oa.getPredefinedAttributes() == null)
				oa.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
			oa.getPredefinedAttributes().add(update);
		}
		update.setLabel(label);
		update.setObjectAttribute(oa);

		Node n = attrs.getNamedItem("sort");
		if (n != null){
			String sortStr = n.getTextContent();
			update.setSort(Integer.parseInt(sortStr));
		}
		n = attrs.getNamedItem("toUse");
		if (n != null){
			String toUseStr = n.getTextContent();
			update.setToUse("1".equals(toUseStr));
		}
		n = attrs.getNamedItem("translation");
		if (n != null){
			String transStr = n.getTextContent();
			update.setTranslation(transStr);
		}
		n = attrs.getNamedItem("halocolor");
		if (n != null){
			String haloStr = n.getTextContent();
			update.setHaloColor(haloStr);
		}

		return update;
	}

	@Override
	protected void persist(Object o){
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		PredefinedAttribute pa = (PredefinedAttribute) o;
		PredefinedAttributeEditModel model = new PredefinedAttributeEditModel();
		ObjectAttribute oa = new ObjectAttribute();
		List<PredefinedAttribute> predefAttrs = new ArrayList<PredefinedAttribute>();
		predefAttrs.add(pa);
		oa.setPredefinedAttributes(predefAttrs);
		model.setCurrentAttribute(oa);

		predefAttributeValidationRule.performCheck(model);
		errorContainer.addAllErrors(predefAttributeValidationRule.getErrorEntries());
		return errorContainer.getErrors().isEmpty();
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "label", "value" };
	}

}
