/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.domain.Schema;

public interface GeoAnalyticsService{

	void applyGisTerritories(List<GisTerritory> territoriesToUpdate, List<GisTerritory> territoriesToDelete);

	List<GisTerritory> getGisTerritories();

	List<Schema> getSchemas();

}
