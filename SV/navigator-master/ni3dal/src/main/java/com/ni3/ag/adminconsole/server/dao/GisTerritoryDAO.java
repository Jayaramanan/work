/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.GisTerritory;

public interface GisTerritoryDAO{

	List<GisTerritory> getGisTerritories();

	void saveOrUpdate(GisTerritory territory);

	void saveOrUpdateAll(List<GisTerritory> territoriesToUpdate);

	void deleteAll(List<GisTerritory> territoriesToDelete);

}
