/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.ObjectStatus;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.EdgeDAO;
import com.ni3.ag.adminconsole.server.dao.NodeDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.util.IntParser;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class UserDataExporter{
	private static final Logger log = Logger.getLogger(UserDataExporter.class);
	private static final String CIS_NODES_TABLE = "cis_nodes";
	private static final String CIS_EDGES_TABLE = "cis_edges";
	private static final String CIS_OBJECTS_TABLE = "cis_objects";
	private static final DateFormat STORE_DATE_FORMAT = new SimpleDateFormat(DataType.DB_DATE_FORMAT);

	private NodeDAO nodeDAO;
	private EdgeDAO edgeDAO;
	private ObjectDAO objectDAO;
	private DiagnosticTask srcIdCheckTask;
	private DateFormat dateFormat;
	private Map<Integer, String> idMap;

	public void setObjectDAO(ObjectDAO objectDAO){
		this.objectDAO = objectDAO;
	}

	public void setSrcIdCheckTask(DiagnosticTask srcIdCheckTask){
		this.srcIdCheckTask = srcIdCheckTask;
	}

	public NodeDAO getNodeDAO(){
		return nodeDAO;
	}

	public void setNodeDAO(NodeDAO nodeDAO){
		this.nodeDAO = nodeDAO;
	}

	public EdgeDAO getEdgeDAO(){
		return edgeDAO;
	}

	public void setEdgeDAO(EdgeDAO edgeDAO){
		this.edgeDAO = edgeDAO;
	}

	public String getDataSql(ObjectDefinition od, List<ObjectAttribute> attributes, Group group){
		String cisTableName = od.isNode() ? CIS_NODES_TABLE : CIS_EDGES_TABLE;
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		for (int i = 0; i < attributes.size(); i++){
			if (i > 0)
				sql.append(", ");
			ObjectAttribute attr = attributes.get(i);
			if (attr.getInTable().equalsIgnoreCase(cisTableName)){
				sql.append("ct.").append(attr.getName());
			} else if (attr.getInTable().equalsIgnoreCase(CIS_OBJECTS_TABLE)){
				sql.append("ot.").append(attr.getName());
			} else{
				sql.append("ut.").append(attr.getName());
			}
		}
		sql.append(" from ").append(od.getTableName()).append(" ut");
		sql.append(" inner join ").append(cisTableName).append(" ct on (ct.id = ut.id)");
		sql.append(" inner join ").append(CIS_OBJECTS_TABLE).append(" ot on (ot.id = ut.id)");
		if (od.isEdge()){
			sql.append(getObjectEdgeAccessSql(group));
		}
		sql.append(" where ot.objectType = ").append(od.getId());
		sql.append(" and ot.status in (").append(ObjectStatus.Normal.toInt()).append(",")
				.append(ObjectStatus.Locked.toInt()).append(")");
		log.debug(sql);
		return sql.toString();
	}

	private Object getObjectEdgeAccessSql(Group group){
		StringBuilder sql = new StringBuilder();
		sql.append(" inner join cis_objects fromCO on (ct.fromid = fromCO.id)");
		sql.append(" inner join sys_object_group fromOG on (fromCO.objecttype = fromOG.objectid and fromOG.canread = 1");
		sql.append(" and fromOG.groupid = ").append(group.getId()).append(")");
		sql.append(" inner join cis_objects toCO on (ct.toid = toCO.id)");
		sql.append(" inner join sys_object_group toOG on (toCO.objecttype = toOG.objectid and toOG.canread = 1");
		sql.append(" and toOG.groupid = ").append(group.getId()).append(")");
		return sql;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getData(ObjectDefinition od, Group group, List<ObjectAttribute> attributes){
		String sql = getDataSql(od, attributes, group);
		List<Object[]> data = (List<Object[]>) nodeDAO.getData(sql, new Object[] {});
		log.debug("Found " + data == null ? 0 : data.size() + " records for object definition " + od);
		if (attributes.size() == 1){
			data = convertData(data);
		}

		if (od.isEdge())
			fillSrcIdMap(od.getSchema());
		prepareData(data, attributes);

		cleanupMap();
		return data;
	}

	private List<Object[]> convertData(List<Object[]> data){
		for (int i = 0; i < data.size(); i++){
			Object obj = data.get(i);
			data.set(i, new Object[] { obj });
		}
		return data;
	}

	public boolean isAvailableObject(ObjectDefinition od, Group group){
		List<ObjectGroup> ougs = group.getObjectGroups();
		for (ObjectGroup oug : ougs){
			if (oug.getObject().equals(od) && oug.isCanRead()){
				return true;
			}
		}
		return false;
	}

	public List<ObjectAttribute> getAvailableExportAttributes(List<ObjectAttribute> allAttributes, Group group){
		List<ObjectAttribute> result = new ArrayList<ObjectAttribute>();

		List<AttributeGroup> ags = group.getAttributeGroups();
		for (ObjectAttribute attr : allAttributes){
			for (AttributeGroup ag : ags){
				if (ag.getObjectAttribute().equals(attr) && ag.isCanRead() && attr.isInExport()){
					result.add(attr);
					break;
				}
			}
		}
		Collections.sort(result, new AttributeInMatrixComparator());
		return result;
	}

	boolean isFromToIdAttribute(ObjectAttribute oa){
		if (CIS_EDGES_TABLE.equalsIgnoreCase(oa.getInTable())
				&& (ObjectAttribute.FROM_ID_ATTRIBUTE_NAME.equalsIgnoreCase(oa.getName()) || ObjectAttribute.TO_ID_ATTRIBUTE_NAME
						.equalsIgnoreCase(oa.getName()))){
			return true;
		}
		return false;
	}

	Map<Integer, String> fillSrcIdMap(Schema schema){
		idMap = new HashMap<Integer, String>();
		for (ObjectDefinition od : schema.getObjectDefinitions()){
			if (!od.isNode()){
				continue;
			}
			String tableName = od.getTableName();
			List<Object[]> idList = objectDAO.getIDsForUserTable(tableName);
			for (Object[] ids : idList){
				Integer id = (ids[0] instanceof BigInteger) ? ((BigInteger) ids[0]).intValue() : (Integer) ids[0];
				idMap.put(id, (String) ids[1]);
			}
		}
		return idMap;
	}

	public void checkSrcIds(Schema schema){
		DiagnoseTaskResult result = srcIdCheckTask.makeDiagnose(schema);
		if (!result.getStatus().equals(DiagnoseTaskStatus.Ok)){
			result.setFixParams(new Object[] { "", schema.getId() });
			try{
				srcIdCheckTask.makeFix(result);
			} catch (ACFixTaskException e){
				log.error(e);
			} catch (ACException e){
				log.error(e);
			}
		}
	}

	List<Object[]> prepareData(List<Object[]> data, List<ObjectAttribute> attributes){
		for (int c = 0; c < attributes.size(); c++){
			ObjectAttribute attr = attributes.get(c);

			boolean isDate = attr.isDateDataType();
			DateFormat formatter = null;
			if (isDate)
				formatter = getDateFormat(attr.getFormat());

			boolean isNumeric = isNumericAttribute(attr);

			for (int r = 0; r < data.size(); r++){
				Object obj = data.get(r)[c];
				if (obj == null)
					continue;
				String value = obj.toString();
				Object label = null;

				if (isFromToIdAttribute(attr)){
					Integer nodeId = IntParser.getInt(data.get(r)[c]);
					label = idMap.get(nodeId);
				} else if (attr.getIsMultivalue()){
					if (attr.isPredefined())
						label = parseMultivaluePredefined(value, attr);
					else if (isDate)
						label = parseMultivalueDate(value, formatter);
					else
						label = prepareMultivalue(value);
				} else{
					if (attr.isPredefined())
						label = getPredefinedLabel(Integer.parseInt(value), attr);
					else if (isDate)
						label = getFormattedDate(value, formatter);
					else if (isNumeric)
						label = formatNumber(obj, attr.getDataType());
					else
						label = value;
				}

				data.get(r)[c] = label;
			}
		}
		return data;
	}

	Object formatNumber(Object number, DataType dt){
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

		if (result instanceof Double && dt == DataType.INT){
			result = ((Double) result).intValue();
		}

		return result;
	}

	private boolean isNumericAttribute(ObjectAttribute oa){
		return !oa.isPredefined() && !oa.getIsMultivalue() && (oa.isIntDataType() || oa.isDecimalDataType());
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
			return null;
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

	DateFormat getDateFormat(String format){
		if (format == null || format.isEmpty() || "0".equals(format) || "null".equalsIgnoreCase(format)){
			return dateFormat;
		}

		DateFormat sdf = new SimpleDateFormat(format);
		return sdf;
	}

	public void initFormat(User user){
		String sql = "select value from sys_user_settings where section = 'Applet' and prop = '"
				+ Setting.DATE_FORMAT_PROPERTY + "' and userid = " + user.getId();
		String format = (String) nodeDAO.getUniqueResult(sql, new Object[] {});

		dateFormat = new SimpleDateFormat((format == null || format.isEmpty()) ? DataType.DISPLAY_DATE_FORMAT : format);
	}

	public void cleanupMap(){
		if (idMap != null){
			idMap.clear();
			idMap = null;
		}
	}

	void setIdMap(Map<Integer, String> idMap){
		this.idMap = idMap;
	}

	private class AttributeInMatrixComparator implements Comparator<ObjectAttribute>{
		@Override
		public int compare(ObjectAttribute o1, ObjectAttribute o2){
			return o1.getMatrixSort() - o2.getMatrixSort();
		}
	}
}
