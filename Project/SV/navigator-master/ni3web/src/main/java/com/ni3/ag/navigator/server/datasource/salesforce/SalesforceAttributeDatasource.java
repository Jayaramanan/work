package com.ni3.ag.navigator.server.datasource.salesforce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ni3.ag.navigator.server.cache.SrcIdToFakeIdCacheImpl;
import com.ni3.ag.navigator.server.cache.SrcIdToIdCache;
import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.server.services.SalesforceConnectionProvider;
import com.ni3.ag.navigator.shared.constants.EditingOption;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.DataType;
import com.sforce.soap.partner.*;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.apache.log4j.Logger;

public class SalesforceAttributeDatasource implements AttributeDataSource{
	private static final Logger log = Logger.getLogger(SalesforceAttributeDatasource.class);
	private static int uniqueInt = 1;
	private boolean primary;
	private SrcIdToFakeIdCacheImpl srcIdToFakeIdCache;
	private SalesforceConnectionProvider salesforceConnectionProvider;

	public void setSalesforceConnectionProvider(SalesforceConnectionProvider salesforceConnectionProvider){
		this.salesforceConnectionProvider = salesforceConnectionProvider;
	}

	public void setSrcIdToIdCache(SrcIdToIdCache srcIdToIdCache){
		this.srcIdToFakeIdCache = (SrcIdToFakeIdCacheImpl) srcIdToIdCache;
	}

	@Override
	public void setPrimary(boolean flag){
		this.primary = flag;
	}

	@Override
	public boolean isPrimary(){
		return primary;
	}

	@Override
	public Collection<Integer> search(Attribute attribute, AdvancedCriteria.Section.Condition condition){
		final String query = "select id from " + attribute.getEntity().getName() + " where " + makeWhereClause(attribute, condition);
		log.debug(query);
		return makeSearch(query, attribute.getEntity());
	}

	@Override
	public Collection<Integer> search(Attribute attribute, AdvancedCriteria.Section.ConditionGroup conditionGroup){
		final String query = "select id from " + attribute.getEntity().getName() + " where " + makeWhereClause(attribute, conditionGroup);
		log.debug(query);
		return makeSearch(query, attribute.getEntity());
	}

	private Collection<Integer> makeSearch(String query, ObjectDefinition entity){
		Collection<Integer> results = new ArrayList<Integer>();
		try{
			final PartnerConnection connection = salesforceConnectionProvider.getConnection();
			QueryResult qr = connection.query(query);
			boolean done = false;
			while (!done){
				final SObject[] records = qr.getRecords();
				for (SObject record : records){
					final int id = srcIdToFakeIdCache.getId(record.getId(), entity);
					if (id == -1)
						continue;
					results.add(id);
				}

				if (qr.isDone()){
					done = true;
				} else{
					qr = connection.queryMore(qr.getQueryLocator());
				}
			}
		} catch (ConnectionException e){
			log.error("Error performing simple search", e);
		}
		return results;
	}

	private String makeWhereClause(Attribute attribute, AdvancedCriteria.Section.ConditionGroup conditionGroup){
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
			sb.append(makeWhereClause(attribute, condition));
		}
		sb.append(")");
		return sb.toString();
	}

	private String makeWhereClause(Attribute attribute, AdvancedCriteria.Section.Condition condition){
		//TODO implement handling of not predefined multivalue attributes
		if (attribute.isMultivalue() && !attribute.isPredefined())
			throw new RuntimeException("Not supported yet");
		if (attribute.isPredefined())
			return makePredefinedWhereClause(attribute, condition);
		else{
			switch (attribute.getDataType()){
				case DATE:
				case TEXT:
				case URL:
					return makeStringClause(attribute, condition);
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
			String result = "(" + attribute.getName() + " " + condition.getOperation() + " " + terms[0] + " and " + terms[1];
			if (condition.getNullAllowed())
				result += " or " + attribute.getName() + " is null";
			result += ")";
			return result;
		} else
			return attribute.getName() + condition.getOperation() + condition.getTerm();
	}

	private String makeStringClause(Attribute attribute, AdvancedCriteria.Section.Condition condition){
		boolean exact = "=".equals(condition.getOperation()) || "<>".equals(condition.getOperation());
		boolean needBrackets = false;
		String prefix = "";
		String operation;
		String term = condition.getTerm();
		if ("=".equals(condition.getOperation()) || "~".equals(condition.getOperation()))
			operation = " LIKE ";
		else if ("<>".equals(condition.getOperation()) || "!~".equals(condition.getOperation())){
			prefix = "NOT ";
			needBrackets = true;
			operation = " LIKE ";
		} else
			throw new RuntimeException("Invalid operation " + condition.getOperation());
		term = term.replaceAll("'", "\\'").replaceAll("\\\\", "\\\\");
		if (!exact)
			term = "'%" + term + "%'";
		else
			term = "'" + term + "'";
		StringBuilder sb = new StringBuilder();
		if (needBrackets)
			sb.append("(");
		sb.append(prefix);
		sb.append(attribute.getName()).append(operation).append(term);
		if (needBrackets)
			sb.append(")");
		return sb.toString();
	}

	private String makePredefinedWhereClause(Attribute attribute, AdvancedCriteria.Section.Condition condition){
		if (attribute.isMultivalue()){
			if ("AtLeastOne".equals(condition.getOperation())){
				return makePredefinedWhereClause(attribute, condition.getTerm(), " OR ", " ILIKE ", condition.getNullAllowed(), "'%{", "}%'");
			} else if ("All".equals(condition.getOperation())){
				return makePredefinedWhereClause(attribute, condition.getTerm(), " AND ", " ILIKE ", condition.getNullAllowed(), "'%{", "}%'");
			} else if ("NoneOf".equals(condition.getOperation())){
				return makePredefinedWhereClause(attribute, condition.getTerm(), " AND ", " NOT ILIKE ", condition.getNullAllowed(), "'%{", "}%'");
			}
			throw new RuntimeException("Unknown operation requested");
		} else{
			if ("NoneOf".equals(condition.getOperation())){
				return makePredefinedWhereClause(attribute, condition.getTerm(), " AND ", "<>", true);
			} else{
				return makePredefinedWhereClause(attribute, condition.getTerm(), " OR ", condition.getOperation(), false);
			}
		}
	}

	private String makePredefinedWhereClause(Attribute attribute, String term, String connector, String operation,
											 boolean nullAllowed){
		String prefix = "";
		String suffix = "";
		if (DataType.TEXT.equals(attribute.getDataType()) || DataType.URL.equals(attribute.getDataType())){
			prefix = "'";
			suffix = "'";
		}
		return makePredefinedWhereClause(attribute, term, connector, operation, nullAllowed, prefix, suffix);
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
			id = attribute.getValue(Integer.parseInt(id)).getValue();
			sb.append(attribute.getName()).append(operation).append(prefix).append(id).append(suffix);
			if (nullAllowed)
				sb.append(" or ").append(attribute.getName()).append(" is null").append(")");
		}
		sb.append(")");
		return sb.toString();
	}


	@Override
	public int createNode(Integer newObjectId, int entityId, int userId, Attribute attribute, String attributeValue){
		ObjectDefinition entity = attribute.getEntity();
		Map<Attribute, String> values = makeMandatoryAttributesValues(entity);
		values.put(attribute, attributeValue);
		SObject sObject = createSObject(values, false);
		PartnerConnection connection = salesforceConnectionProvider.getConnection();
		try{
			SaveResult[] results = connection.create(new SObject[]{sObject});
			if (results == null || results.length <= 0)
				throw new RuntimeException("Error create object for entity: " + attribute.getEntity().getName());
			if (!results[0].getSuccess()){
				logError(results[0]);
				throw new RuntimeException("Error create object for entity: " + attribute.getEntity().getName());
			}
			return srcIdToFakeIdCache.getId(results[0].getId(), attribute.getEntity());
		} catch (ConnectionException e){
			log.error("Cannot create node", e);
			throw new RuntimeException("Error create object for entity: " + attribute.getEntity().getName());
		}
	}

	private Map<Attribute, String> makeMandatoryAttributesValues(ObjectDefinition entity){
		Map<Attribute, String> result = new HashMap<Attribute, String>();
		for (Attribute attribute : entity.getAttributes()){
			if (!isMandatory(attribute))
				continue;
			result.put(attribute, generatePlaceholder(attribute));
		}
		return result;
	}

	private String generatePlaceholder(Attribute attribute){
		if (attribute.isPredefined()){
			if (attribute.getValues().isEmpty())
				return null;
			return "" + attribute.getValues().get(0).getId();
		}
		switch (attribute.getDataType()){
			case DATE:
				return new SimpleDateFormat(DataType.DB_DATE_FORMAT).format(new Date());
			case INT:
			case DECIMAL:
				return "0";
			case BOOL:
				return "false";
			default:
				return attribute.getName() + "_value_" + (++uniqueInt);
		}
	}

	protected boolean isMandatory(Attribute oa){
		for (AttributeGroup ag : oa.getAttributeGroups()){
			if (EditingOption.Mandatory.getValue() == ag.getEditingLock()
					|| EditingOption.Mandatory.getValue() == ag.getEditingUnlock())
				return true;
		}
		return false;
	}

	@Override
	public int createEdge(Integer newObjectId, int fromId, int toId, int entityId, int userId, Attribute attribute, String attributeValue){
		ObjectDefinition entity = attribute.getEntity();
		Schema schema = entity.getSchema();
		int fromEntityId = srcIdToFakeIdCache.getEntityIdById(fromId);
		int toEntityId = srcIdToFakeIdCache.getEntityIdById(toId);
		ObjectDefinition fromEntity = schema.getEntity(fromEntityId);
		ObjectDefinition toEntity = schema.getEntity(toEntityId);
		String toSrcId = srcIdToFakeIdCache.getSrcId(toId);
		String fromSrcId = srcIdToFakeIdCache.getSrcId(fromId);
		SObject sObject = new SObject();
		sObject.setType(toEntity.getName());
		sObject.setField(fromEntity.getName() + "Id", fromSrcId);
		updateObject(toSrcId, sObject);
		String edgeSrcId = fromSrcId + "_" + toSrcId;
		return srcIdToFakeIdCache.getId(edgeSrcId, attribute.getEntity());
	}

	@Override
	public void saveOrUpdate(int id, Map<Attribute, String> values){
		boolean exists = exists(id);
		if (exists)
			updateAttribute(id, values);
		else
			saveAttribute(id, values);
	}

	private void updateAttribute(int id, Map<Attribute, String> values){
		if (values.isEmpty())
			return;
		log.debug("update object in sales force with id: " + id);
		SObject sNode = createSObject(values, true);
		String srcId = srcIdToFakeIdCache.getSrcId(id);
		log.debug("Found mapping: " + id + "->" + srcId);
		updateObject(srcId, sNode);
	}

	private void updateObject(String srcId, SObject sNode){
		PartnerConnection connection = salesforceConnectionProvider.getConnection();
		try{
			sNode.setId(srcId);
			SaveResult[] results = connection.update(new SObject[]{sNode});
			if (results != null && results.length > 0){
				if (!results[0].getSuccess()){
					logError(results[0]);
					throw new RuntimeException("Error update object in sales force with id: " + srcId);
				}
			}
		} catch (ConnectionException e){
			log.error("Cannot update node", e);
			throw new RuntimeException("Error update object in sales force with id: " + srcId);
		}
	}

	private String insertObject(SObject sNode){
		String newId = null;
		PartnerConnection connection = salesforceConnectionProvider.getConnection();
		try{
			final SaveResult[] results = connection.create(new SObject[]{sNode});
			if (results != null && results.length > 0){
				if (results[0].getSuccess()){
					newId = results[0].getId();
				} else{
					logError(results[0]);
				}
			}
		} catch (ConnectionException e){
			log.error("Cannot insert node", e);
		}
		return newId;
	}

	SObject createSObject(Map<Attribute, String> values, boolean allowNull){
		SObject sObject = new SObject();
		sObject.setType(values.keySet().iterator().next().getEntity().getName());
		for (Attribute attribute : values.keySet()){
			Object value = null;
			String val = values.get(attribute);
			if (val != null && !val.isEmpty()){
				if (attribute.isPredefined()){
					Integer pId = Integer.valueOf(val);
					final PredefinedAttribute pa = attribute.getValue(pId);
					if (pa != null){
						value = pa.getValue();
					}
				} else{
					value = formatValue(attribute, val);
				}
			}
			if (value != null || allowNull){
				sObject.setField(attribute.getName(), value);
			}
		}
		return sObject;
	}

	Object formatValue(final Attribute attribute, final String val){
		Object result = null;
		if (val != null && !val.isEmpty()){
			DataType dt = attribute.getDataType();
			if (attribute.isMultivalue() || dt == DataType.TEXT || dt == DataType.URL){
				result = val;
			} else if (dt == DataType.INT){
				try{
					result = Integer.valueOf(val);
				} catch (NumberFormatException ex){
					log.warn("Cannot convert to integer: " + val);
				}
			} else if (dt == DataType.DECIMAL){
				try{
					result = Double.valueOf(val);
				} catch (NumberFormatException ex){
					log.warn("Cannot convert to double: " + val);
				}
			} else if (dt == DataType.DATE){
				try{
					result = new SimpleDateFormat(DataType.DB_DATE_FORMAT).parse(val);
				} catch (ParseException e){
					log.warn("Cannot convert to date: " + val);
				}

			} else if (dt == DataType.BOOL){
				return !val.equals("0");
			}
		}
		return result;
	}


	private void saveAttribute(int id, Map<Attribute, String> values){
		if (values.isEmpty())
			return;
		log.debug("creating object in sales force");
		SObject sNode = createSObject(values, false);
		String newSrcId = insertObject(sNode);
		if (newSrcId == null){
			log.error("Error save object attributes for id: " + id);
			throw new RuntimeException("Failed to create object in SalesForce for id " + id);
		}
		srcIdToFakeIdCache.add(values.keySet().iterator().next().getEntity(), id, newSrcId);
	}

	private boolean exists(int id){
		String srcid = srcIdToFakeIdCache.getSrcId(id);
		return srcid != null;
	}

	@Override
	public void delete(int id, ObjectDefinition entity){
		if (!isPrimary())
			return;
		log.debug("Entity for id is: " + entity.getName());
		if (entity.isNode())
			deleteNode(id);
		else
			deleteEdge(id, entity);
	}

	private void deleteEdge(int id, ObjectDefinition entity){
		String srcId = srcIdToFakeIdCache.getSrcId(id);
		log.debug("Mapping " + id + "->" + srcId);

		String[] objectSrcIds = srcId.split("_");
		String fromSrcId = objectSrcIds[0];
		String toSrcId = objectSrcIds[1];
		log.debug("Deleting edge between nodes `" + fromSrcId + "` and `" + toSrcId + "`");

		int fromId = srcIdToFakeIdCache.getId(fromSrcId, null);
		int toId = srcIdToFakeIdCache.getId(toSrcId, null);
		log.debug("Mapped objects: from: " + fromId + " | toId: " + toId);

		ObjectDefinition fromEntity = entity.getSchema().getEntity(srcIdToFakeIdCache.getEntityIdById(fromId));
		ObjectDefinition toEntity = entity.getSchema().getEntity(srcIdToFakeIdCache.getEntityIdById(toId));
		log.debug("From entity: " + fromEntity.getName());
		log.debug("To entity: " + toEntity.getName());

		SObject sObject = new SObject();
		sObject.setType(toEntity.getName());
		sObject.setFieldsToNull(new String[]{fromEntity.getName() + "Id"});
		updateObject(toSrcId, sObject);
	}

	private void deleteNode(int id){
		String srcId = srcIdToFakeIdCache.getSrcId(id);
		log.debug("Mapping " + id + "->" + srcId);
		PartnerConnection connection = salesforceConnectionProvider.getConnection();
		try{
			final DeleteResult[] results = connection.delete(new String[]{srcId});
			if (results != null && results.length > 0){
				if (!results[0].getSuccess()){
					logError(results[0]);
					throw new RuntimeException("Error delete object from salesForce: " + id + "(" + srcId + ")");
				}
			}
		} catch (ConnectionException e){
			log.error("Cannot delete object " + id, e);
			throw new RuntimeException("Error delete object from salesForce: " + id + "(" + srcId + ")");
		}
	}

	@Override
	public void merge(int id, ObjectDefinition entity){
		delete(id, entity);
	}

	@Override
	//TODO rewrite get to retrive operation instead of query
	public void get(Collection<Integer> ids, final List<Attribute> attributes, Map<Integer, DBObject> results){
		if (ids.isEmpty())
			return;
		if (attributes.isEmpty())
			return;
		ObjectDefinition entity = attributes.get(0).getEntity();
		if (entity.isEdge() && isPrimary())
			return;
		List<Integer> localIds = new ArrayList<Integer>();
		localIds.addAll(ids);
		while (!localIds.isEmpty()){
			int count = Math.min(localIds.size(), 300);
			log.debug("Getting chunk of data: " + localIds.size() + "/" + ids.size() + " left");
			List<Integer> currentIds = localIds.subList(0, count);
			String query = "select id, " + makeAttributeList(attributes) + " from " + attributes.get(0).getEntity().getName() + " where id in (" +
					makeIdList(currentIds) + ")";
			getDataForQuery(query, attributes, results);
			currentIds.clear();
		}
	}

	@Override
	//TODO rewrite get to retrive operation instead of query
	public void get(List<Attribute> attributes, Map<Integer, DBObject> results){
		if (attributes.isEmpty())
			return;
		String query = "select id, " + makeAttributeList(attributes) + " from " + attributes.get(0).getEntity().getName();
		getDataForQuery(query, attributes, results);
	}

	@Override
	public void getContext(Collection<Integer> ids, Attribute pkAttribute, String contextKey, List<Attribute> attributes, Map<Integer, DBObject> results){
	}

	private String makeAttributeList(List<Attribute> attributes){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Attribute attribute : attributes){
			if (!first) sb.append(", ");
			first = false;
			sb.append(attribute.getName());
		}
		return sb.toString();
	}

	private void getDataForQuery(String query, List<Attribute> attributes, Map<Integer, DBObject> results){
		if (attributes.isEmpty())
			return;
		ObjectDefinition entity = attributes.get(0).getEntity();
		log.debug(query);
		try{
			final PartnerConnection connection = salesforceConnectionProvider.getConnection();
			QueryResult qr = connection.query(query);
			boolean done = false;
			while (!done){
				final SObject[] records = qr.getRecords();
				for (SObject record : records){
					final int id = srcIdToFakeIdCache.getId(record.getId(), entity);
					if (id == -1)
						continue;
					DBObject obj = results.get(id);
					if (obj == null){
						obj = new DBObject(id, attributes.get(0).getEntity().getId());
						obj.setData(new LinkedHashMap<Integer, String>());
						results.put(id, obj);
					}
					for (Attribute attribute : attributes){
						Object o = record.getField(attribute.getName());
						String val = makeValue(attribute, o);
						if (val != null)
							obj.getData().put(attribute.getId(), val);
					}
				}

				if (qr.isDone()){
					done = true;
				} else{
					qr = connection.queryMore(qr.getQueryLocator());
				}
			}
		} catch (ConnectionException e){
			log.error("Error performing simple search", e);
		}
	}

	private String makeValue(Attribute attribute, Object o){
		if (o == null)
			return null;
		if ("null".equals(o))
			return null;
		if (attribute.isPredefined()){
			PredefinedAttribute pa = attribute.getPredefinedValueByValue("" + o);
			if (pa == null)
				return null;
			return "" + pa.getId();
		} else{
			if (DataType.INT.equals(attribute.getDataType())){
				String sVal = "" + o;
				if ("false".equalsIgnoreCase(sVal))
					return "0";
				else if ("true".equalsIgnoreCase(sVal))
					return "1";
				else
					return "" + o;
			} else if (DataType.DATE.equals(attribute.getDataType())){
				String sVal = (String) o;
				try{
					Date d = new SimpleDateFormat(DataType.SALESFORCE_DATE_FORMAT).parse(sVal);
					return new SimpleDateFormat(DataType.DB_DATE_FORMAT).format(d);
				} catch (ParseException e){
					log.error("ERROR", e);
					return null;
				}
			} else
				return "" + o;
		}
	}

	private String makeIdList(Collection<Integer> ids){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Integer id : ids){
			String src = srcIdToFakeIdCache.getSrcId(id);
			if (src == null)
				continue;
			if (!first)
				sb.append(",");
			first = false;
			sb.append("'").append(src).append("'");
		}
		return sb.toString();
	}

	@Override
	public String aggregate(Attribute attribute, String operation, Collection<Integer> ids){
		if (ids == null || ids.isEmpty())
			return null;
		return null;
	}

	@Override
	public Collection<Integer> getNotNull(Attribute attribute){
		return null;
	}

	@Override
	public List<Integer> getContextEdges(int entityId, int favoriteId){
		return null;
	}

	@Override
	public void delete(List<Integer> ids, ObjectDefinition entity){
		for (Integer id : ids)
			delete(id, entity);
	}

	@Override
	public Collection<Integer> getIdList(ObjectDefinition entity){
		final String sql = "select id from " + entity.getName();
		Map<Integer, DBObject> results = new HashMap<Integer, DBObject>();
		getDataForQuery(sql, new ArrayList<Attribute>(), results);
		return results.keySet();
	}

	@Override
	public void deleteContextDataByFavorite(Attribute pkAttribute, int favoriteId){
	}

	@Override
	public void saveOrUpdateContext(int nodeId, Attribute pkAttribute, int topicId){
	}

	@Override
	public void saveOrUpdateContextData(int nodeId, Attribute pkAttribute, int topicId, Map<Attribute, String> attributeStringMap){
	}

	@Override
	public void getPredefinedOnly(Collection<Integer> ids, List<Attribute> attributes, Map<Integer, Set<Integer>> results){
		Map<Integer, DBObject> tempResults = new HashMap<Integer, DBObject>();
		get(ids, attributes, tempResults);//get results as usual
		for (Integer id : tempResults.keySet()){  //transfering values to map of sets
			Set<Integer> values = results.get(id);
			if (values == null){
				values = new HashSet<Integer>();
				results.put(id, values);
			}
			DBObject tempObject = tempResults.get(id);
			for (Attribute attribute : attributes){
				String val = tempObject.getData().get(attribute.getId());
				if (val == null)
					continue;
				Integer pid = Integer.parseInt(makeValue(attribute, val));
				if (pid != null)
					values.add(pid);
			}
		}
	}

	@Override
	public Double[] getRowMaxRowSumMaxRowSumMin(List<Attribute> attributes){
		return new Double[0];
	}

	private void logError(SaveResult result){
		final com.sforce.soap.partner.Error[] errors = result.getErrors();
		log.error("Cannot insert node");
		if (errors != null){
			for (Error e : errors){
				log.error(e);
			}
		}
	}

	private void logError(DeleteResult result){
		final com.sforce.soap.partner.Error[] errors = result.getErrors();
		log.error("Cannot insert node");
		if (errors != null){
			for (Error e : errors){
				log.error(e);
			}
		}
	}
}
