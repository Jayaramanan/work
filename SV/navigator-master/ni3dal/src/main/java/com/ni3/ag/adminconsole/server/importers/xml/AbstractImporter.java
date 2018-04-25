/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ErrorContainerImpl;

public abstract class AbstractImporter{

	private Schema currentSchema;
	private Group currentGroup;
	private List<Object[]> processedNodes;
	protected List<Object> objects;
	protected ErrorContainerImpl errorContainer;
	protected Object parent;

	abstract public String[] getMandatoryXMLAttributes();

	abstract public Object getObjectFromXML(Node node);

	abstract protected boolean validateObject(Object o, Node node);

	abstract protected void persist(Object o);

	public ErrorContainer importData(NodeList nodeList){
		return importData(nodeList, false);
	}

	public ErrorContainer importData(NodeList nodeList, boolean persist){
		errorContainer = new ErrorContainerImpl();
		objects = new ArrayList<Object>();
		processedNodes = new ArrayList<Object[]>();
		boolean ok = true;
		for (int i = 0; ok && i < nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			ok = validateXML(node);
			if (!ok)
				break;
			Object o = getObjectFromXML(node);
			if (o != null){
				ok = validateObject(o, node);
				if (ok && errorContainer.getErrors().isEmpty()){
					objects.add(o);
					processedNodes.add(new Object[] { node, o });
				}
				if (persist)
					persist(o);
			}
		}

		return errorContainer;
	}

	private boolean validateXML(Node node){
		String[] mandatoryProps = getMandatoryXMLAttributes();
		NamedNodeMap attrs = node.getAttributes();
		for (int i = 0; i < mandatoryProps.length; i++){
			Node attr = attrs.getNamedItem(mandatoryProps[i]);
			if (attr == null)
				errorContainer
						.addError(TextID.MsgWrongInputFormatForService, new String[] {
								"0",
								"no mandatory attribute `" + mandatoryProps[i] + "` found inside <" + node.getNodeName()
										+ "> tag" });
		}

		return errorContainer.getErrors().isEmpty();
	}

	private ErrorContainer importData(NodeList nodeList, Object o){
		this.parent = o;
		return importData(nodeList);
	}

	protected Map<String, AbstractImporter> getTagToEmbeddedImporterMapping(){
		return new HashMap<String, AbstractImporter>();
	}

	public ErrorContainer processEmbeddedImporters(){
		Map<String, AbstractImporter> map = getTagToEmbeddedImporterMapping();
		ErrorContainerImpl ret = new ErrorContainerImpl();
		for (Iterator<String> it = map.keySet().iterator(); ret.getErrors().isEmpty() && it.hasNext();){
			String tag = it.next();
			for (Object[] obj : processedNodes){
				NodeList nodeList = ((Element) obj[0]).getElementsByTagName(tag.replace("_", ""));
				AbstractImporter dependentImporter = map.get(tag);
				dependentImporter.setCurrentGroup(getCurrentGroup());
				dependentImporter.setCurrentSchema(getCurrentSchema());

				Object currentObject = obj[1];
				// process import
				ErrorContainer ec = dependentImporter.importData(nodeList, currentObject);

				// persist current object
				persist(currentObject);

				dependentImporter.setCurrentGroup(getCurrentGroup());

				// launch processEmbeddedImporters for dependent importer
				dependentImporter.processEmbeddedImporters();

				ret.addAllErrors(ec.getErrors());
			}
		}
		return ret;
	}

	public Schema getCurrentSchema(){
		return currentSchema;
	}

	public void setCurrentSchema(Schema currentSchema){
		this.currentSchema = currentSchema;
	}

	public Group getCurrentGroup(){
		return currentGroup;
	}

	public void setCurrentGroup(Group currentGroup){
		this.currentGroup = currentGroup;
	}

	protected ObjectAttribute getObjectAttributeByName(ObjectDefinition object, String oaName){
		if (object.getObjectAttributes() == null)
			return null;
		for (ObjectAttribute attr : object.getObjectAttributes()){
			if (attr.getName().equals(oaName))
				return attr;
		}
		return null;
	}

	protected ObjectDefinition getObjectDefinitionByName(Schema schema, String objectName){
		if (schema.getObjectDefinitions() == null){
			return null;
		}
		for (ObjectDefinition od : schema.getObjectDefinitions()){
			if (od.getName().equals(objectName))
				return od;
		}
		return null;
	}

	protected PredefinedAttribute getPredefinedAttributeByValueAndLabel(ObjectAttribute oa, String value, String label){
		if (oa.getPredefinedAttributes() == null)
			return null;
		for (PredefinedAttribute pa : oa.getPredefinedAttributes()){
			if (pa.getValue().equals(value) && pa.getLabel().equals(label))
				return pa;
		}
		return null;
	}
}
