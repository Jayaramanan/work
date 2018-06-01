/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.csv;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.server.importers.UserDataImporter;
import com.ni3.ag.adminconsole.server.importers.UserDataTable;
import com.ni3.ag.adminconsole.server.service.CSVUserDataImporter;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class CSVUserDataImporterImpl implements CSVUserDataImporter{
	private static final Logger log = Logger.getLogger(CSVUserDataImporterImpl.class);
	private static final String ID_ATTRIBUTE_NAME = "id";
	private static final int MAX_LINE_COUNT = 20000;
	private ObjectDefinitionDAO objectDefinitionDAO;
	private UserDataImporter userDataImporter;

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public void setUserDataImporter(UserDataImporter userDataImporter){
		this.userDataImporter = userDataImporter;
	}

	@Override
	public void importDataFromCSV(List<String> csvLines, Integer schemaId, Integer userId, String fileName,
			String columnSeparator, boolean recalculateFormulas) throws ACException{
		if (fileName == null || fileName.isEmpty() || csvLines == null || csvLines.size() < 2){
			log.error("File is empty:" + fileName);
			throw new ACException(TextID.MsgFileNotFound);
		}
		String fName = fileName.toLowerCase();
		if (fName.endsWith(".csv")){
			int index = fName.indexOf(".csv");
			fileName = fileName.substring(0, index);
		}

		ObjectDefinition od = objectDefinitionDAO.getObjectDefinitionByName(fileName, schemaId);
		if (od == null){
			log.error("Object definition with name '" + fileName + "' not found");
			throw new ACException(TextID.MsgObjectWithGivenNameNotFound, new String[] { fileName });
		}

		Map<String, Integer> nodeSrcIdMap = null;
		Map<String, Integer> edgeSrcIdMap = null;

		ObjectAttribute[] attributes = getAttributes(csvLines.get(1), od, columnSeparator);
		if (attributes == null || attributes.length == 0){
			log.warn("File is empty:" + fileName);
		} else{
			int start = 2;
			if (od.isEdge()){
				nodeSrcIdMap = userDataImporter.getNodeSrcIdMap(od.getSchema());
				edgeSrcIdMap = userDataImporter.getSrcIdMap(od, null);
			} else{
				nodeSrcIdMap = userDataImporter.getSrcIdMap(od, null);
			}
			int total = 0;
			List<String> next = null;
			while ((next = nextPart(csvLines, start, MAX_LINE_COUNT)) != null && !next.isEmpty()){
				UserDataTable data = getUserDataForObject(next, od, attributes, columnSeparator);
				if (data != null && !data.isEmpty()){
					userDataImporter.storeUserDataForObject(od, data, userId, nodeSrcIdMap, edgeSrcIdMap, recalculateFormulas);
					total += data.size();
					log.debug("Imported row count: " + total);
				}
				start += next.size();
			}
			log.debug("CSV user data import completed, total row count: " + total);
		}
	}

	List<String> nextPart(List<String> csvLines, int from, int maxLength){
		List<String> result = null;
		if (csvLines.size() > from + maxLength){
			int end = from + maxLength;
			if (end > 0){
				result = csvLines.subList(from, end);
			}
		} else{
			result = csvLines.subList(from, csvLines.size());
		}

		return result;
	}

	private ObjectAttribute[] getAttributes(String header, ObjectDefinition od, String columnSeparator) throws ACException{
		String[] columnNames = header.split(columnSeparator);
		ObjectAttribute[] attributes = findAttributes(od, columnNames);
		if (od.isEdge() && !checkFromToIds(od, attributes)){
			throw new ACException(TextID.MsgMandatoryFromToId);
		}
		return attributes;
	}

	private UserDataTable getUserDataForObject(List<String> lines, ObjectDefinition od, ObjectAttribute[] attributes,
			String columnSeparator) throws ACException{
		UserDataTable dataTable = new UserDataTable(od, attributes);
		for (int row = 0; row < lines.size(); row++){
			String[] data = lines.get(row).split(columnSeparator, -1);
			for (int col = 0; col < attributes.length; col++){
				ObjectAttribute attr = attributes[col];
				if (attr == null)
					continue;
			}
			dataTable.addRow(data);
		}
		log.debug("Parsed rows for object definition " + od.getName());
		return dataTable;
	}

	private ObjectAttribute[] findAttributes(ObjectDefinition od, String[] columnNames) throws ACException{
		boolean foundAttributes = false;
		ObjectAttribute[] attributes = new ObjectAttribute[columnNames.length];
		for (int i = 0; i < columnNames.length; i++){
			String columnName = columnNames[i].trim();
			if (ID_ATTRIBUTE_NAME.equalsIgnoreCase(columnName)){
				attributes[i] = null;
				log.debug("Found attribute with name id, skip it");
				continue;
			}
			boolean found = false;
			for (ObjectAttribute attr : od.getObjectAttributes()){
				if (attr.getName().equalsIgnoreCase(columnName)){
					attributes[i] = attr;
					foundAttributes = true;
					found = true;
					break;
				}
			}
			if (!found){
				log.error("attribute `" + columnName + "` not found");
				throw new ACException(TextID.MsgAttributeNotFoundForParameter, new String[] { columnName });
			}
		}

		if (!foundAttributes){
			log.error("Cannot parse file. No corresponding attributes found for object definition " + od.getName());
			throw new ACException(TextID.MsgCannotParseFile);
		}
		return attributes;
	}

	private boolean checkFromToIds(ObjectDefinition od, ObjectAttribute[] attributes){
		boolean foundFrom = false;
		boolean foundTo = false;
		for (ObjectAttribute attr : attributes){
			if (attr == null)
				continue;
			if (ObjectAttribute.FROM_ID_ATTRIBUTE_NAME.equalsIgnoreCase(attr.getName())){
				foundFrom = true;
			} else if (ObjectAttribute.TO_ID_ATTRIBUTE_NAME.equalsIgnoreCase(attr.getName())){
				foundTo = true;
			}
		}

		return foundFrom && foundTo;
	}

}
