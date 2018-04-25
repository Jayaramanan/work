package com.ni3.ag.navigator.server.datasource.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ni3.ag.navigator.server.dao.DaoException;
import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.db.DatabaseAdapter;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.shared.constants.ObjectStatus;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.DataType;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PostgreSqlAttributeDatasource extends JdbcDaoSupport implements AttributeDataSource{
	private Pattern pattern = Pattern.compile("(\\{\\d+\\})");
	private static final Logger log = Logger.getLogger(PostgreSqlAttributeDatasource.class);
	private DatabaseAdapter databaseAdapter;
	private String tableName;
	private boolean isPrimary;

	public void setDatabaseAdapter(DatabaseAdapter databaseAdapter){
		this.databaseAdapter = databaseAdapter;
	}

	public void setTableName(String tableName){
		this.tableName = tableName;
	}

	public String getTableName(){
		return tableName;
	}

	@Override
	public void setPrimary(boolean flag){
		isPrimary = flag;
	}

	@Override
	public boolean isPrimary(){
		return isPrimary;
	}

	@Override
	public List<Integer> search(Attribute attribute, AdvancedCriteria.Section.Condition condition){
		List<String> params = new ArrayList<String>();
		return searchIds(makeWhereClause(attribute, condition, params), params);
	}

	@Override
	public List<Integer> search(Attribute attribute, AdvancedCriteria.Section.ConditionGroup conditionGroup){
		List<String> params = new ArrayList<String>();
		return searchIds(makeWhereClause(attribute, conditionGroup, params), params);
	}

	@SuppressWarnings("unchecked")
	protected List<Integer> searchIds(String whereClause, List<String> params){
		final String sql = "select u.id from " + getTableName() + " u "
				+ "left join cis_objects co on co.id=u.id where co.status in (0, 1) and " + whereClause;
		log.debug(sql);
		log.debug(params);
		List result = getJdbcTemplate().query(sql, params.toArray(), new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				return rs.getInt("id");
			}
		});
		log.debug("Result set size: " + result.size());
		return result;
	}

	@Override
	public void get(Collection<Integer> ids, final List<Attribute> attributes, final Map<Integer, DBObject> results){
		if (attributes.isEmpty())
			return;
		if (ids.isEmpty())
			return;
		final String sql = "select u.id, " + makeAttributeList("u", attributes) + " from " + getTableName() + " u "
				+ "left join cis_objects co on co.id = u.id where co.status in (0, 1) and u.id in (" + makeIdList(ids) + ")";
		extract(sql, attributes, results);
	}

	@Override
	public void get(List<Attribute> attributes, Map<Integer, DBObject> results){
		if (attributes.isEmpty())
			return;
		final String sql = "select u.id, " + makeAttributeList("u", attributes) + " from " + getTableName() + " u "
				+ "left join cis_objects co on co.id = u.id where co.status in (0, 1)";
		extract(sql, attributes, results);
	}

	@Override
	public void getContext(Collection<Integer> ids, Attribute pkAttribute, String contextKey, List<Attribute> attributes,
						   Map<Integer, DBObject> results){
		final String sql = "select u.id, " + makeAttributeList("u", attributes, false) + " from " + getTableName() + " u "
				+ ", cis_objects co where co.id = u.id and co.status in (0, 1) and " + pkAttribute.getName()
				+ " = ? and u.id in (" + makeIdList(ids) + ")";
		extract(sql, attributes, results, new Object[]{Integer.parseInt(contextKey)}, false);
	}

	protected void extract(String sql, final List<Attribute> attributes, final Map<Integer, DBObject> results){
		extract(sql, attributes, results, null, true);
	}

	protected void extract(String sql, final List<Attribute> attributes, final Map<Integer, DBObject> results,
						   Object[] params, final boolean ignoreContextAttributes){
		getJdbcTemplate().query(sql, params, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				int id = rs.getInt("id");
				DBObject obj = results.get(id);
				if (obj == null){
					obj = new DBObject(id, attributes.get(0).getEntity().getId());
					obj.setData(new LinkedHashMap<Integer, String>());
					results.put(id, obj);
				}
				for (Attribute attribute : attributes){
					if (attribute.isInContext() && ignoreContextAttributes)
						continue;
					String val = rs.getString(attribute.getName());
					if (val != null)
						obj.getData().put(attribute.getId(), val);
				}
				return null;
			}
		});
	}

	private String makeAttributeList(String prefix, List<Attribute> attributes){
		return makeAttributeList(prefix, attributes, true);
	}

	private String makeAttributeList(String prefix, List<Attribute> attributes, boolean ignoreContextAttributes){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Attribute attribute : attributes){
			if (attribute.isInContext() && ignoreContextAttributes)
				continue;
			if (!first)
				sb.append(", ");
			first = false;
			if(prefix != null)
				sb.append(prefix).append(".");
			sb.append(attribute.getName());
		}
		return sb.toString();
	}

	@Override
	public int createNode(Integer newObjectId, int entityId, int userId, Attribute attribute, String attributeValue){
		newObjectId = getNewId(newObjectId);
		String sql = "insert into cis_objects(id, objecttype, userid, status, creator) values (?, ?, ?, ?, ?)";
		Object[] params = new Object[]{newObjectId, entityId, userId, ObjectStatus.Normal.toInt(), userId};
		executeUpdate(sql, params);
		sql = "insert into cis_nodes(id, nodetype) values (?, ?)";
		params = new Object[]{newObjectId, entityId};
		executeUpdate(sql, params);
		sql = "insert into " + getTableName() + " (id, " + attribute.getName() + ") values (?, ?)";
		params = new Object[]{newObjectId, getUpdateValue(attribute, attributeValue)};
		executeUpdate(sql, params);
		return newObjectId;
	}

	@Override
	public int createEdge(Integer newObjectId, int fromId, int toId, int entityId, int userId, Attribute attribute,
						  String attributeValue){
		newObjectId = getNewId(newObjectId);
		String sql = "insert into cis_objects(id, objecttype, userid, status, creator) values (?, ?, ?, ?, ?)";
		Object[] params = new Object[]{newObjectId, entityId, userId, ObjectStatus.Normal.toInt(), userId};
		executeUpdate(sql, params);
		sql = "insert into cis_edges(id, edgetype, fromid, toid, userid) values (?, ?, ?, ?, ?)";
		params = new Object[]{newObjectId, entityId, fromId, toId, userId};
		executeUpdate(sql, params);
		sql = "insert into " + getTableName() + " (id, " + attribute.getName() + ") values (?, ?)";
		params = new Object[]{newObjectId, getUpdateValue(attribute, attributeValue)};
		executeUpdate(sql, params);
		return newObjectId;
	}

	@Override
	public void saveOrUpdate(int id, Map<Attribute, String> values){
		String sql = "select count(*) from " + getTableName() + " where id = ?";
		int count = getJdbcTemplate().queryForInt(sql, new Object[]{id});
		if (count > 0)
			sql = "update " + getTableName() + " set " + makeUpdateSetClause(values) + " where id = ?";
		else
			sql = "insert into " + getTableName() + "(" + makeInsertSetClause(values) + ", id) values ( "
					+ makeInsertSubstituteClause(values.keySet().size()) + ", ?)";
		Object[] params = getUpdateValue(values, id);
		executeUpdate(sql, params);
//		if (isPrimary){
			sql = "update cis_objects set lastmodified = now(), changed = 1 where id = ?";
			executeUpdate(sql, new Object[]{id});
//		}
	}

	private Object[] getUpdateValue(Map<Attribute, String> values, Object... additional){
		List<Object> params = new ArrayList<Object>();
		for (Attribute attribute : values.keySet()){
			params.add(getUpdateValue(attribute, values.get(attribute)));
		}
		params.addAll(Arrays.asList(additional));
		return params.toArray(new Object[params.size()]);
	}

	private String makeInsertSubstituteClause(int size){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++)
			if (i != 0)
				sb.append(", ?");
			else
				sb.append("?");
		return sb.toString();
	}

	private String makeInsertSetClause(Map<Attribute, String> values){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Attribute attribute : values.keySet()){
			if (!first)
				sb.append(", ");
			first = false;
			sb.append(attribute.getName());
		}
		return sb.toString();
	}

	private String makeUpdateSetClause(Map<Attribute, String> values){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Attribute attribute : values.keySet()){
			if (!first)
				sb.append(", ");
			first = false;
			sb.append(attribute.getName()).append(" = ?");
		}
		return sb.toString();
	}

	@Override
	public void delete(int id, ObjectDefinition entity){
		if (isPrimary()){
			final String sql = "update cis_objects set lastmodified = now(), status = ? where id = ?";
			Object[] params = new Object[]{ObjectStatus.Deleted.toInt(), id};
			executeUpdate(sql, params);
		}
	}

	@Override
	public void delete(List<Integer> ids, ObjectDefinition entity){
		if (isPrimary()){
			final String sql = "update cis_objects set lastmodified = now(), status = ? where id in (" + makeIdList(ids)
					+ ")";
			Object[] params = new Object[]{ObjectStatus.Deleted.toInt()};
			executeUpdate(sql, params);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<Integer> getIdList(ObjectDefinition entity){
		if (!isPrimary())
			return Collections.emptyList();
		final String sql = "select u.id from " + getTableName() + " u left join cis_objects co on u.id = co.id "
				+ "where co.status in (" + ObjectStatus.Normal.toInt() + ", " + ObjectStatus.Locked.toInt() + ")";
		log.debug(sql);
		return getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				return rs.getInt(1);
			}
		});
	}

	@Override
	public void deleteContextDataByFavorite(Attribute pkAttribute, int favoriteId){
		final String sql = "delete from " + getTableName() + " where " + pkAttribute.getName() + " = ?";
		executeUpdate(sql, new Object[]{favoriteId});
	}

	@Override
	public void saveOrUpdateContext(int nodeId, Attribute pkAttribute, int topicId){
		String sql = "select count(*) from " + getTableName() + " where id = ? and " + pkAttribute.getName() + " = ?";
		log.debug(sql);
		int count = getJdbcTemplate().queryForInt(sql, new Object[]{nodeId, topicId});
		log.debug("Count: " + count);
		if (count == 0){
			sql = "insert into " + getTableName() + " (id, " + pkAttribute.getName() + ") values (?, ?)";
			executeUpdate(sql, new Object[]{nodeId, topicId});
		}
	}

	@Override
	public void saveOrUpdateContextData(int nodeId, Attribute pkAttribute, int topicId, Map<Attribute, String> values){
		final String sql = "update " + getTableName() + " set " + makeUpdateSetClause(values) + " where id = ? and "
				+ pkAttribute.getName() + " = ?";
		Object[] params = getUpdateValue(values, nodeId, topicId);
		executeUpdate(sql, params);
	}

	@Override
	public void getPredefinedOnly(Collection<Integer> ids, List<Attribute> attributes, Map<Integer, Set<Integer>> results){
		if (attributes.isEmpty())
			return;
		if (ids.isEmpty())
			return;
		final String sql = "select u.id, " + makeAttributeList("u", attributes) + " from " + getTableName() + " u "
				+ "left join cis_objects co on co.id = u.id where co.status in (0, 1) and u.id in (" + makeIdList(ids) + ")";
		extractPredefined(sql, attributes, results);
	}

	@Override
	public Double[] getRowMaxRowSumMaxRowSumMin(List<Attribute> attributes){
		final String sql = generateRowMaxRowSumMaxRowSumMinSQL(attributes);
		return (Double[]) getJdbcTemplate().queryForObject(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int i) throws SQLException{
				return new Double[]{(double)resultSet.getInt(1), (double)resultSet.getInt(2), (double)resultSet.getInt(3)};
			}
		});
	}

	private String generateRowMaxRowSumMaxRowSumMinSQL(List<Attribute> attributes){
		StringBuilder sb = new StringBuilder();
		sb.append("select max(rowMax), max(rowSum), min(rowSum) from ");
		final String attributeNameList = makeAttributeList(null, attributes);
		sb.append("(select greatest(").append(attributeNameList).append(") as rowMax, ").append(attributeNameList.replace(", ", "+")).append(" as rowSum from ");
		sb.append("(select ");
		StringBuilder join = new StringBuilder();
		int predefinedCounter = 1;
		boolean first = true;
		for(Attribute attribute : attributes){
			if(!first)
				sb.append(", ");
			else
				first = false;
			sb.append("coalesce(");
			if(attribute.isPredefined()){
				sb.append("pa").append(predefinedCounter).append(".value::integer");
				join.append(" left join cht_predefinedattributes pa").append(predefinedCounter).append(" on pa").append(predefinedCounter).append(".id = ").append(attribute.getName());
				predefinedCounter++;
			}
			else
				sb.append(attribute.getName());
			sb.append(", 0) as ").append(attribute.getName());
		}
		sb.append(" from ").append(getTableName());
		sb.append(join.toString());
		sb.append(") as subselect) as subsubselect");
		return sb.toString();
	}

	protected void extractPredefined(String sql, final List<Attribute> attributes, final Map<Integer, Set<Integer>> results){
		getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				int id = rs.getInt("id");
				Set<Integer> values = results.get(id);
				if (values == null){
					values = new HashSet<Integer>();
					results.put(id, values);
				}
				for (Attribute attribute : attributes){
					String val = rs.getString(attribute.getName());
					if (val == null)
						continue;
					if (attribute.isMultivalue()){
						values.addAll(multiValueToList(val));
					} else{
						values.add(Integer.parseInt(val));
					}
				}
				return null;
			}
		});

	}

	private List<Integer> multiValueToList(String multiValue){
		if (multiValue == null)
			return Collections.emptyList();
		Matcher matcher = pattern.matcher(multiValue);
		List<Integer> result = new ArrayList<Integer>();
		while (matcher.find()){
			String sVal = matcher.group();
			result.add(Integer.parseInt(sVal.substring(1, sVal.length() - 1)));
		}
		return result;
	}

	@Override
	public void merge(int id, ObjectDefinition entity){
		if (isPrimary){
			final String sql = "update cis_objects set lastmodified = now(), status = ? where id = ?";
			Object[] params = new Object[]{ObjectStatus.Merged.toInt(), id};
			executeUpdate(sql, params);
		}
	}

	@Override
	public String aggregate(Attribute attribute, String operation, Collection<Integer> ids){
		final String sql = "select " + operation + "(" + attribute.getName() + ") from " + getTableName() + " where id in ("
				+ makeIdList(ids) + ")";
		return (String) getJdbcTemplate().queryForObject(sql, String.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<Integer> getNotNull(Attribute attribute){
		final String sql = "select id from " + getTableName() + " where " + attribute.getName() + " is not null";
		Set<Integer> result = new HashSet<Integer>();
		result.addAll(getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				return rs.getInt(1);
			}
		}));
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getContextEdges(int entityId, int favoriteId){
		if (!isPrimary())
			return Collections.emptyList();

		final String sql = "select id from cis_edges where favoritesid = ? and edgetype = ?";
		return getJdbcTemplate().query(sql, new Object[]{favoriteId, entityId}, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				return rs.getInt(1);
			}
		});
	}

	private int getNewId(Integer newObjectId){
		if (newObjectId == null || newObjectId <= 0){
			int nextId = getNewId();
			if (nextId != 0){
				return nextId;
			} else{
				throw new DaoException("Could not generate new object id");
			}
		} else{
			return newObjectId;
		}
	}

	private int getNewId(){
		String query = databaseAdapter.getProcedureSQL("sp_GetSeq", "'ObjectCount'");
		return getJdbcTemplate().queryForInt(query);
	}

	private Object getUpdateValue(Attribute attribute, String val){
		if (val == null || val.isEmpty())
			return null;
		if (attribute.isMultivalue())
			return val;
		switch (attribute.getDatabaseDatatype()){
			case TEXT:
				return val;
			case INT:
				return Integer.parseInt(val);
			case DECIMAL:
				return Double.parseDouble(val);
			default:
				throw new RuntimeException("Don't know how to handle: " + attribute.getDatabaseDatatype());
		}
	}

	protected String makeIdList(Collection<Integer> ids){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Integer id : ids){
			if (!first)
				sb.append(",");
			first = false;
			sb.append(id);
		}
		return sb.toString();
	}

	private String makeWhereClause(Attribute attribute, AdvancedCriteria.Section.ConditionGroup conditionGroup,
								   List<String> params){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		boolean first = true;
		for (AdvancedCriteria.Section.Condition condition : conditionGroup.getConditions()){
			if (!first){
				if (conditionGroup.getConditionConnectionType())
					sb.append(" AND ");
				else
					sb.append(" OR ");
			}
			first = false;
			sb.append(makeWhereClause(attribute, condition, params));
		}
		sb.append(")");
		return sb.toString();
	}

	private String makeWhereClause(Attribute attribute, AdvancedCriteria.Section.Condition condition, List<String> params){
		if (attribute.isMultivalue() && !attribute.isPredefined()){
			if ("=".equals(condition.getOperation()) || "<>".equals(condition.getOperation()))
				condition.setTerm("%{" + condition.getTerm() + "}%");
			return makeStringClause(attribute, condition, params);
		}
		if (attribute.isPredefined())
			return makePredefinedWhereClause(attribute, condition);
		else{
			switch (attribute.getDataType()){
				case DATE:
				case TEXT:
				case URL:
					return makeStringClause(attribute, condition, params);
				case INT:
				case DECIMAL:
				case BOOL:
					return makeNumberClause(attribute, condition);
				default:
					throw new RuntimeException("Invalid date type");
			}
		}
	}

	private String makeNumberClause(Attribute attribute, AdvancedCriteria.Section.Condition condition){
		if ("between".equalsIgnoreCase(condition.getOperation())){
			String[] terms = condition.getTerm().split(",");
			String result = "(u." + attribute.getName() + " " + condition.getOperation() + " " + terms[0] + " and " + terms[1];
			if (condition.getNullAllowed())
				result += " or u." + attribute.getName() + " is null";
			result += ")";
			return result;
		} else
			return "u." + attribute.getName() + condition.getOperation() + condition.getTerm();
	}

	private String makeStringClause(Attribute attribute, AdvancedCriteria.Section.Condition condition, List<String> params){
		boolean exact = "=".equals(condition.getOperation()) || "<>".equals(condition.getOperation())
				|| attribute.getDataType() == DataType.DATE;
		String operation;
		String term = condition.getTerm();
		if ("=".equals(condition.getOperation()) || "~".equals(condition.getOperation()))
			operation = " ILIKE ?";
		else if ("<>".equals(condition.getOperation()) || "!~".equals(condition.getOperation()))
			operation = " NOT ILIKE ?";
		else
			operation = condition.getOperation() + "?";
		String result = "u." + attribute.getName() + operation;
		if (!exact)
			term = "%" + term + "%";
		params.add(term);
		return result;
	}

	private String makePredefinedWhereClause(Attribute attribute, AdvancedCriteria.Section.Condition condition){
		if (attribute.isMultivalue()){
			if ("AtLeastOne".equals(condition.getOperation())){
				return makePredefinedWhereClause(attribute, condition.getTerm(), " OR ", " ILIKE ", condition
						.getNullAllowed(), "'%{", "}%'");
			} else if ("All".equals(condition.getOperation())){
				return makePredefinedWhereClause(attribute, condition.getTerm(), " AND ", " ILIKE ", condition
						.getNullAllowed(), "'%{", "}%'");
			} else if ("NoneOf".equals(condition.getOperation())){
				return makePredefinedWhereClause(attribute, condition.getTerm(), " AND ", " NOT ILIKE ", condition
						.getNullAllowed(), "'%{", "}%'");
			}
			throw new RuntimeException("Unknown operation requested");
		} else{
			if ("NoneOf".equals(condition.getOperation()) || "<>".equals(condition.getOperation())){
				return makePredefinedWhereClause(attribute, condition.getTerm(), " AND ", "<>", true);
			} else{
				return makePredefinedWhereClause(attribute, condition.getTerm(), " OR ", condition.getOperation(), false);
			}
		}
	}

	private String makePredefinedWhereClause(Attribute attribute, String term, String connector, String operation,
											 boolean nullAllowed){
		return makePredefinedWhereClause(attribute, term, connector, operation, nullAllowed, "", "");
	}

	private String makePredefinedWhereClause(Attribute attribute, String term, String connector, String operation,
											 boolean nullAllowed, String prefix, String suffix){
		String[] ids = term.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		boolean first = true;
		for (String id : ids){
			if (!first)
				sb.append(connector);
			first = false;
			if (nullAllowed)
				sb.append("(");
			sb.append("u.").append(attribute.getName()).append(operation).append(prefix).append(id).append(suffix);
			if (nullAllowed)
				sb.append(" or u.").append(attribute.getName()).append(" is null").append(")");
		}
		sb.append(")");
		return sb.toString();
	}

	private void executeUpdate(String sql, Object[] params){
		log.debug(sql);
		if (params != null)
			log.debug(Arrays.toString(params));
		if (params == null)
			getJdbcTemplate().update(sql);
		else
			getJdbcTemplate().update(sql, params);
	}
}
