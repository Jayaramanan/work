/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.LineStyle;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.LineWeightDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectConnectionDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;
import com.ni3.ag.adminconsole.shared.service.def.ObjectsConnectionsService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ConnectionImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(ConnectionImporter.class);
	private ObjectConnectionDAO objectConnectionDAO;
	private LineWeightDAO lineWeightDAO;
	private ObjectsConnectionsService objectsConnectionsService;

	private ACValidationRule objectConnectionImportValidationRule;

	public void setObjectConnectionImportValidationRule(ACValidationRule objectConnectionImportValidationRule){
		this.objectConnectionImportValidationRule = objectConnectionImportValidationRule;
	}

	public ACValidationRule getObjectConnectionImportValidationRule(){
		return objectConnectionImportValidationRule;
	}

	public void setObjectsConnectionsService(ObjectsConnectionsService objectsConnectionsService){
		this.objectsConnectionsService = objectsConnectionsService;
	}

	private ObjectConnection importConnection(Node typeAttr, Node fromObjectAttr, Node toObjectAttr, Node lineStyleAttr,
			Node lineColorAttr, Node lineWeightAttr, Node fromScoreAttr, Node toScoreAttr, Node hierarchicalAttr,
			Schema schema, ObjectDefinition object) throws ACException{

		ObjectAttribute oa = getObjectAttributeByName(object, ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME);
		if (oa == null)
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
					"No `Connection Type` attribute found for object `" + object.getName() + "`" });
		List<PredefinedAttribute> predefAttrs = oa.getPredefinedAttributes();
		PredefinedAttribute connType = null;
		for (PredefinedAttribute predefAttr : predefAttrs){
			if (predefAttr.getLabel().equalsIgnoreCase(typeAttr.getTextContent())){
				connType = predefAttr;
				break;
			}
		}
		if (connType == null)
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
					"No predefined attribute `" + typeAttr.getTextContent() + "` found for `Connection Type`" });

		ObjectDefinition fromObjDef = getObjectDefinitionByName(schema, fromObjectAttr.getTextContent());
		if (fromObjDef == null)
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
					"No object `" + fromObjectAttr.getTextContent() + "` found in schema `" + schema.getName() + "`" });

		ObjectDefinition toObjDef = getObjectDefinitionByName(schema, toObjectAttr.getTextContent());
		if (toObjDef == null)
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
					"No object `" + toObjectAttr.getTextContent() + "` found in schema `" + schema.getName() + "`" });

		List<ObjectConnection> ocList = objectConnectionDAO.getConnectionsByConnectionType(connType);
		ObjectConnection update = null;
		for (int i = 0; ocList != null && i < ocList.size(); i++){
			ObjectConnection oc = ocList.get(i);
			if (oc.getFromObject().equals(fromObjDef) && oc.getToObject().equals(toObjDef)){
				update = oc;
				break;
			}
		}
		if (update == null){
			update = new ObjectConnection();
			update.setFromObject(fromObjDef);
			update.setToObject(toObjDef);
			update.setConnectionType(connType);
			if (object.getObjectConnections() == null)
				object.setObjectConnections(new ArrayList<ObjectConnection>());
			object.getObjectConnections().add(update);
		}

		if (lineColorAttr != null){
			update.setRgb(lineColorAttr.getTextContent());
		}

		if (lineStyleAttr != null){
			LineStyle style = LineStyle.fromLabel(lineStyleAttr.getTextContent());
			update.setLineStyle(style);
		}

		if (lineWeightAttr != null){
			LineWeight weight = lineWeightDAO.getLineWeightByName(lineWeightAttr.getTextContent());
			if (weight == null)
				weight = lineWeightDAO.getDefaultLineWeight();
			update.setLineWeight(weight);
		}

		if (hierarchicalAttr != null){
			update.setHierarchical(Boolean.valueOf(hierarchicalAttr.getTextContent()));
			if (update.isHierarchical()){
				objectsConnectionsService.updateHierarchiesSetting(update);
			}
		}

		update.setObject(object);

		return update;
	}

	public void setObjectConnectionDAO(ObjectConnectionDAO objectConnectionDAO){
		this.objectConnectionDAO = objectConnectionDAO;
	}

	public void setLineWeightDAO(LineWeightDAO lineWeightDAO){
		this.lineWeightDAO = lineWeightDAO;
	}

	@Override
	public Object getObjectFromXML(Node node){
		NamedNodeMap attrs = node.getAttributes();
		Node typeAttr = attrs.getNamedItem("type");

		log.debug("importing connection `" + typeAttr.getTextContent() + "`");
		Node fromObjectAttr = attrs.getNamedItem("fromObject");
		Node toObjectAttr = attrs.getNamedItem("toObject");
		Node lineStyleAttr = attrs.getNamedItem("lineStyle");
		Node lineColorAttr = attrs.getNamedItem("lineColor");
		Node lineWeightAttr = attrs.getNamedItem("lineWeight");
		Node fromScoreAttr = attrs.getNamedItem("fromScore");
		Node toScoreAttr = attrs.getNamedItem("toScore");
		Node hierarchicalAttr = attrs.getNamedItem("hierarchical");

		Node connectionsList = node.getParentNode();
		Node objectDefinition = connectionsList.getParentNode();
		String objectName = objectDefinition.getAttributes().getNamedItem("name").getTextContent();

		Schema schema = (Schema) parent;
		ObjectDefinition object = getObjectDefinitionByName(schema, objectName);
		ObjectConnection update = null;
		try{
			update = importConnection(typeAttr, fromObjectAttr, toObjectAttr, lineStyleAttr, lineColorAttr, lineWeightAttr,
					fromScoreAttr, toScoreAttr, hierarchicalAttr, schema, object);
			if (object.getObjectConnections() == null)
				object.setObjectConnections(new ArrayList<ObjectConnection>());
			object.getObjectConnections().add(update);
		} catch (ACException e){
			errorContainer.addAllErrors(e.getErrors());
		}
		return update;
	}

	@Override
	protected void persist(Object o){
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		if (o != null){
			NamedNodeMap attrs = node.getAttributes();
			Node lineStyleAttr = attrs.getNamedItem("lineStyle");
			Node lineWeightAttr = attrs.getNamedItem("lineWeight");

			ObjectConnection update = (ObjectConnection) o;

			if (lineStyleAttr != null && lineStyleAttr.getTextContent() != null && update.getLineStyle() == null){
				errorContainer.addError(TextID.MsgWrongInputFormatForService, new String[] { "0",
						"invalid line style: `" + lineStyleAttr.getTextContent() + "`" });
			} else if (lineWeightAttr != null && lineWeightAttr.getTextContent() != null && update.getLineWeight() == null){
				errorContainer.addError(TextID.MsgWrongInputFormatForService, new String[] { "0",
						"invalid line weight: `" + lineWeightAttr.getTextContent() + "`" });
			} else{
				ObjectConnectionModel model = new ObjectConnectionModel();
				ObjectDefinition od = update.getObject();
				model.setCurrentObject(od);
				objectConnectionImportValidationRule.performCheck(model);
			}

			errorContainer.addAllErrors(objectConnectionImportValidationRule.getErrorEntries());
		}
		return errorContainer.getErrors().isEmpty();
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "type", "fromObject", "toObject" };
	}

}
