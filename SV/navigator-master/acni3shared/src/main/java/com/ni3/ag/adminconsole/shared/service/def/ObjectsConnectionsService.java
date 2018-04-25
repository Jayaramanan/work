/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;

public interface ObjectsConnectionsService{

	List<ObjectDefinition> getObjectDefinitions();

	List<LineWeight> getLineWeights();

	LineWeight getDefaultLineWeight();

	List<Schema> getSchemas();

	List<ObjectDefinition> getNodeLikeObjectDefinitions();

	void save(ObjectDefinition object);

	ObjectDefinition reloadObject(Integer id);

	void updateHierarchiesSetting(ObjectConnection oc);

	boolean isHierarchicalConnection(ObjectConnection oc);

}
