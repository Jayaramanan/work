package com.ni3.ag.navigator.server.datasource.postgres;

import java.util.*;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.shared.domain.DBObject;
import org.apache.log4j.Logger;

public class DefaultPostgreSQLDataSource extends PostgreSqlAttributeDatasource{
	private static final Logger log = Logger.getLogger(DefaultPostgreSQLDataSource.class);
	private static final String NAME_PATTERN = "[^a-zA-Z0-9_]*";
	private static final String[] fixedNodeAttributes = {"lat", "lon", "iconname"};
	private static final String[] fixedEdgeAttributes = {"fromid", "toid", "directed", "cmnt", "strength", "inpath", "connectiontype", "favoritesid"};
	private ThreadLocal<String> tableName = new ThreadLocal<String>();

	@Override
	public List<Integer> search(Attribute attribute, AdvancedCriteria.Section.Condition condition){
		setTableName(attribute);
		return super.search(attribute, condition);
	}

	@Override
	public List<Integer> search(Attribute attribute, AdvancedCriteria.Section.ConditionGroup conditionGroup){
		setTableName(attribute);
		return super.search(attribute, conditionGroup);
	}

	private void setTableName(Attribute attribute){
		String currentTableName = getTableNameForAttribute(attribute);
		setTableName(currentTableName);
	}

	public static String getTableNameForEntity(ObjectDefinition entity){
		Schema schema = entity.getSchema();
		return "usr_" + schema.getName().replaceAll(NAME_PATTERN, "") + "_" + entity.getName().replaceAll(NAME_PATTERN, "");
	}

	private static String getTableNameForAttribute(Attribute attribute){
		ObjectDefinition entity = attribute.getEntity();
		Schema schema = entity.getSchema();
		String currentTableName = "usr_" + schema.getName().replaceAll(NAME_PATTERN, "") + "_" + entity.getName().replaceAll(NAME_PATTERN, "");
		if (isFixedNodeAttribute(attribute) && entity.isNode())
			currentTableName = "cis_nodes";
		else if (isFixedEdgeAttribute(attribute) && entity.isEdge())
			currentTableName = "cis_edges";
		if(attribute.isInContext())
			currentTableName += "_ctxt";
		return currentTableName;
	}

	private static boolean isFixedEdgeAttribute(Attribute attribute){
		return isFixedAttribute(fixedEdgeAttributes, attribute);
	}

	private static boolean isFixedNodeAttribute(Attribute attribute){
		return isFixedAttribute(fixedNodeAttributes, attribute);
	}

	private static boolean isFixedAttribute(String[] fixedAttributes, Attribute attribute){
		for (String s : fixedAttributes)
			if (s.equalsIgnoreCase(attribute.getName()))
				return true;
		return false;
	}

	@Override
	public int createNode(Integer newObjectId, int entityId, int userId, Attribute attribute, String attributeValue){
		setTableName(getTableNameForEntity(attribute.getEntity()));
		return super.createNode(newObjectId, entityId, userId, attribute, attributeValue);
	}

	@Override
	public int createEdge(Integer newObjectId, int fromId, int toId, int entityId, int userId, Attribute attribute, String attributeValue){
		setTableName(getTableNameForEntity(attribute.getEntity()));
		return super.createEdge(newObjectId, fromId, toId, entityId, userId, attribute, attributeValue);
	}

	@Override
	public void saveOrUpdate(int id, Map<Attribute, String> attributeValueMap){
		if(attributeValueMap.isEmpty())
			return;
		ObjectDefinition entity = attributeValueMap.keySet().iterator().next().getEntity();
		Map<Attribute, String> fixedMap = new HashMap<Attribute, String>();
		Attribute[] attributes = attributeValueMap.keySet().toArray(new Attribute[attributeValueMap.keySet().size()]);
		for (Attribute attribute : attributes){
			if ((entity.isNode() && isFixedNodeAttribute(attribute)) || (entity.isEdge() && isFixedEdgeAttribute(attribute))){
				fixedMap.put(attribute, attributeValueMap.get(attribute));
				attributeValueMap.remove(attribute);
			}
			if(attribute.isInContext()){
				attributeValueMap.remove(attribute);
			}
		}
		if(!attributeValueMap.isEmpty()){
			setTableName(getTableNameForEntity(entity));
			super.saveOrUpdate(id, attributeValueMap);
		}
		if(!fixedMap.isEmpty()){
			if(entity.isNode())
				setTableName("cis_nodes");
			else
				setTableName("cis_edges");
			super.saveOrUpdate(id, fixedMap);
		}
	}

	@Override
	public void get(Collection<Integer> ids, List<Attribute> attributes, Map<Integer, DBObject> results){
		if (attributes.isEmpty())
			return;
		if(ids.isEmpty())
			return;
		final String sql = generateSelectSQL(attributes, ids, null);
		extract(sql, attributes, results);
	}

	@Override
	public void get(List<Attribute> attributes, Map<Integer, DBObject> results){
		if (attributes.isEmpty())
			return;
		final String sql = generateSelectSQL(attributes, null, null);
		extract(sql, attributes, results);
	}

	@Override
	public void getContext(Collection<Integer> ids, Attribute pkAttribute, String contextKey, List<Attribute> attributes,
						   Map<Integer, DBObject> results){
		if(attributes.isEmpty())
			return;
		final String sql = generateSelectSQL(attributes, ids, pkAttribute);
		extract(sql, attributes, results, new Object[]{Integer.parseInt(contextKey)}, false);
	}

	private String generateSelectSQL(List<Attribute> attributes, Collection<Integer> ids, Attribute contextPkAttribute){
		ObjectDefinition entity = attributes.get(0).getEntity();
		String cisTable;
		if (entity.isNode())
			cisTable = "cis_nodes";
		else
			cisTable = "cis_edges";
		StringBuilder sb = new StringBuilder();
		sb.append("select o.id, ").append(makeAttributeList("c", "u", entity, attributes, contextPkAttribute == null))
				.append(" from cis_objects o right join ").append(cisTable).append(" c on c.id = o.id ");
		String usrTable = getUserTable(attributes);
		if(usrTable == null && ids == null)
			usrTable = getTableNameForEntity(entity);
		if (usrTable != null)
			sb.append(" right join ").append(usrTable).append(" u on u.id=o.id ");
		log.trace(sb.toString());
		boolean whereAdded = false;
		if(ids != null){
			sb.append(" where o.id in (").append(makeIdList(ids)).append(")");
			whereAdded = true;
		}
		if(contextPkAttribute != null){
			if(!whereAdded)
				sb.append(" where ");
			else
				sb.append(" and ");
			sb.append("u.").append(contextPkAttribute.getName()).append(" = ?");
		}
		return sb.toString();
	}

	private String getUserTable(List<Attribute> attributes){
		Attribute usrAttribute = null;
		for (Attribute attribute : attributes){
			if (!isFixedEdgeAttribute(attribute) && !isFixedNodeAttribute(attribute)){
				usrAttribute = attribute;
				break;
			}
		}
		if(usrAttribute == null)
			return null;
		return getTableNameForAttribute(usrAttribute);
	}

	private String makeAttributeList(String cisPrefix, String usrPrefix, ObjectDefinition entity, List<Attribute> attributes, boolean ignoreContext){
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for(Attribute attribute : attributes){
			if(attribute.isInContext() && ignoreContext)
				continue;
			if(!first)
				sb.append(", ");
			first = false;
			if((entity.isEdge() && isFixedEdgeAttribute(attribute)) || (entity.isNode() && isFixedNodeAttribute(attribute)))
				sb.append(cisPrefix).append(".").append(attribute.getName());
			else
				sb.append(usrPrefix).append(".").append(attribute.getName());
		}
		return sb.toString();
	}

	@Override
	public String aggregate(Attribute attribute, String operation, Collection<Integer> ids){
		if(ids.isEmpty())
			return null;
		setTableName(attribute);
		return super.aggregate(attribute, operation, ids);
	}

	@Override
	public Collection<Integer> getNotNull(Attribute attribute){
		setTableName(attribute);
		return super.getNotNull(attribute);
	}

	@Override
	public Collection<Integer> getIdList(ObjectDefinition entity){
		setTableName(getTableNameForEntity(entity));
		return super.getIdList(entity);
	}

	@Override
	public void saveOrUpdateContext(int nodeId, Attribute pkAttribute, int topicId){
		setTableName(getTableNameForAttribute(pkAttribute));
		super.saveOrUpdateContext(nodeId, pkAttribute, topicId);
	}

	@Override
	public void saveOrUpdateContextData(int nodeId, Attribute pkAttribute, int topicId, Map<Attribute, String> values){
		setTableName(getTableNameForAttribute(pkAttribute));
		super.saveOrUpdateContextData(nodeId, pkAttribute, topicId, values);
	}

	@Override
	public Double[] getRowMaxRowSumMaxRowSumMin(List<Attribute> attributes){
		if(attributes.isEmpty())
			return new Double[]{0., 0., 0.};
		setTableName(getTableNameForAttribute(attributes.get(0)));
		return super.getRowMaxRowSumMaxRowSumMin(attributes);
	}

	@Override
	public void deleteContextDataByFavorite(Attribute attribute, int favoriteId){
		setTableName(attribute);
		super.deleteContextDataByFavorite(attribute, favoriteId);
	}

	@Override
	public void getPredefinedOnly(Collection<Integer> ids, List<Attribute> attributes, Map<Integer, Set<Integer>> results){
		if(attributes.isEmpty())
			return;
		setTableName(attributes.get(0));
		if (ids.isEmpty())
			return;
		final String sql = generateSelectSQL(attributes, ids, null);
		extractPredefined(sql, attributes, results);
	}

	@Override
	public void setTableName(String tableName){
		this.tableName.set(tableName);
	}

	@Override
	public String getTableName(){
		return tableName.get();
	}
}
