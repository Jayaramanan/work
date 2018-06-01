package com.ni3.ag.navigator.server.services;

import java.util.Collection;

import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.server.search.ListIdCriteria;
import com.ni3.ag.navigator.server.search.SimpleCriteria;
import com.ni3.ag.navigator.shared.domain.DBObject;

public interface SearchService{
	Collection<DBObject> performSimpleSearch(SimpleCriteria simpleCriteria);

	Collection<DBObject> performAdvancedSearch(AdvancedCriteria criteria);

	Collection<DBObject> performGetList(ListIdCriteria criteria);

	Collection<DBObject> performGetListUnknown(ListIdCriteria criteria);
}
