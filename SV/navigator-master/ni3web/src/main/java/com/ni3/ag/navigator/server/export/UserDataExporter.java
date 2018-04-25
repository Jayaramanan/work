/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.export;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.util.MemoryWatcher;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.services.ExportService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.User;

public class UserDataExporter{
	private static final Logger log = Logger.getLogger(UserDataExporter.class);
	private static final int START_COLUMN = 1;
	private static final int START_ROW = 3;
	private static final String[] ROW_LABELS = new String[] { "Attribute name", "Database column name", "Datatype" };
	private static final DefaultNi3ExcelStyleSheet styleSheet = DefaultNi3ExcelStyleSheet.getInstance();
	private static final int MAX_ROWS_PER_SHEET = 60000;
	private static final DateFormat STORE_DATE_FORMAT = new SimpleDateFormat(DataType.DB_DATE_FORMAT);

	private Integer currentGroupId;
	private WritableWorkbook currentBook;
	private int currentSheetIndex;
	private int totalRowCount;
	private Map<Integer, String> idMap;
	private DateFormat dateFormat;
	private ExportService exportService;

	public UserDataExporter(){
		this(NSpringFactory.getInstance().getExportService());
	}

	UserDataExporter(ExportService exportService){
		this.idMap = new HashMap<Integer, String>();
		this.exportService = exportService;
	}

	public void performAction(String nodes, String edges, String dateFormat, OutputStream os) throws IOException,
	        ACException{
		List<ObjectDefinition> odList = getObjectDefinitions(nodes == null ? "" : nodes, edges == null ? "" : edges);

		prepareExport(os, dateFormat);

		exportData(odList, nodes, edges);
	}

	private void exportData(List<ObjectDefinition> objectDefinitions, String nodeIds, String edgeIds) throws ACException{
		try{
			for (ObjectDefinition od : objectDefinitions){
				boolean isNode = od.isNode();
				if (!isNode && idMap.isEmpty()){
					idMap = exportService.getSrcIdMap(objectDefinitions);
				}
				serializeDataOfObject(od, isNode ? nodeIds : edgeIds);
			}
			if (currentBook.getNumberOfSheets() == 0){
				log.error("No data to export");
				throw new ACException(TextID.MsgNoDataFoundForExport);
			}
			cleanUpExport();
		} catch (OutOfMemoryError ex){
			log.error(ex);
			throw new ACException(TextID.MsgOutOfMemory);
		} catch (ACException e){
			log.error("User data export error: " + (!e.getErrors().isEmpty() ? e.getErrors().get(0) : ""));
			throw e;
		} catch (Exception e){
			log.error(e.getMessage(), e);
			throw new ACException(TextID.MsgFailedToMakeUserDataExport);
		}
	}

	private void cleanUpExport() throws IOException, WriteException{
		currentBook.write();
		currentBook.close();
		currentBook = null;
		currentGroupId = null;
		dateFormat = null;
		idMap.clear();
		idMap = null;
		new MemoryWatcher(getClass().getSimpleName()).dump("cleanUpExport");
		log.info("Exported row count: " + totalRowCount);
	}

	private void prepareExport(OutputStream os, String format) throws IOException{
		totalRowCount = 0;
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User user = localStorage.getCurrentUser();

		currentGroupId = exportService.getGroupId(user.getId());
		currentBook = Workbook.createWorkbook(os);
		currentSheetIndex = 0;
		dateFormat = new SimpleDateFormat((format == null || format.isEmpty()) ? DataType.DISPLAY_DATE_FORMAT : format);
	}

	private void fillSheetData(WritableSheet sheet, List<Object[]> data, List<ObjectAttribute> attributes)
	        throws RowsExceededException, WriteException{
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
		WritableCell cell = null;
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

	Object formatNumber(Object number){
		Object result = number;
		if (number instanceof Long){
			result = ((Long) number).intValue();
		} else if (number instanceof BigInteger){
			result = ((BigInteger) number).intValue();
		} else if (number instanceof Short){
			result = ((Short) number).intValue();
		} else if (number instanceof BigDecimal){
			result = ((BigDecimal) number).doubleValue();
		} else if (number instanceof Float){
			result = Double.valueOf(number.toString());
		}
		return result;
	}

	private boolean isNumericAttribute(ObjectAttribute oa){
		return !oa.isPredefined() && !oa.getIsMultivalue() && (oa.isIntDataType() || oa.isDecimalDataType());
	}

	private void serializeDataOfObject(ObjectDefinition od, String objectIds) throws RowsExceededException, WriteException{
		if (!exportService.isAvailableObject(od.getId(), currentGroupId)){
			return;
		}
		List<ObjectAttribute> attributes = exportService.getAvailableAttributes(od.getId(), currentGroupId);
		if (attributes.isEmpty()){
			return;
		}

		List<Object[]> data = null;
		boolean isNode = od.isNode();
		data = (List<Object[]>) exportService.getUserData(od, attributes, objectIds);
		if (!od.isNode())
			data = parseFromToIDs(data, attributes);
		prepareData(data, attributes);

		int sheetIndex = 1;
		int initialCount = data.size();
		totalRowCount += initialCount;
		while (data.size() > 0){
			String sheetName = od.getName();
			if (initialCount > MAX_ROWS_PER_SHEET)
				sheetName += "_" + sheetIndex++;
			WritableSheet sheet = currentBook.createSheet(sheetName, ++currentSheetIndex);
			if (isNode)
				fillSheetData(sheet, data, attributes);
			else
				fillSheetData(sheet, data, attributes);
			fillRowLabels(sheet);
		}
		data.clear();
		data = null;
	}

	private void fillRowLabels(WritableSheet sheet){
		try{
			for (int i = 0; i < ROW_LABELS.length; i++){
				Label l = new Label(0, i, ROW_LABELS[i]);
				l.setCellFormat(styleSheet.getTableHeaderStyle());
				sheet.addCell(l);
			}
			sheet.setColumnView(0, 20);
		} catch (RowsExceededException e){
			log.error(e);
		} catch (WriteException e){
			log.error(e);
		}
	}

	List<ObjectDefinition> getObjectDefinitions(String nodes, String edges){
		List<ObjectDefinition> objDefinitionList = null;
		if (!nodes.isEmpty() || !edges.isEmpty()){
			String objSql = nodes;
			if (!edges.isEmpty()){
				if (!nodes.isEmpty())
					objSql += ",";
				objSql += edges;
			}
			objDefinitionList = exportService.getObjectDefinitionsByCisObjects(objSql);
		}

		return objDefinitionList == null ? new ArrayList<ObjectDefinition>() : objDefinitionList;
	}

	String parseMultivalueDate(String multivalue, DateFormat formatter){
		if (multivalue == null || multivalue.isEmpty()){
			return "";
		}
		String str = prepareMultivalue(multivalue);
		String[] dates = str.split(";");
		String labels = "";
		for (int i = 0; i < dates.length; i++){
			String label = "";
			label = getFormattedDate(dates[i], formatter);
			labels += label;
			if (i < dates.length - 1 && !labels.isEmpty()){
				labels += ";";
			}
		}
		return labels;
	}

	String parseMultivaluePredefined(String multivalue, ObjectAttribute attr){
		if (multivalue == null || multivalue.isEmpty()){
			return "";
		}
		String str = prepareMultivalue(multivalue);
		String[] ids = str.split(";");
		String labels = "";
		for (int i = 0; i < ids.length; i++){
			String label = "";
			for (PredefinedAttribute pattr : attr.getPredefinedAttributes()){
				if (pattr.getId().toString().equals(ids[i])){
					label = pattr.getLabel();
				}
			}
			labels += label;
			if (i < ids.length - 1 && !labels.isEmpty()){
				labels += ";";
			}
		}
		return labels;
	}

	String getPredefinedLabel(Integer id, ObjectAttribute attr){
		for (PredefinedAttribute pattr : attr.getPredefinedAttributes()){
			if (pattr.getId().equals(id)){
				return pattr.getLabel();
			}
		}
		return null;
	}

	String prepareMultivalue(Object obj){
		if (obj == null)
			return null;
		String str = "" + obj;
		return str.replace("}{", ";").replace("{", "").replace("}", "");
	}

	List<Object[]> parseFromToIDs(List<Object[]> data, List<ObjectAttribute> attributes){
		for (int c = 0; c < attributes.size(); c++){
			ObjectAttribute attr = attributes.get(c);
			if (attr.getName().equalsIgnoreCase(ObjectAttribute.FROM_ID_ATTRIBUTE_NAME)
			        || attr.getName().equalsIgnoreCase(ObjectAttribute.TO_ID_ATTRIBUTE_NAME)){
				for (int r = 0; r < data.size(); r++){
					Integer nodeId = (Integer) data.get(r)[c];
					data.get(r)[c] = idMap.get(nodeId);
				}
			}
		}
		return data;
	}

	List<Object[]> prepareData(List<Object[]> data, List<ObjectAttribute> attributes){
		for (int c = 0; c < attributes.size(); c++){
			ObjectAttribute attr = attributes.get(c);

			boolean isDate = attr.isDateDataType();
			DateFormat format = null;
			if (isDate)
				format = getDateFormat(attr.getFormat());

			boolean isNumeric = isNumericAttribute(attr);

			for (int r = 0; r < data.size(); r++){
				Object obj = data.get(r)[c];
				if (obj == null)
					continue;
				String value = obj.toString();
				Object label = null;

				if (attr.getIsMultivalue()){
					if (attr.isPredefined())
						label = parseMultivaluePredefined(value, attr);
					else if (isDate)
						label = parseMultivalueDate(value, format);
					else
						label = prepareMultivalue(value);
				} else{
					if (attr.isPredefined())
						label = getPredefinedLabel(Integer.parseInt(value), attr);
					else if (isDate)
						label = getFormattedDate(value, format);
					else if (isNumeric)
						label = formatNumber(obj);
					else
						label = value;
				}

				data.get(r)[c] = label;
			}
		}
		return data;
	}

	String getFormattedDate(String date, DateFormat formatter){
		if (date == null || date.isEmpty()){
			return "";
		}
		String strDate = "";
		try{
			Date dt = STORE_DATE_FORMAT.parse(date);
			strDate = dt != null ? formatter.format(dt) : "";
		} catch (ParseException e){
			log.warn("Cannot parse date: " + date);
		}
		return strDate;
	}

	DateFormat getDateFormat(String format){
		if (format == null || format.isEmpty() || "0".equals(format) || "null".equalsIgnoreCase(format)){
			return dateFormat;
		}

		DateFormat sdf = new SimpleDateFormat(format);
		return sdf;
	}

	/**
	 * for tests
	 */
	void setIdMap(Map<Integer, String> idMap){
		this.idMap = idMap;
	}

	/**
	 * for tests
	 */
	void setDefaultDateFormat(DateFormat format){
		dateFormat = format;
	}
}
