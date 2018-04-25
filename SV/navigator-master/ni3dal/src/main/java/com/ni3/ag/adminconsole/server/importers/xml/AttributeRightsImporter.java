/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.server.dao.AttributeGroupDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.AttributeGroupModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AttributeRightsImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(AttributeRightsImporter.class);

	private AttributeGroupDAO attributeGroupDAO;
	private ACValidationRule attributeGroupValidationRule;
	private AbstractImporter predefinedRightsImporter;

	protected Map<String, AbstractImporter> getTagToEmbeddedImporterMapping(){
		Map<String, AbstractImporter> map = new HashMap<String, AbstractImporter>();
		map.put("predefinedAttributeRight", predefinedRightsImporter);
		return map;
	}

	public void setPredefinedRightsImporter(AbstractImporter predefinedRightsImporter){
		this.predefinedRightsImporter = predefinedRightsImporter;
	}

	public void setAttributeGroupValidationRule(ACValidationRule attributeGroupValidationRule){
		this.attributeGroupValidationRule = attributeGroupValidationRule;
	}

	public void setAttributeGroupDAO(AttributeGroupDAO attributeGroupDAO){
		this.attributeGroupDAO = attributeGroupDAO;
	}

	@Override
	public Object getObjectFromXML(Node node){
		NamedNodeMap attrs = node.getAttributes();
		Node attrAttr = attrs.getNamedItem("name");
		log.debug("importing attribute rights for `" + attrAttr.getTextContent() + "`");

		Node canReadAttr = attrs.getNamedItem("canRead");
		Node canUpdateAttr = attrs.getNamedItem("editingUnlocked");
		Node canUpdateLockedAttr = attrs.getNamedItem("editingLocked");

		Group group = getCurrentGroup();

		ObjectDefinition od = ((ObjectGroup) parent).getObject();

		ObjectAttribute attr = getObjectAttributeByName(od, attrAttr.getTextContent());
		if (attr == null){
			errorContainer.addError(TextID.MsgWrongInputFormatForService, new String[] { "0",
			        "no attribute `" + attrAttr.getTextContent() + "` found for object '" + od.getName() + "'" });
			return null;
		}

		AttributeGroup update = getAttributeGroup(attr, group);
		if (update == null){
			update = new AttributeGroup(attr, group);
			if (attr.getAttributeGroups() == null)
				attr.setAttributeGroups(new ArrayList<AttributeGroup>());
			attr.getAttributeGroups().add(update);
		}

		update.setCanRead(getBooleanValue(canReadAttr));
		update.setEditingOption(getEditingValue(canUpdateAttr));
		update.setEditingOptionLocked(getEditingValue(canUpdateLockedAttr));

		return update;
	}

	private Boolean getBooleanValue(Node node){
		return node != null && node.getTextContent() != null && "1".equals(node.getTextContent());
	}

	private EditingOption getEditingValue(Node node){
		EditingOption opt = null;
		if (node != null && node.getTextContent() != null){
			opt = EditingOption.valueOf(node.getTextContent());
		}
		return opt;
	}

	private AttributeGroup getAttributeGroup(ObjectAttribute attr, Group group){
		if (group.getAttributeGroups() == null)
			return null;
		for (AttributeGroup sg : group.getAttributeGroups()){
			if (sg.getObjectAttribute().equals(attr))
				return sg;
		}
		return null;
	}

	@Override
	protected void persist(Object o){
		attributeGroupDAO.updateAttributeGroup((AttributeGroup) o);
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		NamedNodeMap attrs = node.getAttributes();
		Node attrAttr = attrs.getNamedItem("name");

		AttributeGroupModel model = new AttributeGroupModel();
		model.setAttributeName(attrAttr.getTextContent());
		model.setAttributeGroup((AttributeGroup) o);
		attributeGroupValidationRule.performCheck(model);
		errorContainer.addAllErrors(attributeGroupValidationRule.getErrorEntries());
		return errorContainer.getErrors().isEmpty();
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "name" };
	}
}
