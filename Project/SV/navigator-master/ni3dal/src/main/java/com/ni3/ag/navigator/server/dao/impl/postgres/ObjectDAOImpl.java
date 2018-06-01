package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import com.ni3.ag.navigator.server.dao.DaoException;
import com.ni3.ag.navigator.server.dao.ObjectDAO;
import com.ni3.ag.navigator.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.CisObject;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.services.UserDataService;
import com.ni3.ag.navigator.shared.constants.ObjectStatus;
import com.ni3.ag.navigator.shared.domain.DBObject;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ObjectDAOImpl extends JdbcDaoSupport implements ObjectDAO{
	private static final Logger log = Logger.getLogger(ObjectDAOImpl.class);
	private static final String SELECT_BY_ID = "select id, objecttype, userid, status, creator, lastmodified "
	        + "from cis_objects where id = ?";

	private ObjectDefinitionDAO objectDefinitionDAO;
	private UserDataService userDataService;

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public void setUserDataService(UserDataService userDataService){
		this.userDataService = userDataService;
	}

	private RowMapper cisObjectRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
			CisObject co = new CisObject();
			co.setId(rs.getInt(1));
			co.setTypeId(rs.getInt(2));
			co.setUserId(rs.getInt(3));
			co.setStatus(ObjectStatus.fromInt(rs.getInt(4)));
			co.setCreatorId(rs.getInt(5));
			java.sql.Date date = rs.getDate(6);
			if (date != null){
				co.setLastModified(new Date(date.getTime()));
			}
			return co;
		}
	};

	private String makeInFilter(List<ObjectDefinition> types){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		boolean first = true;
		for (ObjectDefinition ent : types){
			int id = ent.getId();
			if (first){
				sb.append(id);
				first = false;
			} else
				sb.append(", ").append(id);
		}
		sb.append(")");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CisObject> getUpdatedObjects(Date lastUpdateTime, List<ObjectDefinition> types){
		String sql = "select id, objecttype, userid, status, creator, lastmodified "
		        + "from cis_objects where (lastmodified > ? or lastmodified is null) " + " and objecttype in "
		        + makeInFilter(types) + "order by lastmodified";

		Object[] params = new Object[] { new Timestamp(lastUpdateTime.getTime()) };
		return getJdbcTemplate().query(sql, params, cisObjectRowMapper);
	}

	@Override
	public CisObject get(int objectId){
		return (CisObject) getJdbcTemplate().queryForObject(SELECT_BY_ID, new Object[] { objectId }, cisObjectRowMapper);
	}

	@Override
	public void fillLastModified(Date lastModified, List<ObjectDefinition> types){
		String sql = "update cis_objects set lastmodified = ? where lastmodified is null and objecttype in "
		        + makeInFilter(types);
		Object[] params = new Object[] { new Timestamp(lastModified.getTime()) };
		getJdbcTemplate().update(sql, params);
	}

	@Override
	public String getSrcIdById(Integer id, ObjectDefinition od) throws SQLException{
		ObjectDefinition entity = objectDefinitionDAO.get(od.getId());
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		Map<Integer, DBObject> result = userDataService.getDataForIdList(entity, ids);
		DBObject object = result.get(id);
		if(object == null){
			log.warn("No object found for id: " + id);
			return "-1";
		}
		for(Attribute a : entity.getAttributes()){
			if(a.getName().equalsIgnoreCase("srcid")){
				return object.getData().get(a.getId());
			}
		}
		return "-1";
	}

	@Override
	public void setChanged(Integer id) {
		String sql = "update cis_objects set changed = 1 where id = ?";
		Object[] params = new Object[] { id };
		getJdbcTemplate().update(sql, params);
	}

	public static Map<Attribute, Object> getAttributeValueMap(DBObject edge, ObjectDefinition entity){
		Map<Attribute, Object> result = new HashMap<Attribute, Object>();
		for(Integer attrId : edge.getData().keySet()){
			Attribute a = entity.getAttribute(attrId);
			result.put(a, getResultSetValue(a, edge.getData().get(attrId)));
		}
		return result;
	}

	private static Object getResultSetValue(Attribute a, String val){
		if (a.isPredefined())
			return Integer.parseInt(val);
		switch (a.getDataType()){
			case TEXT:
			case URL:
			case DATE:
				return val;

			case INT:
			case BOOL:
				return Integer.parseInt(val);

			case DECIMAL:
				return Double.parseDouble(val);

			default:
				throw new DaoException("Invalid attribute data type " + a);
		}
	}

}
