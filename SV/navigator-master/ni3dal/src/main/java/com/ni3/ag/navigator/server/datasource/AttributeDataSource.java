package com.ni3.ag.navigator.server.datasource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.shared.domain.DBObject;

public interface AttributeDataSource{
	void setPrimary(boolean flag);

	boolean isPrimary();

	//TODO make search for List of attributes and criterias
	Collection<Integer> search(Attribute attribute, AdvancedCriteria.Section.Condition condition);

	//TODO make search for List of attributes and criterias
	Collection<Integer> search(Attribute attribute, AdvancedCriteria.Section.ConditionGroup conditionGroup);

	int createNode(Integer newObjectId, int entityId, int userId, Attribute attribute, String attributeValue);

	int createEdge(Integer newObjectId, int fromId, int toId, int entityId, int userId, Attribute attribute, String attributeValue);

	void saveOrUpdate(int id, Map<Attribute, String> attributeValueMap);

	void delete(int id, ObjectDefinition entity);

	void delete(List<Integer> ids, ObjectDefinition entity);

	void merge(int id, ObjectDefinition entity);

	void get(Collection<Integer> ids, List<Attribute> attributes, Map<Integer, DBObject> results);

	void get(List<Attribute> attributes, Map<Integer, DBObject> objectMap);

	void getContext(Collection<Integer> ids, Attribute pkAttribute, String contextKey, List<Attribute> attributes, Map<Integer, DBObject> results);

	String aggregate(Attribute attribute, String operation, Collection<Integer> ids);

	Collection<Integer> getNotNull(Attribute attribute);

	List<Integer> getContextEdges(int entityId, int favoriteId);

	Collection<Integer> getIdList(ObjectDefinition entity);

	void deleteContextDataByFavorite(Attribute pkAttribute, int favoriteId);

	void saveOrUpdateContext(int nodeId, Attribute pkAttribute, int topicId);

	void saveOrUpdateContextData(int nodeId, Attribute pkAttribute, int topicId, Map<Attribute, String> attributeStringMap);

	void getPredefinedOnly(Collection<Integer> ids, List<Attribute> attributes, Map<Integer, Set<Integer>> results);

	Double[] getRowMaxRowSumMaxRowSumMin(List<Attribute> attributes);
}
