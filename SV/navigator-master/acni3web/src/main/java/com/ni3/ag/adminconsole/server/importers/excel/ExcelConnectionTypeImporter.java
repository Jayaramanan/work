package com.ni3.ag.adminconsole.server.importers.excel;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.LineStyle;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.ObjectConnectionDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.shared.service.def.ObjectsConnectionsService;

public class ExcelConnectionTypeImporter{
	private static final Logger log = Logger.getLogger(ExcelConnectionTypeImporter.class);

	private static final int EDGE_NAME_INDEX = 0;
	private static final int CONNECTION_TYPE_NAME_INDEX = 1;
	private static final int FROM_OBJECT_NAME_INDEX = 2;
	private static final int TO_OBJECT_NAME_INDEX = 3;
	private static final int LINE_STYLE_INDEX = 4;
	private static final int COLOR_INDEX = 5;
	private static final int LINE_WEIGHT_INDEX = 6;
	private static final int HIERARCHICAL_INDEX = 7;

	private ObjectDefinitionDAO objectDefinitionDAO;
	private ObjectConnectionDAO objectConnectionDAO;
	private ObjectsConnectionsService objectsConnectionsService;

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public void setObjectConnectionDAO(ObjectConnectionDAO objectConnectionDAO){
		this.objectConnectionDAO = objectConnectionDAO;
	}

	public void setObjectsConnectionsService(ObjectsConnectionsService objectsConnectionsService){
		this.objectsConnectionsService = objectsConnectionsService;
	}

	public void importObject(Schema newSchema, Sheet sheet){
		try{
			for (int i = 1; i < sheet.getRows(); i++){
				String edgeName = sheet.getCell(EDGE_NAME_INDEX, i).getContents();
				ObjectDefinition edge = getObject(newSchema, edgeName);
				edge = objectDefinitionDAO.merge(edge);
				if (edge == null){
					log.error("Cannot find edge with name: " + edgeName);
					continue;
				}
				Map<String, PredefinedAttribute> connectionTypeMap = getConnectionTypeMap(edge);
				String connectionTypeName = sheet.getCell(CONNECTION_TYPE_NAME_INDEX, i).getContents();
				PredefinedAttribute connectionType = connectionTypeMap.get(connectionTypeName);
				if (connectionType == null){
					log.error("Error: cannot find connection type: " + connectionTypeName);
					continue;
				}
				String fromObjectName = sheet.getCell(FROM_OBJECT_NAME_INDEX, i).getContents();
				String toObjectName = sheet.getCell(TO_OBJECT_NAME_INDEX, i).getContents();
				ObjectDefinition fromObject = getObject(newSchema, fromObjectName);
				if (fromObject == null){
					log.error("Cannot find from object by name: " + fromObjectName);
					continue;
				}
				ObjectDefinition toObject = getObject(newSchema, toObjectName);
				if (toObject == null){
					log.error("Cannot find to object by name: " + toObjectName);
					continue;
				}
				int lineStyleId = Integer.parseInt(sheet.getCell(LINE_STYLE_INDEX, i).getContents());
				String color = sheet.getCell(COLOR_INDEX, i).getContents();
				int lineWeightId = Integer.parseInt(sheet.getCell(LINE_WEIGHT_INDEX, i).getContents());
				boolean hierarchical = Boolean.parseBoolean(sheet.getCell(HIERARCHICAL_INDEX, i).getContents());
				log.debug("Object: " + edgeName + " | " + edge);
				log.debug("Type: " + connectionTypeName + " | " + connectionType);
				log.debug("FromObject: " + fromObjectName + " | " + fromObject);
				log.debug("ToObject: " + toObjectName + " | " + toObject);
				log.debug("LineStyle: " + lineStyleId + " | " + LineStyle.fromInt(lineStyleId));
				log.debug("Color: " + color);
				log.debug("LineWeight: " + lineWeightId);
				log.debug("Hierarchical: " + hierarchical);

				ObjectConnection oc = new ObjectConnection();
				oc.setObject(edge);
				oc.setConnectionType(connectionType);
				oc.setFromObject(fromObject);
				oc.setToObject(toObject);
				oc.setLineStyle(LineStyle.fromInt(lineStyleId));
				oc.setRgb(color);
				LineWeight lw = new LineWeight();
				lw.setId(lineWeightId);
				oc.setLineWeight(lw);
				oc.setHierarchical(hierarchical);

				objectConnectionDAO.saveOrUpdateNoMerge(oc);
				if (oc.isHierarchical()){
					objectsConnectionsService.updateHierarchiesSetting(oc);
				}
			}
		} catch (Exception ex){
			log.error("Error importing connection types", ex);
		}
	}

	private Map<String, PredefinedAttribute> getConnectionTypeMap(ObjectDefinition edge){
		Map<String, PredefinedAttribute> connectionTypeMap = new HashMap<String, PredefinedAttribute>();
		for (ObjectAttribute oa : edge.getObjectAttributes()){
			if (!oa.getName().equals(ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME))
				continue;
			log.debug("Found connection type");
			for (PredefinedAttribute pa : oa.getPredefinedAttributes()){
				connectionTypeMap.put(pa.getLabel(), pa);
			}
			break;
		}
		if (connectionTypeMap.isEmpty())
			log.error("Either connection type attribute not found or no predefined value found in it");
		return connectionTypeMap;
	}

	private ObjectDefinition getObject(Schema newSchema, String objectName){
		for (ObjectDefinition od : newSchema.getObjectDefinitions()){
			if (od.getName().equals(objectName))
				return od;
		}
		log.error("Cannot find object with name `" + objectName + " ` in new schema");
		return null;
	}

}
