/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Map;
import com.ni3.ag.adminconsole.domain.MapJob;

public interface MapJobDAO{

	void saveOrUpdate(MapJob job);

	List<MapJob> getAllJobs();

	List<MapJob> getScheduledMapJobs();

	void delete(MapJob job);

	MapJob getMapJob(Integer id);

	Map getMap(Integer mapId);

	void saveAndFlush(MapJob job);

	MapJob merge(MapJob job);

}
