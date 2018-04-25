package com.ni3.ag.navigator.client.gateway;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.shared.domain.DBObject;

public interface SearchGateway{
	List<DBObject> simpleSearch(final int schemaId, final String searchForString,
										   final DataFilter preFilter);

	List<DBObject> advancedSearch(final int schemaId, final Query query, final DataFilter preFilter);

	List<DBObject> getList(final int schemaId, final Map<Integer, Collection<Integer>> missing);

	List<DBObject> getListContext(final int schemaId, final int entityId, final List<Integer> ids,
											 final int contextId, final String contextKey);

	List<DBObject> searchUnknown(int schemaId, List<Integer> ids, boolean withDeleted);
}
