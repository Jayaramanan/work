/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectStatus;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDAO;
import com.ni3.ag.adminconsole.server.formula.FormulaExecutor;
import com.ni3.ag.adminconsole.shared.service.def.CalculateFormulaService;
import com.ni3.ag.adminconsole.util.IntParser;
import com.ni3.ag.adminconsole.validation.ACException;

public class CalculateFormulaServiceImpl implements CalculateFormulaService{
	private static final Logger log = Logger.getLogger(CalculateFormulaServiceImpl.class);
	private static final String CIS_NODES_TABLE = "cis_nodes";
	private static final String CIS_EDGES_TABLE = "cis_edges";
	private static final String CIS_OBJECTS_TABLE = "cis_objects";

	private ObjectAttributeDAO objectAttributeDAO;
	private ObjectDAO objectDAO;

	public void setObjectDAO(ObjectDAO objectDAO){
		this.objectDAO = objectDAO;
	}

	public void setObjectAttributeDAO(ObjectAttributeDAO objectAttributeDAO){
		this.objectAttributeDAO = objectAttributeDAO;
	}

	@Override
	public void calculateFormulaValue(Integer attributeId) throws ACException{
		ObjectAttribute attr = objectAttributeDAO.getObjectAttribute(attributeId);
		if (attr != null && attr.getFormula() != null){
			calculateValues(attr);
		}
	}

	private void calculateValues(ObjectAttribute attr) throws ACException{
		ObjectDefinition od = attr.getObjectDefinition();
		final List<ObjectAttribute> attributes = getAttributes(od);
		final String sql = getDataSql(od, attributes);
		log.debug("Getting user data for calculation");
		List<?> nodeData = (List<?>) objectDAO.getData(sql, new Object[] { od.getId(), ObjectStatus.Normal.toInt(),
				ObjectStatus.Locked.toInt() });
		log.debug("Start calculation, formula = " + attr.getFormula().getFormula());
		Map<Integer, Object> values = new HashMap<Integer, Object>();
		for (Object node : nodeData){
			Map<String, Object> dataMap = null;
			Object[] dataRow = (Object[]) node;
			dataMap = prepareDataMap(dataRow, attributes);

			final Object value = FormulaExecutor.recalculateFormulaValues(attr, dataMap);

			values.put(IntParser.getInt(dataRow[0]), value);
		}
		log.debug("Calculation finished");
		log.debug("Store values");
		storeValues(od, attr, values);
		log.debug("Values stored");
	}

	private List<ObjectAttribute> getAttributes(ObjectDefinition od){
		List<ObjectAttribute> attributes = new ArrayList<ObjectAttribute>();
		for (ObjectAttribute attr : od.getObjectAttributes()){
			if (!attr.isInContext()){
				attributes.add(attr);
			}
		}
		return attributes;
	}

	private Map<String, Object> prepareDataMap(Object[] dataRow, List<ObjectAttribute> attributes){
		Map<String, Object> dataMap = new HashMap<String, Object>();
		for (int i = 0; i < attributes.size(); i++){
			ObjectAttribute pAttr = attributes.get(i);
			dataMap.put(pAttr.getName(), dataRow[i + 1]);
		}
		return dataMap;
	}

	public String getDataSql(ObjectDefinition od, List<ObjectAttribute> attributes){
		String cisTableName = od.isNode() ? CIS_NODES_TABLE : CIS_EDGES_TABLE;
		StringBuffer sql = new StringBuffer();
		sql.append("select ot.id");
		for (int i = 0; i < attributes.size(); i++){
			ObjectAttribute attr = attributes.get(i);
			sql.append(", ");
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
		sql.append(" where ot.objectType = ? AND ot.status in (?,?)");
		log.debug(sql);
		return sql.toString();
	}

	private void storeValues(ObjectDefinition od, ObjectAttribute attr, Map<Integer, Object> values){
		String sql = "update " + attr.getInTable() + " set " + attr.getName() + " = ? where id = ?";
		for (Integer id : values.keySet()){
			final Object value = formatValue(values.get(id), attr);
			objectDAO.executeUpdate(sql, new Object[] { value, id }, new DataType[] { attr.getDatabaseDataType(),
					DataType.INT });
		}
	}

	Object formatValue(Object value, ObjectAttribute attr){
		Object result = null;
		if (value == null){
			result = null;
		} else{
			DataType dbDataType = attr.getDatabaseDataType();
			switch (dbDataType){
				case TEXT:
					result = value.toString();
					break;
				case INT:
					result = IntParser.getInt(value);
					break;
				case DECIMAL:
					if (value instanceof Double){
						result = value;
					} else{
						try{
							result = Double.valueOf(value.toString());
						} catch (NumberFormatException ex){
							log.warn("Cannot parse result: " + value);
						}
					}
					break;
			}
		}
		return result;
	}
}
