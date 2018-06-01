/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.ThematicCluster;

public interface ThematicClusterDAO{

	void insertThematicCluster(ThematicCluster cluster);

	void deleteClustersByThematicMapId(int thematicMapId);

	List<ThematicCluster> getClustersByThematicMapId(int thematicMapId);

	void insertThematicClustersWithIds(List<ThematicCluster> clusters);
}
