/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.navigator.server.services.ExportService;
import com.ni3.ag.navigator.server.type.PredefinedType;

public class ExportServiceImpl extends JdbcDaoSupport implements ExportService {
    private static final Logger log = Logger.getLogger(ExportServiceImpl.class);

    private static final String CIS_NODES_TABLE = "cis_nodes";
    private static final String CIS_EDGES_TABLE = "cis_edges";
    private static final String CIS_OBJECTS_TABLE = "cis_objects";

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectDefinition> getObjectDefinitionsByCisObjects(String ids) {
        // TODO avoid IN(<string>), possible sql injection
        final String GET_OD_BY_CIS_OBJECTS = "select distinct od.id, od.objecttypeid, od.name, od.tableName, od.sort"
                + " from cis_objects o, sys_object od where o.objectType = od.id and o.id in (?) order by od.sort";
        final String sql = GET_OD_BY_CIS_OBJECTS.replace("?", ids);
        return getJdbcTemplate().query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                ObjectDefinition od = new ObjectDefinition();
                od.setId(rs.getInt(1));
                od.setObjectType(ObjectType.fromInt(rs.getInt(2)));
                od.setName(rs.getString(3));
                od.setTableName(rs.getString(4));
                return od;
            }
        });
    }

    @Override
    public boolean isAvailableObject(Integer odId, Integer groupId) {
        final String sql = "select canread from sys_object_user_group where groupId = ? and objectId = ?";
        return (Boolean) getJdbcTemplate().queryForObject(sql, new Object[]{groupId, odId},
                new RowMapper() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getBoolean(1);
                    }
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectAttribute> getAvailableAttributes(Integer odId, Integer groupId) {
        final String sql = "select oa.id, oa.name, oa.label, "
                + "case when oa.Predefined in (" + PredefinedType.Predefined.getId() + ","
                + PredefinedType.FormulaPredefined.getId() + ") then " + PredefinedType.Predefined.getId() + " else "
                + PredefinedType.NotPredefined.getId() + " end as predefined"
                + ", oa.intable, oa.datatypeid, oa.multivalue, oa.format from sys_object_attributes oa"
                + " inner join sys_attribute_group ag on (ag.attributeId = oa.id and ag.groupId = ? and ag.canread = 1)"
                + " where objectDefinitionId = ? and oa.inExport = 1 order by oa.matrix_sort";
        return getJdbcTemplate().query(sql, new Object[]{groupId, odId}, new RowMapper() {
            @Override
            public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                ObjectAttribute objectAttribute = new ObjectAttribute();

                objectAttribute.setId(resultSet.getInt("id"));
                objectAttribute.setName(resultSet.getString("name"));
                objectAttribute.setLabel(resultSet.getString("label"));
                objectAttribute.setPredefined(resultSet.getBoolean("predefined"));
                objectAttribute.setInTable(resultSet.getString("intable"));

                objectAttribute.setDataType(DataType.fromInt(resultSet.getInt("datatypeid")));
                objectAttribute.setIsMultivalue(resultSet.getInt("multivalue") != 0);
                objectAttribute.setFormat(resultSet.getString("format"));
                if (objectAttribute.isPredefined()) {
                    objectAttribute.setPredefinedAttributes(getPredefineds(objectAttribute.getId()));
                }
                return objectAttribute;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private List<PredefinedAttribute> getPredefineds(int attributeId) {
        final String sql = "select id, label from cht_predefinedattributes where attributeid = ?";
        return getJdbcTemplate().query(sql, new Object[]{attributeId}, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                PredefinedAttribute pa = new PredefinedAttribute();
                pa.setId(rs.getInt(1));
                pa.setLabel(rs.getString(2));
                return pa;
            }
        });
    }

    @Override
    public Map<Integer, String> getSrcIdMap(List<ObjectDefinition> objectDefinitions) {
        final String GET_ID_SRCID = "select id, srcid from ";
        final StringBuilder sb = new StringBuilder();
        for (ObjectDefinition od : objectDefinitions) {
            if (sb.length() > 0)
                sb.append(" union ");
            sb.append(GET_ID_SRCID).append(od.getTableName());
        }
        final Map<Integer, String> idMap = new HashMap<Integer, String>();
        getJdbcTemplate().query(sb.toString(), new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer id = rs.getInt(1);
                String srcId = rs.getString(2);
                idMap.put(id, srcId);
                return null;
            }
        });
        return idMap;
    }

    @Override
    public Integer getGroupId(Integer userId) {
        final String sql = "select groupId from sys_user_group where userId = ?";
        return getJdbcTemplate().queryForInt(sql, new Object[]{userId});
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getUserData(ObjectDefinition od, List<ObjectAttribute> attributes, String objectIds) {
        final String sql = getDataSql(od, attributes, objectIds);
        return getJdbcTemplate().query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                Object[] arr = new Object[columnCount];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = rs.getObject(i + 1);
                }
                return arr;
            }
        });
    }

    private String getDataSql(ObjectDefinition od, List<ObjectAttribute> attributes, String objectIds) {
        String cisTableName = od.isNode() ? CIS_NODES_TABLE : CIS_EDGES_TABLE;
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        for (int i = 0; i < attributes.size(); i++) {
            if (i > 0)
                sql.append(", ");
            ObjectAttribute attr = attributes.get(i);
            if (attr.getInTable().equalsIgnoreCase(cisTableName)) {
                sql.append("ct.").append(attr.getName());
            } else if (attr.getInTable().equalsIgnoreCase(CIS_OBJECTS_TABLE)) {
                sql.append("ot.").append(attr.getName());
            } else {
                sql.append("ut.").append(attr.getName());
            }
        }
        sql.append(" from ").append(od.getTableName()).append(" ut");
        sql.append(" inner join ").append(cisTableName).append(" ct on (ct.id = ut.id)");
        sql.append(" inner join ").append(CIS_OBJECTS_TABLE).append(" ot on (ot.id = ut.id)");
        sql.append(" where ot.objectType = ").append(od.getId());
        sql.append(" and ut.id in (").append(objectIds).append(")");
        String sqlString = sql.toString();
        if (log.isDebugEnabled()) {
            log.debug(sqlString);
        }
        return sqlString;
    }
}
