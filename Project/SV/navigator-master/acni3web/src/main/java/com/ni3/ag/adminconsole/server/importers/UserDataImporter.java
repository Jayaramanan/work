/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.DeltaHeader;
import com.ni3.ag.adminconsole.domain.DeltaParam;
import com.ni3.ag.adminconsole.domain.DeltaType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.server.dao.NodeDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.formula.FormulaExecutor;
import com.ni3.ag.adminconsole.server.importers.util.ImportDataError;
import com.ni3.ag.adminconsole.server.importers.util.ImportError;
import com.ni3.ag.adminconsole.server.service.DeltaService;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class UserDataImporter{
	private static final Logger log = Logger.getLogger(UserDataImporter.class);
	protected static final String CIS_EDGES_TABLE_NAME = "cis_edges";
	protected static final String CIS_NODES_TABLE_NAME = "cis_nodes";
	private static final DateFormat STORE_DATE_FORMAT = new SimpleDateFormat(DataType.DB_DATE_FORMAT);
	private static final String VALUE_LABEL_SEPARATOR = "###";

	private DeltaService deltaService;
	private NodeDAO nodeDAO;
	private ObjectDAO objectDAO;
	private ACRoutingDataSource dataSource;

	public void setObjectDAO(ObjectDAO objectDAO){
		this.objectDAO = objectDAO;
	}

	public void setNodeDAO(NodeDAO nodeDAO){
		this.nodeDAO = nodeDAO;
	}

	public void setDataSource(ACRoutingDataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setDeltaService(DeltaService deltaService){
		this.deltaService = deltaService;
	}

	public void storeUserDataForObject(ObjectDefinition od, UserDataTable data, Integer userId,
			Map<String, Integer> nodeSrcIdMap, Map<String, Integer> edgeSrcIdMap, boolean recalculateFormulas)
			throws ACException{
		DateFormat defaultFormat = initFormat(userId);
		List<ImportError> errs = prepareData(data, nodeSrcIdMap, defaultFormat, recalculateFormulas);
		String userTable = od.getTableName();
		boolean isEdge = od.isEdge();
		String cisTableName = isEdge ? CIS_EDGES_TABLE_NAME : CIS_NODES_TABLE_NAME;
		Connection c = null;
		Statement st = null;
		final List<DeltaHeader> deltaHeaders = new ArrayList<DeltaHeader>();
		try{
			c = dataSource.getConnection();
			c.setAutoCommit(false);
			storeErrors(errs, c);
			st = c.createStatement();
			int size = data.size();
			for (int row = 0; row < size; row++){
				String sql;
				final String srcId = data.getSrcId(row);
				log.trace("rowData.getId(): " + srcId);
				Integer existingId = isEdge ? edgeSrcIdMap.get(srcId) : nodeSrcIdMap.get(srcId);
				if (existingId == null){
					Integer newId = nodeDAO.getNewNodeId();
					BigInteger newIdBig = BigInteger.valueOf(newId.longValue());

					sql = getInsertSql(userTable, data, row, newIdBig, true, 0, null);
					st.addBatch(sql);

					sql = getInsertSql(cisTableName, data, row, newIdBig, false, od.getId(), isEdge ? "edgeType"
							: "nodeType");
					st.addBatch(sql);

					sql = getObjectInsertSQL(newIdBig, od, userId);
					st.addBatch(sql);

					final DeltaType type = isEdge ? DeltaType.EDGE_CREATE : DeltaType.NODE_CREATE;
					final DeltaHeader delta = deltaService.getDeltaHeader(type, userId, newId, od, data, row);
					deltaHeaders.add(delta);
				} else{
					sql = getUpdateSql(userTable, data, row, existingId);
					if (sql != null)
						st.addBatch(sql);

					sql = getUpdateSql(cisTableName, data, row, existingId);
					if (sql != null)
						st.addBatch(sql);

					sql = getObjectUpdateSql(existingId);
					st.addBatch(sql);

					final DeltaType type = isEdge ? DeltaType.EDGE_UPDATE : DeltaType.NODE_UPDATE;
					final DeltaHeader delta = deltaService.getDeltaHeader(type, userId, existingId, od, data, row);
					deltaHeaders.add(delta);
				}

				if ((row > 0 && row % 1000 == 0) || row == size - 1){
					st.executeBatch();
					log.debug("Stored rows: " + (row + 1) + "/" + size);
					saveDeltas(deltaHeaders, c);
					deltaHeaders.clear();
					log.debug("Deltas saved");
				}
			}
			c.commit();
			log.debug("Committed");
		} catch (SQLException e){
			log.error("Error executing batch. ", e);
			try{
				c.rollback();
			} catch (SQLException e1){
				log.error(e1);
			}
			throw new ACException(TextID.MsgImportError, new String[] { parseError(e) });
		} finally{
			try{
				if (st != null){
					st.close();
				}
			} catch (SQLException e1){
				log.error(e1);
			}
			try{
				c.close();
			} catch (SQLException e1){
				log.error(e1);
			}
		}
	}

	private void saveDeltas(List<DeltaHeader> deltas, Connection c) throws SQLException{
		log.debug("Saving delta headers, count: " + deltas.size());
		PreparedStatement st = null;
		List<String> paramSqls = new ArrayList<String>();
		String sql = "INSERT INTO sys_delta_header(deltatype, status, creatorid, issync) VALUES (?, ?, ?, ?)";
		try{
			st = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (DeltaHeader delta : deltas){
				st.setInt(1, delta.getDeltaType().getValue());
				st.setInt(2, delta.getSyncStatus().getValue());
				st.setInt(3, delta.getCreator());
				st.setInt(4, 0);
				if (log.isTraceEnabled()){
					log.trace("execute: " + sql);
				}
				st.execute();
				ResultSet generatedKeys = st.getGeneratedKeys();
				generatedKeys.next();
				delta.setId(generatedKeys.getInt(1));
				List<String> pSqls = getParamSqls(delta);
				paramSqls.addAll(pSqls);
			}
		} finally{
			if (st != null){
				try{
					st.close();
				} catch (SQLException ex){
					log.error("Error closing statement", ex);
				}
			}
		}
		log.debug("Saving delta params, count: " + paramSqls.size());
		executeInBatch(paramSqls, c);
	}

	private void executeInBatch(List<String> sqls, Connection c) throws SQLException{
		Statement st = null;
		try{
			st = c.createStatement();
			for (String sql : sqls){
				st.addBatch(sql);
			}
			st.executeBatch();
		} finally{
			if (st != null){
				try{
					st.close();
				} catch (SQLException ex){
					log.error("Error closing statement", ex);
				}
			}
		}
	}

	private List<String> getParamSqls(DeltaHeader delta){
		List<String> sqls = new ArrayList<String>();
		String s = "INSERT INTO sys_delta_params( deltaid, \"name\", \"value\") VALUES (";
		for (DeltaParam param : delta.getParams()){
			String sql = s + delta.getId() + ", '" + param.getName() + "', '" + param.getValue() + "')";
			sqls.add(sql);
		}
		return sqls;
	}

	private String getObjectUpdateSql(Integer existingId){
		return "update cis_objects set lastmodified = now() where id = " + existingId;
	}

	private void storeErrors(List<ImportError> errs, Connection c){
		PreparedStatement st = null;
		String sql;
		sql = "insert into sys_import_data_error(srcid, objectname, attributename, invalidvalue, errorid, errtime) values (?, ?, ?, ?, ?, ?)";
		try{
			st = c.prepareStatement(sql);
			for (ImportError er : errs){
				st.setString(1, er.getSrcId());
				st.setString(2, er.getObjectName());
				st.setString(3, er.getAttrName());
				st.setString(4, er.getLabel());
				st.setInt(5, er.getErr().intValue());
				st.setTimestamp(6, new java.sql.Timestamp(new Date().getTime()));
				if (log.isTraceEnabled()){
					log.trace("execute: " + sql);
					log.trace(er.getSrcId() + ", " + er.getObjectName() + ", " + er.getAttrName() + ", " + er.getLabel()
							+ ", " + er.getErr() + ", " + new Date());
				}
				st.execute();
			}
			c.commit();
		} catch (SQLException ex){
			log.error("Error execute stamtent: " + sql, ex);
		} finally{
			if (st != null)
				try{
					st.close();
				} catch (SQLException ex){
					log.error("Error closing statement", ex);
				}
		}
	}

	private String parseError(SQLException e){
		String msg = e.getMessage();
		if (msg != null){
			if (msg.contains("aborted.")){
				msg = msg.substring(0, msg.indexOf("aborted.") + 8);
			}
		}
		return msg;
	}

	String getObjectInsertSQL(BigInteger objId, ObjectDefinition od, Integer userId){
		return "insert into cis_objects (id, objecttype, userid, status, creator) values (" + objId + ", " + od.getId()
				+ ", " + userId + ", 0, " + userId + ")";
	}

	String getInsertSql(String tableName, UserDataTable data, int row, BigInteger id, boolean usrTable, int objectId,
			String typeColName) throws ACException{
		StringBuilder query = new StringBuilder();
		StringBuilder valuesStr = new StringBuilder();
		query.append("insert into ").append(tableName).append(" (id");
		if (!usrTable){
			query.append(", ").append(typeColName);
			valuesStr.append(", ").append(objectId);
		}
		for (int i = 0; i < data.getAttributes().size(); i++){
			ObjectAttribute oa = data.getAttributes().get(i);
			if (oa != null && tableName.equalsIgnoreCase(oa.getInTable())){
				query.append(", ").append(oa.getName());
				valuesStr.append(", ").append(data.getValue(row, i));
			}
		}
		query.append(") values (").append(id);
		if (valuesStr.length() > 0){
			query.append(valuesStr);
		}
		query.append(")");

		if (log.isTraceEnabled()){
			log.trace("Insert sql: " + query.toString());
		}
		return query.toString();
	}

	String getUpdateSql(String tableName, UserDataTable data, int row, Integer id) throws ACException{
		StringBuilder query = new StringBuilder();
		query.append("update ").append(tableName).append(" set ");

		boolean found = false;
		for (int i = 0; i < data.getAttributes().size(); i++){
			ObjectAttribute oa = data.getAttributes().get(i);
			if (oa != null && tableName.equalsIgnoreCase(oa.getInTable())){
				if (found){
					query.append(", ");
				}
				query.append(oa.getName()).append(" = ").append(data.getValue(row, i));
				found = true;
			}
		}
		if (!found){
			return null;
		}
		query.append(" where id = ").append(id);
		if (log.isTraceEnabled()){
			log.trace("Update sql: " + query.toString());
		}
		return query.toString();
	}

	boolean isFromToIdAttribute(ObjectAttribute oa){
		if (CIS_EDGES_TABLE_NAME.equalsIgnoreCase(oa.getInTable())
				&& (ObjectAttribute.FROM_ID_ATTRIBUTE_NAME.equalsIgnoreCase(oa.getName()) || ObjectAttribute.TO_ID_ATTRIBUTE_NAME
						.equalsIgnoreCase(oa.getName()))){
			return true;
		}
		return false;
	}

	Integer getPredefinedId(ObjectAttribute oa, String predefinedLabel){
		Integer predefinedId = null;
		if (predefinedLabel.trim().isEmpty())
			return predefinedId;
		String value = null;
		if (predefinedLabel.contains(VALUE_LABEL_SEPARATOR)){
			final int sIndex = predefinedLabel.indexOf(VALUE_LABEL_SEPARATOR);
			value = predefinedLabel.substring(sIndex + VALUE_LABEL_SEPARATOR.length());
			predefinedLabel = predefinedLabel.substring(0, sIndex);
		}
		for (PredefinedAttribute predefined : oa.getPredefinedAttributes()){
			if (predefined.getLabel().equalsIgnoreCase(predefinedLabel)
					&& (value == null || predefined.getValue().equalsIgnoreCase(value))){
				predefinedId = predefined.getId();
				break;
			}
		}
		return predefinedId;
	}

	public Map<String, Integer> getNodeSrcIdMap(Schema schema){
		Map<String, Integer> idMap = new HashMap<String, Integer>();
		for (ObjectDefinition od : schema.getObjectDefinitions()){
			if (!od.isNode()){
				continue;
			}
			getSrcIdMap(od, idMap);
		}
		return idMap;
	}

	public Map<String, Integer> getSrcIdMap(ObjectDefinition od, Map<String, Integer> idMap){
		if (idMap == null){
			idMap = new HashMap<String, Integer>();
		}
		String tableName = od.getTableName();
		List<Object[]> idList = objectDAO.getIDsForUserTable(tableName);
		for (Object[] ids : idList){
			if (ids[1] == null){
				continue;
			}
			Integer id = (ids[0] instanceof BigInteger) ? ((BigInteger) ids[0]).intValue() : (Integer) ids[0];
			idMap.put((String) ids[1], id);
		}
		return idMap;
	}

	List<ImportError> prepareData(UserDataTable data, Map<String, Integer> nodeSrcIdMap, DateFormat defaultFormat,
			boolean recalculateFormulas) throws ACException{
		List<ImportError> errs = new ArrayList<ImportError>();
		data.checkSrcIdAttribute();
		List<ObjectAttribute> attributes = data.getAttributes();
		Set<Integer> invalidEdgeIndexes = new HashSet<Integer>();
		for (int c = 0; c < attributes.size(); c++){
			ObjectAttribute attr = attributes.get(c);
			if (attr == null || attr.getId() == null || attr.getId() < 0){
				continue;
			}

			boolean isDate = attr.isDateDataType();
			DateFormat formatter = null;
			if (isDate)
				formatter = getDateFormat(attr.getFormat(), defaultFormat);

			for (int r = 0; r < data.size(); r++){
				Object cell = data.getValue(r, c);
				if (cell == null)
					continue;
				String label = ((String) cell).trim();
				if (label.isEmpty() && !isFromToIdAttribute(attr)){
					data.setValue(r, c, null);
					continue;
				}

				Object value = label;

				if (isFromToIdAttribute(attr)){
					value = parseFromTo(attr, label, nodeSrcIdMap);
					if (value == null){
						invalidEdgeIndexes.add(r);
						errs.add(new ImportError(attr, label, data.getSrcId(r), ImportDataError.InvalidFromToId));
					}
				} else if (attr.getIsMultivalue()){
					if (attr.isPredefined())
						value = parseMultivaluePredefined(label, attr);
					else if (isDate)
						value = parseMultivalueDate(label, formatter);
					else
						value = parseMultivalue(label);
				} else{
					if (attr.isPredefined()){
						value = getPredefinedId(attr, label);
						if (value == null){
							log.warn("Cannot resolve predefined attribute for label `" + label + "` "
									+ attr.getObjectDefinition().getName() + "->" + attr.getName());
							errs.add(new ImportError(attr, label, data.getSrcId(r), ImportDataError.InvalidPredefined));
						}
					} else if (isDate){
						value = getFormattedDate(label, formatter);
						if (value != null)
							value = "'" + value + "'";
					} else if (attr.isTextDataType() || attr.isURLDataType()){
						value = "'" + label + "'";
					} else if (attr.isIntDataType() || attr.isDecimalDataType() || attr.isBoolDataType()){
						try{
							value = parseNumber(attr, label, r);
						} catch (NumberFormatException ex){
							errs.add(creteNumberFormatError(attr, label, data.getSrcId(r)));
							value = null;
						}
					}
				}
				data.setValue(r, c, value);
			}
		}
		data.filterOut(invalidEdgeIndexes);
		if (recalculateFormulas){
			addAndApplyFormulaAttributes(data);
		}
		return errs;
	}

	private void addAndApplyFormulaAttributes(UserDataTable data){
		for (ObjectAttribute oa : data.getOd().getObjectAttributes()){
			if (!oa.isFormulaAttribute())
				continue;
			if (data.getAttributes().contains(oa))
				continue;
			data.addAttribute(oa);
		}

		for (int r = 0; r < data.size(); r++){
			Map<ObjectAttribute, Object> dataMap = new HashMap<ObjectAttribute, Object>();
			for (int c = 0; c < data.getAttributes().size(); c++){
				ObjectAttribute attribute = data.getAttributes().get(c);
				Object attributeValue = data.getValue(r, c);
				dataMap.put(attribute, plainValue(attribute, attributeValue));
			}
			appendMissingValues(dataMap, data.getOd());
			FormulaExecutor.recalcObjectFields(data.getOd(), dataMap);
			for (int c = 0; c < data.getAttributes().size(); c++){
				ObjectAttribute oa = data.getAttributes().get(c);
				if (!oa.isFormulaAttribute())
					continue;
				Object value = dataMap.get(oa);
				if (value != null && (oa.isTextDataType() || oa.isURLDataType()) && !oa.isPredefined()){
					String sValue = value.toString();
					if (!sValue.startsWith("'"))
						sValue = "'" + sValue;
					if (!sValue.endsWith("'"))
						sValue = sValue + "'";
					value = sValue;
				}
				data.setValue(r, c, value);
			}
		}
	}

	private Object plainValue(ObjectAttribute attribute, Object attributeValue){
		if (attributeValue == null)
			return attributeValue;
		if (attribute.isPredefined())
			return attributeValue;
		if (!attribute.isTextDataType() && !attribute.isURLDataType())
			return attributeValue;
		String sValue = (String) attributeValue;
		if (sValue.startsWith("'"))
			sValue = sValue.substring(1);
		if (sValue.endsWith("'"))
			sValue = sValue.substring(0, sValue.length() - 1);
		return sValue;
	}

	private void appendMissingValues(Map<ObjectAttribute, Object> dataMap, ObjectDefinition od){
		for (ObjectAttribute oa : od.getObjectAttributes())
			if (!dataMap.containsKey(oa))
				dataMap.put(oa, null);
	}

	private ImportError creteNumberFormatError(ObjectAttribute attr, String label, String srcId){
		if (attr.isIntDataType())
			return new ImportError(attr, label, srcId, ImportDataError.InvalidInt);
		else if (attr.isDecimalDataType())
			return new ImportError(attr, label, srcId, ImportDataError.InvalidDecimal);
		else if (attr.isBoolDataType())
			return new ImportError(attr, label, srcId, ImportDataError.InvalidBool);
		return null;
	}

	private Object parseFromTo(ObjectAttribute attr, String label, Map<String, Integer> nodeSrcIdMap) throws ACException{
		Integer existing = nodeSrcIdMap.get(label);
		Object value = existing != null ? existing.toString() : null;
		if (value == null){
			log.warn("Cannot parse " + attr.getName() + ", value = " + label);
			// throw new ACException(TextID.MsgFromToIdNotCorrect);
		}
		return value;
	}

	private String parseNumber(ObjectAttribute attr, String label, int row) throws ACException{
		if (label == null || label.isEmpty())
			return null;
		try{
			Double.parseDouble(label);
		} catch (NumberFormatException ex){
			log.warn("Column '" + attr.getName() + "' is of numeric type. Value of row " + (row) + " cannot be parsed: "
					+ label);
			throw ex;
		}
		return label;
	}

	String parseMultivaluePredefined(String multivalue, ObjectAttribute attr){
		if (multivalue == null || multivalue.isEmpty()){
			return null;
		}
		String[] labels = multivalue.split(";");
		String ids = "";
		for (String label : labels){
			Integer id = getPredefinedId(attr, label);
			if (id != null){
				ids += "{" + id + "}";
			} else
				log.warn("Cannot resolve predefined value for id " + label);
		}
		if (ids.length() > 0){
			ids = "'" + ids + "'";
		} else{
			ids = null;
		}
		return ids;
	}

	String parseMultivalueDate(String multivalue, DateFormat format){
		if (multivalue == null || multivalue.isEmpty()){
			return null;
		}
		String[] labels = multivalue.split(";");
		String dates = "";
		for (String label : labels){
			String date = getFormattedDate(label, format);
			if (date != null && !date.isEmpty()){
				dates += "{" + date + "}";
			}
		}
		if (!dates.isEmpty()){
			dates = "'" + dates + "'";
		} else{
			dates = null;
		}
		return dates;
	}

	String parseMultivalue(String multivalue){
		if (multivalue == null || multivalue.isEmpty()){
			return null;
		}
		multivalue = "'{" + multivalue.replace(";", "}{") + "}'";
		return multivalue;
	}

	public String getFormattedDate(String val, DateFormat format){
		if (val == null)
			return null;
		Date date = null;
		try{
			date = format.parse(val);
		} catch (ParseException e){
			log.warn("Cannot parse date: " + val);
		}
		String result = null;
		if (date != null){
			result = STORE_DATE_FORMAT.format(date);
		}
		return result;
	}

	DateFormat getDateFormat(String format, DateFormat defaultFormat){
		if (format == null || format.isEmpty() || "0".equals(format) || "null".equalsIgnoreCase(format)){
			return defaultFormat;
		}

		return new SimpleDateFormat(format);
	}

	public DateFormat initFormat(int userId){
		String sql = "select value from sys_user_settings where section = 'Applet' and prop = '"
				+ Setting.DATE_FORMAT_PROPERTY + "' and userid = " + userId;
		String format = (String) nodeDAO.getUniqueResult(sql, new Object[] {});

		return new SimpleDateFormat((format == null || format.isEmpty()) ? DataType.DISPLAY_DATE_FORMAT : format);
	}

}
