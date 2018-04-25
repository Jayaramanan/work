/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.NodeDAO;
import com.ni3.ag.adminconsole.server.exporters.DefaultNi3ExcelStyleSheet;
import com.ni3.ag.adminconsole.server.exporters.Ni3ExcelStyleSheet;
import com.ni3.ag.adminconsole.server.exporters.UserDataExporter;
import com.ni3.ag.adminconsole.server.service.XLSUserDataExporter;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.UserDataModel;
import com.ni3.ag.adminconsole.validation.ACException;

public class XLSUserDataExporterImpl implements XLSUserDataExporter{

	private static final Logger log = Logger.getLogger(XLSUserDataExporterImpl.class);
	private static final int START_COLUMN = 1;
	private static final int START_ROW = 3;
	private static final String[] ROW_LABELS = new String[] { "Attribute name", "Database column name", "Datatype" };
	private static final Ni3ExcelStyleSheet styleSheet = DefaultNi3ExcelStyleSheet.getInstance();
	private static final int MAX_ROWS_PER_SHEET = 60000;

	private UserDataExporter exporter;
	private NodeDAO nodeDAO;

	private Integer allowedCount;

	private Group currentGroup;
	private WritableWorkbook currentBook;
	private int currentSheetIndex;
	private int totalRowCount;

	public void setAllowedCount(Integer allowedCount){
		this.allowedCount = allowedCount;
	}

	public void setExporter(UserDataExporter exporter){
		this.exporter = exporter;
	}

	public void setNodeDAO(NodeDAO nodeDAO){
		this.nodeDAO = nodeDAO;
	}

	@Override
	public ByteArrayOutputStream performAction(Schema sch, User u) throws ACException{
		log.debug("start xls export for schema " + sch.getName());
		Group g = u.getGroups().get(0);
		UserDataModel model = processUserDataModel(sch, g.getObjectGroups());
		try{
			exporter.checkSrcIds(sch);
			exporter.initFormat(u);
			if (model.getEdgeCount() + model.getNodeCount() > allowedCount)
				throw new ACException(TextID.MsgUserDataTooBigForExcel);
			return exportData(sch, g);
		} catch (OutOfMemoryError er){
			log.error("Out of memory error.", er);
			cleanup();
			throw new ACException(TextID.MsgOutOfMemory);
		}
	}

	private UserDataModel processUserDataModel(Schema schema, List<ObjectGroup> objUserGroups){
		UserDataModel model = new UserDataModel();
		int nodeCount = 0;
		int edgeCount = 0;
		for (ObjectGroup oug : objUserGroups){
			ObjectDefinition od = oug.getObject();
			if (oug.isCanRead() && od.getSchema().equals(schema)){
				Integer count = nodeDAO.getRowCount(od.getTableName());
				if (od.isNode())
					nodeCount += count;
				else if (od.isEdge())
					edgeCount += count;
			}
		}
		model.setNodeCount(nodeCount);
		model.setEdgeCount(edgeCount);
		return model;
	}

	private void serializeDataOfObject(ObjectDefinition od) throws Exception{
		if (!exporter.isAvailableObject(od, currentGroup)){
			return;
		}
		Hibernate.initialize(od.getObjectAttributes());
		List<ObjectAttribute> attributes = exporter.getAvailableExportAttributes(od.getObjectAttributes(), currentGroup);
		if (attributes.isEmpty()){
			log.warn("No attributes available (with inExport=1) for object definition " + od.getName() + ", group "
			        + currentGroup.getName());
			return;
		}

		List<Object[]> data = exporter.getData(od, currentGroup, attributes);

		int sheetIndex = 1;
		int initialCount = data.size();
		totalRowCount += initialCount;
		while (data.size() > 0){
			String sheetName = od.getName();
			if (initialCount > MAX_ROWS_PER_SHEET)
				sheetName += "_" + sheetIndex++;
			WritableSheet sheet = currentBook.createSheet(sheetName, ++currentSheetIndex);
			fillSheetData(sheet, data, attributes);
			fillRowLabels(sheet);
		}
		log.debug("Added to xls file");
	}

	public ByteArrayOutputStream exportData(Schema schema, Group group) throws ACException{
		try{
			ByteArrayOutputStream bos = prepareExport(group);
			Collections.sort(schema.getObjectDefinitions(), new ObjectDefinitionSortComparator());
			for (ObjectDefinition od : schema.getObjectDefinitions()){
				serializeDataOfObject(od);
			}
			if (currentBook.getNumberOfSheets() == 0){
				log.error("No data to export");
				finishExport(false);
				throw new ACException(TextID.MsgNoDataFoundForExport);
			}
			finishExport(true);
			return bos;
		} catch (ACException e){
			log.error("User data export error: " + (!e.getErrors().isEmpty() ? e.getErrors().get(0) : ""));
			throw e;
		} catch (Exception e){
			finishExport(false);
			log.error(e.getMessage(), e);
			throw new ACException(TextID.MsgFailedToMakeUserDataExport);
		}
	}

	private void finishExport(boolean success) throws ACException{
		try{
			if (success){
				currentBook.write();
				log.info("Exported row count: " + totalRowCount);
			}
			currentBook.close();
		} catch (Exception e){
			log.error("Error writing/closing xls file", e);
			if (success)
				throw new ACException(TextID.MsgFailedToMakeUserDataExport);
		} finally{
			cleanup();
		}
	}

	private void cleanup(){
		currentBook = null;
		currentGroup = null;
	}

	private ByteArrayOutputStream prepareExport(Group group) throws IOException{
		totalRowCount = 0;
		currentGroup = group;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		WorkbookSettings wbSetting = new WorkbookSettings();
		wbSetting.setUseTemporaryFileDuringWrite(true);
		currentBook = Workbook.createWorkbook(bos, wbSetting);
		currentSheetIndex = 0;
		return bos;
	}

	private void fillSheetData(WritableSheet sheet, List<Object[]> data, List<ObjectAttribute> attributes)
	        throws WriteException{
		int currentColumn = START_COLUMN;
		int rowCount = data.size() > MAX_ROWS_PER_SHEET ? MAX_ROWS_PER_SHEET : data.size();

		for (int c = 0; c < attributes.size(); c++){
			ObjectAttribute oa = attributes.get(c);
			int width = 10;
			if (oa.getLabel().length() > width){
				width = oa.getLabel().length();
			}
			sheet.addCell(new Label(currentColumn, 0, oa.getLabel()));
			sheet.addCell(new Label(currentColumn, 1, oa.getName()));
			sheet.addCell(new Label(currentColumn, 2, oa.getDataType().getTextId().getKey()));

			for (int r = 0; r < rowCount; r++){
				Object obj = data.get(r)[c];
				WritableCell cell = fillCellData(currentColumn, r + START_ROW, obj);
				sheet.addCell(cell);
				if (obj != null && obj.toString().length() > width){
					width = obj.toString().length();
				}
			}

			sheet.setColumnView(currentColumn, width + 1);
			currentColumn++;
		}
		data.subList(0, rowCount).clear();
	}

	private WritableCell fillCellData(int col, int row, Object value){
		WritableCell cell;
		if (value instanceof Integer){
			cell = new jxl.write.Number(col, row, (Integer) value);
		} else if (value instanceof Double){
			cell = new jxl.write.Number(col, row, (Double) value);
		} else{
			String val = value != null ? value.toString() : null;
			cell = new Label(col, row, val);
		}
		return cell;
	}

	protected void fillRowLabels(WritableSheet sheet) throws Exception{
		for (int i = 0; i < ROW_LABELS.length; i++){
			Label l = new Label(0, i, ROW_LABELS[i]);
			l.setCellFormat(styleSheet.getTableHeaderStyle());
			sheet.addCell(l);
		}
		sheet.setColumnView(0, 20);
	}

	private class ObjectDefinitionSortComparator implements Comparator<ObjectDefinition>{
		@Override
		public int compare(ObjectDefinition o1, ObjectDefinition o2){
			return o1.getSort() - o2.getSort();
		}
	}

}
