/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.ThematicFolder;

public interface ThematicFolderDAO{

	List<ThematicFolder> getThematicFolders(int schemaId);

	int createThematicFolder(ThematicFolder folder);

	ThematicFolder getThematicFolder(String name, int schemaId);

}
