package com.ni3.ag.navigator.server.services.impl;

import java.util.*;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.server.search.ListIdCriteria;
import com.ni3.ag.navigator.server.search.SimpleCriteria;
import com.ni3.ag.navigator.server.services.GroupScopeProvider;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.server.services.SearchService;
import com.ni3.ag.navigator.server.services.UserDataService;
import com.ni3.ag.navigator.server.servlets.GraphServlet;
import com.ni3.ag.navigator.server.util.Utility;
import com.ni3.ag.navigator.shared.constants.QueryType;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.DataType;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SearchServiceImpl extends JdbcDaoSupport implements SearchService{
	private static final Logger log = Logger.getLogger(SearchServiceImpl.class);
//	private static final String LON_ATTRIBUTE_NAME = "lon";
//	private static final String LAT_ATTRIBUTE_NAME = "lat";

	private GroupDAO groupDAO;
	private SchemaLoaderService schemaLoaderService;
	private UserDataService userDataService;
	private GroupScopeProvider groupScopeProvider;

	public void setGroupScopeProvider(GroupScopeProvider groupScopeProvider){
		this.groupScopeProvider = groupScopeProvider;
	}

	public void setUserDataService(UserDataService userDataService){
		this.userDataService = userDataService;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setSchemaLoaderService(SchemaLoaderService schemaLoaderService){
		this.schemaLoaderService = schemaLoaderService;
	}

	@Override
	public Collection<DBObject> performSimpleSearch(SimpleCriteria criteria){
		log.debug("String passed for search: " + criteria.getTerm());
		final String cleanTerm = criteria.getTerm().replaceAll("\t+", " ").replaceAll("\\s+/g", " ").replaceAll("%", "")
				.replaceAll("'", "''").trim();
		log.debug("Perform search for string: " + cleanTerm);
		if (cleanTerm.isEmpty()){
			log.warn("Search term is empty value - ignore request, do nothing");
			return null;
		}
		criteria.setTerm(cleanTerm);

		Schema schema = schemaLoaderService.getSchema(criteria.getSchema());
		if (schema == null){
			log.error("Invalid schema to search");
			return null;
		}
		log.debug("Performing search on schema: " + schema.getId());

		Group group = groupDAO.getByUser(criteria.getUser().getId());
		log.debug("User group: " + group.getId());

		AdvancedCriteria advancedCriteria = makeCriteriaFromSimple(schema, criteria, group);
		if (advancedCriteria == null)
			return new ArrayList<DBObject>();
		return performAdvancedSearch(advancedCriteria);
	}

	private AdvancedCriteria makeCriteriaFromSimple(Schema schema, SimpleCriteria criteria, Group group){
		String[] terms = criteria.getTerm().split("\\s");
		terms = correctTerm(terms);
		AdvancedCriteria ac = new AdvancedCriteria();
		ac.setSections(new ArrayList<AdvancedCriteria.Section>());
		if (terms.length == 0)
			return null;

		for (ObjectDefinition e : schema.getDefinitions()){
			log.debug("Processing object: " + e.getName());
			if (!isVisibleForUser(e, group))
				continue;
			log.debug("Object `" + e.getName() + "`is visible for user - performing query");

			AdvancedCriteria.Section section = new AdvancedCriteria.Section();
			section.setEntity(e.getId());
			for (Attribute a : e.getAttributes()){
				if (!a.isInSimpleSearch() || !a.canGroupRead(group.getId()))
					continue;
				if (a.isPredefined()){
					makeConditionForPredefined(a, section, terms);
				} else{
					AdvancedCriteria.Section.ConditionGroup cg = new AdvancedCriteria.Section.ConditionGroup(a.getId());
					for (String s : terms){
						AdvancedCriteria.Section.Condition c = makeCondition(s, a);
						if (c != null)
							cg.addCondition(c);
					}
					if (!cg.getConditions().isEmpty())
						section.getConditionGroups().add(cg);
					cg.setConditionConnectionType(containsTermModificator(terms));
				}
			}
			if (!section.getConditionGroups().isEmpty())
				ac.getSections().add(section);
		}

		ac.setFilteredValues(criteria.getFilteredValues());
		ac.setQueryType(QueryType.NODE.getValue());
		ac.setLimit(criteria.getLimit());
		ac.setSchema(criteria.getSchema());
		ac.setUser(criteria.getUser());
		return ac;
	}

	private void makeConditionForPredefined(Attribute a, AdvancedCriteria.Section section, String[] terms){
		if (a.getValues() == null || a.getValues().isEmpty())
			return;
		List<PredefinedAttribute> matchingAttributes = new ArrayList<PredefinedAttribute>();
		for (PredefinedAttribute pa : a.getValues()){
			if (valueMatchingTerms(pa, terms))
				matchingAttributes.add(pa);
		}
		if (matchingAttributes.isEmpty())
			return;
		if (a.isMultivalue()){
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (PredefinedAttribute pa : matchingAttributes){
				if (!first)
					sb.append(",");
				first = false;
				sb.append(pa.getId());
			}

			section.createConditionGroup(a.getId()).addCondition(new AdvancedCriteria.Section.Condition(a.getId(), "AtLeastOne", sb.toString(), false));
		} else{
			AdvancedCriteria.Section.ConditionGroup cg = section.createConditionGroup(a.getId());
			for (PredefinedAttribute pa : matchingAttributes){
				cg.addCondition(new AdvancedCriteria.Section.Condition(a.getId(), "=", "" + pa.getId(), false));
			}
			cg.setConditionConnectionType(false);
		}
	}

	private boolean valueMatchingTerms(PredefinedAttribute pa, String[] terms){
		boolean hasMods = containsTermModificator(terms);
		boolean wasAnyTrue = false;
		for (String term : terms){
			boolean current;
			if (term.startsWith("+"))
				term = term.substring(1);
			if (term.startsWith("-")){
				term = term.substring(1);
				current = !pa.getLabel().toUpperCase().contains(term.toUpperCase());
			} else{
				current = pa.getLabel().toUpperCase().contains(term.toUpperCase());
			}
			if (!current && hasMods)
				return false;
			wasAnyTrue |= current;
		}
		return wasAnyTrue;
	}

	private String[] correctTerm(String[] terms){
		List<String> result = new ArrayList<String>();
		for (String s : terms){
			s = s.trim();
			s = s.replaceAll("\\{", "").replaceAll("\\}", "");
			if (!s.isEmpty() && !s.equals("-") && !s.equals("+"))
				result.add(s);
		}
		return result.toArray(new String[result.size()]);
	}

	private AdvancedCriteria.Section.Condition makeCondition(String s, Attribute a){
		boolean negative = s.startsWith("-");
		if (s.startsWith("+") || negative)
			s = s.substring(1);
		String operation;
		boolean exact = false;
		if (s.startsWith("\"") && s.endsWith("\"")){
			s = s.substring(1, s.length() - 1);
			exact = true;
		}
		//for numeric dataTypes use only exact match
		exact |= a.getDatabaseDatatype().equals(DataType.INT) ||
				a.getDatabaseDatatype().equals(DataType.DECIMAL) ||
				a.getDatabaseDatatype().equals(DataType.BOOL);
		if (!isValueAccpetable(a, s))
			return null;

		if (a.isMultivalue()){
			if (negative)
				operation = "!~";
			else
				operation = "~";
		} else{
			if (exact){
				if (negative)
					operation = "<>";
				else
					operation = "=";
			} else{
				if (negative)
					operation = "!~";
				else
					operation = "~";
			}
		}
		return new AdvancedCriteria.Section.Condition(a.getId(), operation, s, false);
	}

	private boolean isValueAccpetable(Attribute a, String s){
		switch (a.getDataType()){
			case INT:
				try{
					Integer.parseInt(s);
					return true;
				} catch (NumberFormatException ignore){
					return false;
				}
			case DECIMAL:
				try{
					Double.parseDouble(s);
					return true;
				} catch (NumberFormatException ignore){
					return false;
				}
			default:
				return true;
		}
	}

	private boolean containsTermModificator(String[] terms){
		for (String s : terms){
			if (s.startsWith("+") || s.startsWith("-"))
				return true;
		}
		return false;
	}

	@Override
	public Collection<DBObject> performAdvancedSearch(final AdvancedCriteria criteria){
		Schema schema = schemaLoaderService.getSchema(criteria.getSchema());
		if (schema == null){
			log.error("Invalid schema to search");
			return null;
		}
		log.debug("Performing search on schema: " + schema.getId());

		Group group = groupDAO.getByUser(criteria.getUser().getId());
		log.debug("User group: " + group.getId());
		makeGeoCriteria(criteria, schema);

		switch (QueryType.getByValue(criteria.getQueryType())){
			case NODE:
				return performNodeAdvancedSearch(criteria, schema, group);
			case LINKED_NODES:
				return performLinkedNodesAdvancedSearch(criteria, schema, group);
			case NODE_WITH_CONNECTIONS:
				return performNodesWithConnectionsAdvancedSearch(criteria, schema, group);
		}
		throw new RuntimeException("Unimplemented yet");
	}

	private void makeGeoCriteria(AdvancedCriteria criteria, Schema schema){
		String geoCriteria = criteria.getGeoCondition();
		log.debug("GEO criteria: " + geoCriteria);
		if (geoCriteria == null || geoCriteria.trim().isEmpty())
			return;
		String[] coords = geoCriteria.trim().split(",");
		if (coords.length != 4){
			log.error("Invalid geo criteria - should be 4 numbers separated with comma, but is: " + geoCriteria);
			return;
		}
		for (AdvancedCriteria.Section s : criteria.getSections()){
			ObjectDefinition entity = schema.getEntity(s.getEntity());
			if (!entity.isNode())
				continue;
			if (s.getConditionGroups().isEmpty() && s.getConditions().isEmpty()){
				log.debug("No any criteria given for entity: " + entity.getName());
				log.debug("Will not add geo criteria to criteria set");
				continue;
			}
			log.debug("Add geo criteria for entity: " + entity.getName());
			Attribute lon = entity.getAttribute("lon");
			Attribute lat = entity.getAttribute("lat");
			if (lon == null || lat == null){
				log.warn("Cannot find lon or lat attributes in " + entity.getName() + " ->  lon=" + lon + ", lat=" + lat);
				continue;
			}
			s.addCondition(new AdvancedCriteria.Section.Condition(lon.getId(), "between", coords[0] + "," + coords[1], false));
			s.addCondition(new AdvancedCriteria.Section.Condition(lat.getId(), "between", coords[2] + "," + coords[3], false));
		}
	}

	@Override
	public Collection<DBObject> performGetList(ListIdCriteria criteria){
		Schema schema = schemaLoaderService.getSchema(criteria.getSchema());
		if (schema == null){
			log.error("Invalid schema to search");
			return null;
		}
		log.debug("Performing search on schema: " + schema.getId());

		Map<Integer, DBObject> results = new HashMap<Integer, DBObject>();
		for (Integer entityId : criteria.getRequestIdMap().keySet()){
			ObjectDefinition entity = schema.getEntity(entityId);
			results.putAll(userDataService.getDataForIdList(entity, criteria.getRequestIdMap().get(entityId)));
		}
		ListIdCriteria.ContextData contextData = criteria.getContextData();
		Map<Integer, DBObject> contextResults;
		if (contextData != null){
			contextResults = performGetListContext(schema, criteria);
			mergeData(results, contextResults);
		}
		return results.values();
	}

	private void mergeData(Map<Integer, DBObject> results, Map<Integer, DBObject> contextResults){
		for (Integer id : contextResults.keySet()){
			DBObject object = results.get(id);
			if (object == null)
				continue;
			object.getData().putAll(contextResults.get(id).getData());
		}
	}

	private Map<Integer, DBObject> performGetListContext(Schema schema, ListIdCriteria criteria){
		Map<Integer, DBObject> results = new HashMap<Integer, DBObject>();
		for (Integer entityId : criteria.getRequestIdMap().keySet()){
			ObjectDefinition entity = schema.getEntity(entityId);
			results.putAll(
					userDataService.getContextDataForIdList(entity,
							criteria.getContextData().getContextId(),
							criteria.getContextData().getKey(),
							criteria.getRequestIdMap().get(entityId)));
		}
		return results;
	}

	@Override
	public Collection<DBObject> performGetListUnknown(ListIdCriteria criteria){
		Schema schema = schemaLoaderService.getSchema(criteria.getSchema());
		if (schema == null){
			log.error("Invalid schema to search");
			return null;
		}
		log.debug("Performing search on schema: " + schema.getId());
		List<Integer> ids = criteria.getRequestIdMap().get(0);
		Map<Integer, DBObject> results = new HashMap<Integer, DBObject>();
		for (ObjectDefinition e : schema.getDefinitions()){
			Map<Integer, DBObject> currentResults = userDataService.getDataForIdList(e, ids);
			for(DBObject o : currentResults.values()){
				if(!o.getData().isEmpty())
					results.put(o.getId(), o);
			}

		}
		return results.values();
	}

	private Collection<DBObject> performNodesWithConnectionsAdvancedSearch(AdvancedCriteria criteria, Schema schema, Group group){
		AdvancedCriteria.Section nodeSection = criteria.getSections().get(0);
		AdvancedCriteria.Section edgeSection = criteria.getSections().get(1);

		ObjectDefinition nodeEntity = schema.getEntity(nodeSection.getEntity());
		ObjectDefinition edgeEntity = schema.getEntity(edgeSection.getEntity());

		List<Integer> searchedEdgeIds = makeConditionalSearch(criteria, edgeSection, edgeEntity, false);
		List<Integer> searchedNodeIds = makeConditionalSearch(criteria, nodeSection, nodeEntity, false);

		log.debug("Searched nodes: " + searchedNodeIds.size());
		log.debug("Searched edges: " + searchedEdgeIds.size());

		List<Integer> scopedEdges = groupScopeProvider.getEdgeScope(group.getId());
		List<Integer> scopedNodes = groupScopeProvider.getNodeScope(group.getId());
		log.debug("Edge scope size: " + scopedEdges.size());
		log.debug("Node scope size: " + scopedNodes.size());

		if (!scopedNodes.isEmpty())
			searchedNodeIds.retainAll(scopedNodes);

		if (!scopedEdges.isEmpty())
			searchedEdgeIds.retainAll(scopedEdges);

		searchedEdgeIds = applyFilter(searchedEdgeIds, criteria, edgeEntity);
		searchedNodeIds = applyFilter(searchedNodeIds, criteria, nodeEntity);

		if (searchedEdgeIds.isEmpty() || searchedNodeIds.isEmpty())
			return Collections.emptySet();

		//TODO refactor nav client to "wait" node id array instead of edge id array
		//TODO and change filter method of graph below
		GraphNi3Engine graph = GraphServlet.getGraph(schema.getId());
		searchedEdgeIds = graph.filterEdgesByNodes(searchedEdgeIds, searchedNodeIds, criteria.getLimit());
		log.debug("Edges after filter: " + searchedEdgeIds.size());

		Map<Integer, DBObject> results = new HashMap<Integer, DBObject>();
		results.putAll(userDataService.getDataForIdList(edgeEntity, searchedEdgeIds));
		return results.values();
	}

	private List<Integer> applyFilter(List<Integer> idList, AdvancedCriteria criteria, ObjectDefinition entity){
		log.debug("Applying filter of result for entity: " + entity.getId() + " -> " + idList.size());
		if (idList.isEmpty())
			return idList;
		DataFilter df = new DataFilter(entity.getSchema(), criteria.getFilteredValues(), false);
		if (!df.getFilter().containsKey(entity.getId())){
			log.debug("No filter found for entity: " + entity.getId());
			return idList;
		}
		log.debug("Found filter for entity: " + entity.getId());
		Map<Integer, Set<Integer>> filteredAttributes = df.getFilter().get(entity.getId());
		for (Integer attributeId : filteredAttributes.keySet()){
			log.debug("Found filter for attribute: " + attributeId);
			Set<Integer> filteredValues = filteredAttributes.get(attributeId);
			log.debug("Attribute is filtered by values: " + filteredValues);
			Attribute attribute = entity.getAttribute(attributeId);
			String dataSource = attribute.getDataSource();
			if (dataSource == null)
				log.error("Attribute " + attribute.getLabel() + "(" + attribute.getName() + ") is not mapped to any dataSource");
			AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(dataSource);
			final String filteredValuesStr = Utility.listToString(new ArrayList<Integer>(filteredValues));
			AdvancedCriteria.Section.Condition filterCondition = new AdvancedCriteria.Section.Condition(attributeId,
					"NoneOf", filteredValuesStr, true);
			Collection<Integer> filteredIds = attributeDataSource.search(attribute, filterCondition);
			idList.retainAll(filteredIds);
			if (idList.isEmpty())//if filter removed all objects stop any filtering
				return idList;
		}
		return idList;
	}

	private Collection<DBObject> performLinkedNodesAdvancedSearch(AdvancedCriteria criteria, Schema schema, Group group){
		AdvancedCriteria.Section fromSection = criteria.getSections().get(0);
		AdvancedCriteria.Section edgeSection = criteria.getSections().get(1);
		AdvancedCriteria.Section toSection = criteria.getSections().get(2);

		ObjectDefinition fromEntity = schema.getEntity(fromSection.getEntity());
		ObjectDefinition edgeEntity = schema.getEntity(edgeSection.getEntity());
		ObjectDefinition toEntity = schema.getEntity(toSection.getEntity());

		List<Integer> searchedFromIds = makeConditionalSearch(criteria, fromSection, fromEntity, false);
		List<Integer> searchedEdgeIds = makeConditionalSearch(criteria, edgeSection, edgeEntity, false);
		List<Integer> searchedToIds = makeConditionalSearch(criteria, toSection, toEntity, false);

		log.debug("Searched from: " + searchedFromIds.size());
		log.debug("Searched edges: " + searchedEdgeIds.size());
		log.debug("Searched to: " + searchedToIds.size());

		List<Integer> scopedEdges = groupScopeProvider.getEdgeScope(group.getId());
		List<Integer> scopedNodes = groupScopeProvider.getNodeScope(group.getId());
		log.debug("Edge scope size: " + scopedEdges.size());
		log.debug("Node scope size: " + scopedNodes.size());

		if (!scopedNodes.isEmpty()){
			searchedFromIds.retainAll(scopedNodes);
			searchedToIds.retainAll(scopedNodes);
		}

		if (!scopedEdges.isEmpty())
			searchedEdgeIds.retainAll(scopedEdges);

		searchedEdgeIds = applyFilter(searchedEdgeIds, criteria, edgeEntity);
		searchedFromIds = applyFilter(searchedFromIds, criteria, fromEntity);
		searchedToIds = applyFilter(searchedToIds, criteria, toEntity);

		if (searchedEdgeIds.isEmpty() || searchedFromIds.isEmpty() || searchedToIds.isEmpty())
			return Collections.emptySet();

		GraphNi3Engine graph = GraphServlet.getGraph(schema.getId());
		searchedEdgeIds = graph.filterEdgesByFromTo(searchedEdgeIds, searchedFromIds, searchedToIds, criteria.getLimit());
		log.debug("Edges after filter: " + searchedEdgeIds.size());

		Map<Integer, DBObject> results = new HashMap<Integer, DBObject>();
		results.putAll(userDataService.getDataForIdList(edgeEntity, searchedEdgeIds));
		return results.values();
	}

	private Collection<DBObject> performNodeAdvancedSearch(AdvancedCriteria criteria, Schema schema, Group group){
		Map<Integer, DBObject> results = new HashMap<Integer, DBObject>();
		List<Integer> scopedIds = groupScopeProvider.getNodeScope(group.getId());
		log.debug("Scope for group(" + group.getId() + "): " + scopedIds.size());
		for (AdvancedCriteria.Section section : criteria.getSections()){
			ObjectDefinition entity = schema.getEntity(section.getEntity());
			List<Integer> ids = makeConditionalSearch(criteria, section, entity);
			log.debug("Search for entity: " + entity.getId() + " -> " + ids.size());
			if (!scopedIds.isEmpty())
				ids.retainAll(scopedIds);
			log.debug("Scope filtered: " + ids.size());
			if (ids.isEmpty())
				continue;

			ids = applyFilter(ids, criteria, entity);

			log.debug("After filter apply: " + ids.size());
			Map<Integer, DBObject> currentResults = userDataService.getDataForIdList(entity, ids);
			results.putAll(currentResults);
		}

		return results.values();
	}

	private List<Integer> makeConditionalSearch(AdvancedCriteria criteria, AdvancedCriteria.Section section, ObjectDefinition entity){
		return makeConditionalSearch(criteria, section, entity, true);
	}

	private List<Integer> makeConditionalSearch(AdvancedCriteria criteria, AdvancedCriteria.Section section, ObjectDefinition entity, boolean limit){
		if((criteria.getQueryType() == 2 || criteria.getQueryType() == 3) && section.getConditions().isEmpty() && section.getConditionGroups().isEmpty()){
			return getAllIds(entity);
		}
		List<Integer> ids = new ArrayList<Integer>();
		for (AdvancedCriteria.Section.ConditionGroup conditionGroup : section.getConditionGroups()){
			Attribute attribute = entity.getAttribute(conditionGroup.getAttributeId());
			String dataSource = attribute.getDataSource();
			AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(dataSource);
			log.debug("Attribute " + attribute.getName() + " -> datasource " + dataSource);
			ids.addAll(attributeDataSource.search(attribute, conditionGroup));
		}
		boolean first = true;
		for (AdvancedCriteria.Section.Condition condition : section.getConditions()){
			Attribute attribute = entity.getAttribute(condition.getAttributeId());
			String dataSource = attribute.getDataSource();
			AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(dataSource);
			log.debug("Attribute " + attribute.getName() + " -> datasource " + dataSource);
			if (first)
				ids.addAll(attributeDataSource.search(attribute, condition));
			else{
				ids.retainAll(attributeDataSource.search(attribute, condition));
				if (ids.isEmpty())//do not check any other condition couse all objects already filtered out - result set is empty
					return ids;
			}
			first = false;
		}
		if (limit && criteria.getLimit() > 0 && ids.size() > criteria.getLimit())
			ids = ids.subList(0, criteria.getLimit());
		return ids;
	}

	private List<Integer> getAllIds(ObjectDefinition entity){
		List<Integer> ids = new ArrayList<Integer>();
		for(Attribute attribute : entity.getAttributes()){
			AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(attribute.getDataSource());
			if(attributeDataSource.isPrimary()){
				ids.addAll(attributeDataSource.getIdList(entity));
				break;
			}
		}
		return ids;
	}

	private static boolean isVisibleForUser(ObjectDefinition entity, Group group){
		for (ObjectDefinitionGroup gop : entity.getObjectPermissions())
			if (gop.getGroupId() == group.getId())
				return gop.isCanRead();
		return false;
	}
}
