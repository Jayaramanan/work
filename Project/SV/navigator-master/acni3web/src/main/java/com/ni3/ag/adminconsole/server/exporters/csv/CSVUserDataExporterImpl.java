/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.csv;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.exporters.UserDataExporter;
import com.ni3.ag.adminconsole.server.service.CSVUserDataExporter;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class CSVUserDataExporterImpl implements CSVUserDataExporter{
	private static final Logger log = Logger.getLogger(CSVUserDataExporterImpl.class);

	private UserDataExporter exporter;

	private String columnSeparator = "\t";
	private String lineSeparator = "\r\n";
	private int totalRowCount = 0;

	public void setExporter(UserDataExporter exporter){
		this.exporter = exporter;
	}

	@Override
	public byte[] performAction(ObjectDefinition od, User u, String columnSeparator, String lineSeparator)
			throws ACException{
		log.debug("start csv export for object definition " + od.getName());
		this.lineSeparator = lineSeparator;
		this.columnSeparator = columnSeparator;
		Group g = u.getGroups().get(0);
		try{
			exporter.checkSrcIds(od.getSchema());
			exporter.initFormat(u);
			return exportData(od, g);
		} catch (OutOfMemoryError er){
			log.error("Out of memory error.", er);
			throw new ACException(TextID.MsgOutOfMemory);
		}
	}

	public byte[] exportData(ObjectDefinition od, Group group) throws ACException{
		try{
			StringBuffer csvText = serializeDataOfObject(od, group);
			if (csvText == null){
				log.error("No data to export");
				throw new ACException(TextID.MsgNoDataFoundForExport);
			}

			byte[] result = csvText.toString().getBytes("UTF-8");
			log.info("Exported row count: " + totalRowCount);
			return result;
		} catch (OutOfMemoryError ex){
			log.error("Out of memory error while exporting data for object " + od.getName());
			throw new ACException(TextID.MsgOutOfMemory);
		} catch (ACException e){
			log.error("User data export error: " + (!e.getErrors().isEmpty() ? e.getErrors().get(0) : ""));
			throw e;
		} catch (Exception e){
			log.error(e.getMessage(), e);
			throw new ACException(TextID.MsgFailedToMakeUserDataExport);
		}
	}

	private StringBuffer serializeDataOfObject(ObjectDefinition od, Group group) throws ACException{

		if (!exporter.isAvailableObject(od, group)){
			log.warn("Object definition " + od.getName() + " is not available for group " + group.getName());
			return null;
		}
		Hibernate.initialize(od.getObjectAttributes());
		List<ObjectAttribute> attributes = exporter.getAvailableExportAttributes(od.getObjectAttributes(), group);
		if (attributes.isEmpty()){
			log.warn("No attributes available (with inExport=1) for object definition " + od.getName() + ", group "
					+ group.getName());
			return null;
		}

		List<Object[]> data = exporter.getData(od, group, attributes);
		if (data == null || data.isEmpty()){
			return null;
		}

		StringBuffer csvText = new StringBuffer();

		StringBuilder labelHeader = new StringBuilder();
		StringBuilder valueHeader = new StringBuilder();
		for (int i = 0; i < attributes.size(); i++){
			ObjectAttribute oa = attributes.get(i);
			labelHeader.append(oa.getLabel());
			valueHeader.append(oa.getName());
			if (i == attributes.size() - 1){
				labelHeader.append(lineSeparator);
				valueHeader.append(lineSeparator);
			} else{
				labelHeader.append(columnSeparator);
				valueHeader.append(columnSeparator);
			}
		}
		csvText.append(labelHeader);
		csvText.append(valueHeader);

		for (Object[] row : data){
			for (int i = 0; i < row.length; i++){
				csvText.append(row[i] == null ? "" : row[i]);
				if (i == row.length - 1){
					csvText.append(lineSeparator);
				} else{
					csvText.append(columnSeparator);
				}
			}
		}

		totalRowCount = data.size();
		log.debug("User data stored to text buffer, file length = " + csvText.length());
		return csvText;
	}

}
